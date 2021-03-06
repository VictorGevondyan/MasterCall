package com.idealsystems.idealmaster.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.idealsystems.idealmaster.R;
import com.idealsystems.idealmaster.api.APIBuilder;
import com.idealsystems.idealmaster.api.response.OrderResponse;
import com.idealsystems.idealmaster.api.response.SimpleOrderResponse;
import com.idealsystems.idealmaster.constants.IntentConstants;
import com.idealsystems.idealmaster.constants.OrderActionConstants;
import com.idealsystems.idealmaster.constants.OrderStatusConstants;
import com.idealsystems.idealmaster.dialogs.LoadingProgressDialog;
import com.idealsystems.idealmaster.model.Order;
import com.idealsystems.idealmaster.settings.AppSettings;
import com.idealsystems.idealmaster.settings.UserData;
import com.idealsystems.idealmaster.utils.DateUtils;
import com.idealsystems.idealmaster.utils.DeviceUtil;
import com.idealsystems.idealmaster.utils.ErrorNotificationUtil;
import com.idealsystems.idealmaster.utils.TypefaceLoader;
import com.idealsystems.idealmaster.views.CircleView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends SuperActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnMyLocationButtonClickListener {
    private static final String HAS_SHOWN_PATH = "hasShownPath";
    private static final String HAS_SHOWN_DESTINATION = "hasShownDestination";
    private static final String MAP_VIEW_BUNDLE = "mapViewBundle";

    private LoadingProgressDialog loading;

    private Order order;
    private MapView mapView;
    private GoogleMap googleMap;
    private Marker masterMarker;
    private Polyline polyline;
    private Location masterLocation;
    private Button leftButton;
    private Button rightButton;
    private TextView statusValueTextView;
    private Timer timer;
    private boolean hasShownPath;
    private boolean hasShownDestination;
    private Button buttonPhoneOrderDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        loading = new LoadingProgressDialog(this);

        order = getIntent().getParcelableExtra(IntentConstants.EXTRA_ORDER);

        Bundle mapViewBundle = new Bundle();

        if (savedInstanceState != null) {
            order = savedInstanceState.getParcelable(IntentConstants.EXTRA_ORDER);
            hasShownPath = savedInstanceState.getBoolean(HAS_SHOWN_PATH);
            hasShownDestination = savedInstanceState.getBoolean(HAS_SHOWN_DESTINATION);
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE);
        }


        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(mapViewBundle);
        MapsInitializer.initialize(this);

        if (order.getStatus().equals(OrderStatusConstants.FINISHED)) {
            mapView.setVisibility(View.INVISIBLE);
        } else {
            mapView.getMapAsync(this);
        }

        leftButton = (Button) findViewById(R.id.left_button);
        rightButton = (Button) findViewById(R.id.right_button);

        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);

        setupActionBar();
        setupOrderDetails();
        reloadOrderUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = new Bundle();
        mapView.onSaveInstanceState(mapViewBundle);

        outState.putParcelable(IntentConstants.EXTRA_ORDER, order);
        outState.putBoolean(HAS_SHOWN_PATH, hasShownPath);
        outState.putBoolean(HAS_SHOWN_DESTINATION, hasShownDestination);
        outState.putBundle(MAP_VIEW_BUNDLE, mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(order.getId(), 0);
        getOrderFromServer();

        if (!order.getStatus().equals(OrderStatusConstants.FINISHED)
                && !order.getStatus().equals(OrderStatusConstants.CANCELED)) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    getOrderFromServer();
                }
            }, 5000, 5000);
        }

        // Check for GPS permission and availability

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 506);
        }

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new MaterialDialog.Builder(this)
                    .title(R.string.error)
                    .content(R.string.turn_on_gps)
                    .positiveText(R.string.settings)
                    .negativeText(R.string.ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);

                            dialog.dismiss();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_down_out);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            onBackPressed();
        } else if (view.getId() == R.id.left_button) {
            onLeftButtonClicked();
        } else if (view.getId() == R.id.right_button) {
            onRightButtonClicked();
        } else if (view.getId() == R.id.button_phone_order_details) {
            if (order.getUserPhone() == null || order.getUserPhone().length() == 0) {
                new MaterialDialog.Builder(OrderDetailsActivity.this)
                        .title(R.string.error)
                        .content(R.string.user_has_no_phone)
                        .positiveText(R.string.ok)
                        .show();
            } else {
                String phoneNumber = order.getUserPhone();

                if (!phoneNumber.startsWith("+")) {
                    phoneNumber = "+" + phoneNumber;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, 505);
                    return;
                }

                try {
                    startActivity(callIntent);
                } catch (Exception e) {
                    Log.i("TAG", e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onOrderStartedReceived(Order order) {
        this.order = order;
        reloadOrderUI();
    }

    @Override
    public void onOrderOfferedReceived(Order order) {
        this.order = order;
        reloadOrderUI();
    }

    @Override
    public void onOrderFinishedReceived(Order order) {
        this.order = order;
        reloadOrderUI();
    }

    @Override
    public void onOrderCanceledReceived(Order order) {
        this.order = order;
        reloadOrderUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMyLocationChangeListener(this);

        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (order.getStatus().equals(OrderStatusConstants.FINISHED)
                || order.getStatus().equals(OrderStatusConstants.CANCELED)) {
            return;
        }

        int mapLocationPadding = (int) DeviceUtil.getPxForDp(OrderDetailsActivity.this, 48);

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setPadding(0, mapLocationPadding, 0, 0);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(order.getLocationLatitude(), order.getLocationLongitude()));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_user));
        googleMap.addMarker(markerOptions);

        if (!hasShownDestination) {
            hasShownDestination = true;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(order.getLocationLatitude(), order.getLocationLongitude()), 16));
        }

        this.googleMap = googleMap;
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (order.getStatus().equals(OrderStatusConstants.FINISHED)
                || order.getStatus().equals(OrderStatusConstants.CANCELED)) {
            return;
        }

        if (location.getAccuracy() > 20) {
            return;
        }

        if (masterLocation != null
                && masterLocation.distanceTo(location) < 30) {
            return;
        }

        masterLocation = location;

        if (masterMarker != null) {
            masterMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        } else {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_master));
            masterMarker = googleMap.addMarker(markerOptions);
        }

        if (!hasShownPath) {
            hasShownPath = true;
            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(new LatLng(location.getLatitude(), location.getLongitude()))
                    .include(new LatLng(order.getLocationLatitude(), order.getLocationLongitude()))
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 50);
            googleMap.moveCamera(cameraUpdate);
        }

        GoogleDirection
                .withServerKey("AIzaSyCu9vHQPhi4y_-Pdj5m5CZ8_YYtFak7xC8")
                .from(new LatLng(location.getLatitude(), location.getLongitude()))
                .to(new LatLng(order.getLocationLatitude(), order.getLocationLongitude()))
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {

                            Route route = direction.getRouteList().get(0);

                            if (polyline != null) {
                                polyline.setPoints(route.getOverviewPolyline().getPointList());
                            } else {
                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.addAll(route.getOverviewPolyline().getPointList());
                                polylineOptions.color(Color.RED);
                                polylineOptions.width(4);
                                polyline = googleMap.addPolyline(polylineOptions);
                            }
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.d("Directions Failed", "YUHU!");
                    }
                });
    }

    private void getOrderFromServer() {
        APIBuilder
                .getIdealAPI()
                .getOrder(
                        AppSettings.sharedSettings(this).getBearerToken(),
                        order.getId()
                )
                .enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        loading.dismiss();

                        if (order.getUpdated().after(response.body().getUpdated())) {
//                            ErrorNotificationUtil.showErrorForCode(response.code(), OrderDetailsActivity.this);
                            return;
                        }


                        final Order order = Order.fromResponse(response.body());

                        if (order != null
                                && order.getMasterId() != null
                                && !order.getMasterId().equals(UserData.sharedData(OrderDetailsActivity.this).getId())) {
                            if (timer != null) {
                                timer.cancel();
                                timer.purge();
                            }

                            new MaterialDialog.Builder(OrderDetailsActivity.this)
                                    .title(R.string.error)
                                    .content(R.string.already_granted)
                                    .positiveText(R.string.ok)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Realm
                                                    .getDefaultInstance()
                                                    .executeTransaction(new Realm.Transaction() {
                                                        @Override
                                                        public void execute(Realm realm) {
                                                            Order realmOrder = realm.where(Order.class).equalTo("id", order.getId()).findFirst();

                                                            if (realmOrder != null) {
                                                                realmOrder.deleteFromRealm();
                                                            }
                                                        }
                                                    });

                                            finish();
                                        }
                                    })
                                    .show();

                            return;
                        }

                        OrderDetailsActivity.this.order = order;

                        if (OrderDetailsActivity.this.order != null
                                && !OrderDetailsActivity.this.order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                                && !OrderDetailsActivity.this.order.getStatus().equals(OrderStatusConstants.NOT_TAKEN_MASTER_ATTACHED)
                                && !OrderDetailsActivity.this.order.getStatus().equals(OrderStatusConstants.WAITING_FAVORITE)) {
                            Realm
                                    .getDefaultInstance()
                                    .executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.insertOrUpdate(OrderDetailsActivity.this.order);
                                        }
                                    });
                        }

                        reloadOrderUI();
                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {
                        loading.dismiss();
//                        ErrorNotificationUtil.showErrorForCode(0, OrderDetailsActivity.this);
                    }
                });
    }

    private void setupActionBar() {
        ImageView actionBarBackgroundImageView = (ImageView) findViewById(R.id.action_background);
        actionBarBackgroundImageView.setImageResource(R.drawable.new_orders_background);

        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(R.string.order);
        titleTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_MEDIUM, this));

        Button backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(this);
        backButton.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON, this));
    }

    private void setupOrderDetails() {
        View orderDetailsView = findViewById(R.id.order_details);

        TextView titleTextView = (TextView) orderDetailsView.findViewById(R.id.title);
        TextView commentsTextView = (TextView) orderDetailsView.findViewById(R.id.details);
        TextView commentsIconTextView = (TextView) orderDetailsView.findViewById(R.id.details_icon);

        TextView dateValueTextView = processSection(orderDetailsView.findViewById(R.id.date_section), R.string.icon_calendar, R.string.date);
        TextView locationValueTextView = processSection(orderDetailsView.findViewById(R.id.location_section), R.string.icon_marker, R.string.location);
        statusValueTextView = processSection(orderDetailsView.findViewById(R.id.status_section), R.string.icon_details, R.string.status);
        TextView costValueTextView = processSection(orderDetailsView.findViewById(R.id.cost_section), R.string.icon_cost, R.string.cost);

        titleTextView.setText(order.getTranslatedServiceName(this));
        commentsTextView.setText(order.getDescription());
        dateValueTextView.setText(DateUtils.infoDateStringFromDate(order.getOrderTime()));

        titleTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_BOOK, this));
        commentsTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_LIGHT, this));
        commentsIconTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON, this));

        buttonPhoneOrderDetails = (Button) findViewById(R.id.button_phone_order_details);
        buttonPhoneOrderDetails.setOnClickListener(this);


        switch (order.getStatus()) {
            case OrderStatusConstants.PAUSED:
            case OrderStatusConstants.STARTED:
            case OrderStatusConstants.WAITING_PAUSED:
            case OrderStatusConstants.FINISHED_WAITING_PAYMENT:
            case OrderStatusConstants.WAITING_FINISHED:
                buttonPhoneOrderDetails.setVisibility(View.VISIBLE);
                break;
            default:
                buttonPhoneOrderDetails.setVisibility(View.GONE);
                break;
        }

        if (order.getDescription() == null || order.getDescription().isEmpty()) {
            commentsIconTextView.setVisibility(View.GONE);
            commentsTextView.setVisibility(View.GONE);
        }

        String location = order.getLocationName();
        locationValueTextView.setText(location == null || location.isEmpty() ? "N/A" : location);

        String cost;

        if (!order.isServiceIsCountable()) {
            cost = order.getServiceCost() + " " + getString(R.string.amd);
        } else {
            cost = order.getQuantity() + " " + order.getTranslatedServiceUnit(this) + " / " + (order.getQuantity() * order.getServiceCost()) + " " + getString(R.string.amd);
        }

        costValueTextView.setText(cost);

        CircleView balloonCircleView = (CircleView) orderDetailsView.findViewById(R.id.balloon);
        CircleView balloonOutlineCircleView = (CircleView) orderDetailsView.findViewById(R.id.balloon_outline);

        try {
            balloonCircleView.setBackgroundColor(Color.parseColor(order.getServiceColor()));
            balloonOutlineCircleView.setBackgroundColor(Color.parseColor(order.getServiceColor()));
        } catch (Exception e) {

        }

        balloonOutlineCircleView.setIsOutlineOnly(true);
    }

    private void setOrderStatus() {
        if (order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.NOT_TAKEN_MASTER_ATTACHED)) {
            statusValueTextView.setText(R.string.not_taken);
        } else if (order.getStatus().equals(OrderStatusConstants.STARTED)) {
            statusValueTextView.setText(R.string.started);
        } else if (order.getStatus().equals(OrderStatusConstants.PAUSED)) {
            statusValueTextView.setText(R.string.paused);
        } else if (order.getStatus().equals(OrderStatusConstants.CANCELED)) {
            statusValueTextView.setText(R.string.canceled);
        } else if (order.getStatus().equals(OrderStatusConstants.FINISHED)) {
            statusValueTextView.setText(R.string.finished);
        } else if (order.getStatus().equals(OrderStatusConstants.WAITING_FINISHED)
                || order.getStatus().equals(OrderStatusConstants.FINISHED_WAITING_PAYMENT)) {
            statusValueTextView.setText(R.string.waiting_finished);
        } else if (order.getStatus().equals(OrderStatusConstants.WAITING_PAUSED)) {
            statusValueTextView.setText(R.string.waiting_paused);
        } else if (order.getStatus().equals(OrderStatusConstants.WAITING_FAVORITE)) {
            statusValueTextView.setText(R.string.waiting_favorite);
        }
    }

    private TextView processSection(View section, int icon, int title) {
        TextView iconTextView = (TextView) section.findViewById(R.id.icon);
        TextView titleTextView = (TextView) section.findViewById(R.id.title);
        TextView valueTextView = (TextView) section.findViewById(R.id.value);

        iconTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON, this));
        titleTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_BOOK, this));
        valueTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_BOOK, this));

        iconTextView.setText(icon);
        titleTextView.setText(title);

        return valueTextView;
    }

    private void reloadOrderUI() {
        if (order.getStatus().equals(OrderStatusConstants.FINISHED)
                || order.getStatus().equals(OrderStatusConstants.CANCELED)) {
            leftButton.setVisibility(View.GONE);
            rightButton.setVisibility(View.GONE);
            mapView.setVisibility(View.GONE);
        } else if (order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.NOT_TAKEN_MASTER_ATTACHED)) {
            leftButton.setText("");
            rightButton.setText(R.string.take);
        } else if (order.getStatus().equals(OrderStatusConstants.WAITING_FAVORITE)) {
            leftButton.setText("");

            if (order.getChosenFavorite() == null || order.getChosenFavorite().equals(UserData.sharedData(this).getId())) {
                rightButton.setText(R.string.accept);
            } else {
                rightButton.setText(R.string.take);
            }
        } else if (order.getStatus().equals(OrderStatusConstants.STARTED)
                || order.getStatus().equals(OrderStatusConstants.WAITING_PAUSED)
                || order.getStatus().equals(OrderStatusConstants.WAITING_FINISHED)
                || order.getStatus().equals(OrderStatusConstants.FINISHED_WAITING_PAYMENT)) {
            leftButton.setText(R.string.pause);
            rightButton.setText(R.string.finish);
        } else if (order.getStatus().equals(OrderStatusConstants.PAUSED)) {
            leftButton.setText(R.string.start);
            rightButton.setText(R.string.finish);
        }

        setOrderStatus();
    }

    private void onLeftButtonClicked() {
        String action;
        final int successTitle;
        final int successMessage;

        if (order.getStatus().equals(OrderStatusConstants.STARTED)
                || order.getStatus().equals(OrderStatusConstants.WAITING_PAUSED)
                || order.getStatus().equals(OrderStatusConstants.WAITING_FINISHED)
                || order.getStatus().equals(OrderStatusConstants.FINISHED_WAITING_PAYMENT)) {
            action = OrderActionConstants.PAUSE_REQUEST;
            successTitle = R.string.pause_request_sent;
            successMessage = R.string.pause_request_sent_long;
        } else if (order.getStatus().equals(OrderStatusConstants.PAUSED)) {
            action = OrderActionConstants.START;
            successTitle = R.string.order_was_started;
            successMessage = R.string.order_was_started_long;
        } else {
            return;
        }

        loading.show();

        APIBuilder
                .getIdealAPI()
                .makeOrderAction(
                        AppSettings.sharedSettings(this).getBearerToken(),
                        order.getId(),
                        action
                )
                .enqueue(new Callback<SimpleOrderResponse>() {
                    @Override
                    public void onResponse(Call<SimpleOrderResponse> call, Response<SimpleOrderResponse> response) {
                        loading.dismiss();

                        if (!response.isSuccessful()) {
                            if (response.code() == 430) {
                                new MaterialDialog.Builder(OrderDetailsActivity.this)
                                        .title(R.string.error)
                                        .content(R.string.order_is_canceled)
                                        .positiveText(R.string.ok)
                                        .show();

                                return;
                            }

                            ErrorNotificationUtil.showErrorForCode(response.code(), OrderDetailsActivity.this);

                            return;
                        }

                        order.mergeSimpleResponse(response.body());

                        Realm
                                .getDefaultInstance()
                                .executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.insertOrUpdate(order);
                                    }
                                });

                        new MaterialDialog.Builder(OrderDetailsActivity.this)
                                .title(successTitle)
                                .content(successMessage)
                                .positiveText(R.string.ok)
                                .show();

                        reloadOrderUI();

                    }

                    @Override
                    public void onFailure(Call<SimpleOrderResponse> call, Throwable t) {
                        ErrorNotificationUtil.showErrorForCode(0, OrderDetailsActivity.this);
                        loading.dismiss();
                    }
                });
    }

    private void onRightButtonClicked() {
        String action;
        final int successTitle;
        final int successMessage;

        loading.show();

        if (order.getStatus().equals(OrderStatusConstants.WAITING_FAVORITE)
                && order.getChosenFavorite() != null && order.getChosenFavorite().equals(UserData.sharedData(this).getId())) {
            action = OrderActionConstants.ACCEPT_FAVORITE;
            successTitle = R.string.accept_request_sent;
            successMessage = R.string.accept_request_sent_long;
        } else if (order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.NOT_TAKEN_MASTER_ATTACHED)
                || order.getStatus().equals(OrderStatusConstants.WAITING_FAVORITE)) {
            action = OrderActionConstants.ATTACH_MASTER;
            successTitle = R.string.take_request_sent;
            successMessage = R.string.take_request_sent_long;
        } else {
            action = OrderActionConstants.FINISH_REQUEST;
            successTitle = R.string.finish_request_sent;
            successMessage = R.string.finish_request_sent_long;
        }

        APIBuilder
                .getIdealAPI()
                .makeOrderAction(
                        AppSettings.sharedSettings(this).getBearerToken(),
                        order.getId(),
                        action

                )
                .enqueue(new Callback<SimpleOrderResponse>() {
                    @Override
                    public void onResponse(Call<SimpleOrderResponse> call, Response<SimpleOrderResponse> response) {
                        loading.dismiss();

                        if (!response.isSuccessful()) {
                            if (response.code() == 430) {
                                new MaterialDialog.Builder(OrderDetailsActivity.this)
                                        .title(R.string.cant_take_order)
                                        .content(R.string.already_granted)
                                        .positiveText(R.string.ok)
                                        .show();
                            } else if (response.code() == 431) {
                                new MaterialDialog.Builder(OrderDetailsActivity.this)
                                        .title(R.string.cant_take_order)
                                        .content(R.string.already_taken)
                                        .positiveText(R.string.ok)
                                        .show();
                            } else {
                                ErrorNotificationUtil.showErrorForCode(response.code(), OrderDetailsActivity.this);
                            }

                            return;
                        }

                        order.mergeSimpleResponse(response.body());

                        Realm
                                .getDefaultInstance()
                                .executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.insertOrUpdate(order);
                                    }
                                });

                        new MaterialDialog.Builder(OrderDetailsActivity.this)
                                .title(successTitle)
                                .content(successMessage)
                                .positiveText(R.string.ok)
                                .show();

                        reloadOrderUI();
                    }

                    @Override
                    public void onFailure(Call<SimpleOrderResponse> call, Throwable t) {
                        loading.dismiss();
                        ErrorNotificationUtil.showErrorForCode(0, OrderDetailsActivity.this);
                    }
                });
    }

    public String getOrderId() {
        return order.getId();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 506 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (googleMap.getMyLocation() == null) {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                new MaterialDialog.Builder(this)
                        .title(R.string.error)
                        .content(R.string.turn_on_gps)
                        .positiveText(R.string.settings)
                        .negativeText(R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

            return true;
        }

        return false;
    }
}
