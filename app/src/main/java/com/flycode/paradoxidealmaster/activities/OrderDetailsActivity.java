package com.flycode.paradoxidealmaster.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.response.OrderResponse;
import com.flycode.paradoxidealmaster.api.response.SimpleOrderResponse;
import com.flycode.paradoxidealmaster.constants.IntentConstants;
import com.flycode.paradoxidealmaster.constants.OrderActionConstants;
import com.flycode.paradoxidealmaster.constants.OrderStatusConstants;
import com.flycode.paradoxidealmaster.dialogs.LoadinProgressDialog;
import com.flycode.paradoxidealmaster.model.Order;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.utils.DateUtils;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;
import com.flycode.paradoxidealmaster.views.CircleView;
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

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends SuperActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {
    private static final String HAS_SHOWN_PATH = "hasShownPath";
    private static final String HAS_SHOWN_DESTINATION = "hasShownDestination";
    private static final String MAP_VIEW_BUNDLE = "mapViewBundle";

    private LoadinProgressDialog loading;

    private Order order;
    private MapView mapView;
    private GoogleMap googleMap;
    private Marker masterMarker;
    private Polyline polyline;
    private Location masterLocation;
    private Button leftButton;
    private Button rightButton;
    private TextView statusValueTextView;
    private boolean hasShownPath;
    private boolean hasShownDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        loading = new LoadinProgressDialog(this);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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
        mapView.getMapAsync(this);

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

        APIBuilder
                .getIdealAPI()
                .getOrder(
                        AppSettings.sharedSettings(this).getBearerToken(),
                        order.getId()
                )
                .enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        if (order.getUpdated().after(response.body().getUpdated())) {
                            return;
                        }

                        order = Order.fromResponse(response.body());

                        Realm
                                .getDefaultInstance()
                                .executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.insertOrUpdate(order);
                                    }
                                });

                        reloadOrderUI();
                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {

                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
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
        }
    }

    @Override
    public void onOrderStartedReceived(Order order) {
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

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setPadding(0, 100, 0, 0);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(order.getLocationLatitude(), order.getLocationLongitude()));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_user));
        googleMap.addMarker(markerOptions);

        if (!hasShownDestination) {
            hasShownDestination = true;
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(order.getLocationLatitude(), order.getLocationLongitude())));
        }

        this.googleMap = googleMap;
    }

    @Override
    public void onMyLocationChange(Location location) {
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
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 200);
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

    private void setupActionBar() {
        ImageView actionBarBackgroundImageView = (ImageView) findViewById(R.id.action_background);
        actionBarBackgroundImageView.setImageResource(R.drawable.new_orders_background);

        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(R.string.order);
        titleTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_MEDIUM));

        Button backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(this);
        backButton.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON));
    }

    private void setupOrderDetails() {
        View orderDetailsView = findViewById(R.id.order_details);

        TextView titleTextView = (TextView) orderDetailsView.findViewById(R.id.title);
        TextView commentsTextView = (TextView) orderDetailsView.findViewById(R.id.details);

        TextView dateValueTextView = processSection(orderDetailsView.findViewById(R.id.date_section), R.string.icon_calendar, R.string.date);
        TextView locationValueTextView = processSection(orderDetailsView.findViewById(R.id.location_section), R.string.icon_marker, R.string.location);
        statusValueTextView = processSection(orderDetailsView.findViewById(R.id.status_section), R.string.icon_details, R.string.status);
        TextView costValueTextView = processSection(orderDetailsView.findViewById(R.id.cost_section), R.string.icon_cost, R.string.cost);

        titleTextView.setText(order.getServiceName());
        commentsTextView.setText(order.getDescription());
        dateValueTextView.setText(DateUtils.infoDateStringFromDate(order.getOrderTime()));

        if (order.getDescription() == null || order.getDescription().isEmpty()) {
            commentsTextView.setVisibility(View.GONE);
        }

        String location = order.getLocationName();
        locationValueTextView.setText(location == null || location.isEmpty() ? "N/A" : location);

        String cost;

        if (order.isServiceIsCountable()) {
            cost = order.getServiceCost() + "AMD";
        } else {
            cost = order.getQuantity() + " / " + (order.getQuantity() * order.getServiceCost()) + "AMD";
        }

        costValueTextView.setText(cost);

        CircleView balloonCircleView = (CircleView) orderDetailsView.findViewById(R.id.balloon);
        CircleView balloonOutlineCircleView = (CircleView) orderDetailsView.findViewById(R.id.balloon_outline);

        balloonCircleView.setBackgroundColor(Color.GREEN);
        balloonOutlineCircleView.setBackgroundColor(Color.GREEN);
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
        } else if (order.getStatus().equals(OrderStatusConstants.WAITING_FINISHED)) {
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

        iconTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON));
        titleTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_BOOK));
        valueTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_BOOK));

        iconTextView.setText(icon);
        titleTextView.setText(title);

        return valueTextView;
    }

    private void reloadOrderUI() {
        if (order.getStatus().equals(OrderStatusConstants.FINISHED)
                || order.getStatus().equals(OrderStatusConstants.CANCELED)) {
            leftButton.setVisibility(View.GONE);
            rightButton.setVisibility(View.GONE);
        } else if (order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.NOT_TAKEN_MASTER_ATTACHED)) {
            leftButton.setText("");
            rightButton.setText(R.string.take);
        } else if (order.getStatus().equals(OrderStatusConstants.WAITING_FAVORITE)) {
            leftButton.setText(R.string.decline);
            rightButton.setText(R.string.take);
        } else if (order.getStatus().equals(OrderStatusConstants.STARTED)
                || order.getStatus().equals(OrderStatusConstants.WAITING_PAUSED)
                || order.getStatus().equals(OrderStatusConstants.WAITING_FINISHED)) {
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

        loading.show();

        if (order.getStatus().equals(OrderStatusConstants.STARTED)
                || order.getStatus().equals(OrderStatusConstants.WAITING_PAUSED)
                || order.getStatus().equals(OrderStatusConstants.WAITING_FINISHED)) {
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
                        if (!response.isSuccessful()) {
                            return;
                        }

                        order.mergeSimpleResponse(response.body());
                        loading.dismiss();

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
                        Log.d("Order update", "fuck you");
                        loading.dismiss();
                    }
                });
    }

    private void onRightButtonClicked() {
        String action;
        final int successTitle;
        final int successMessage;

        loading.show();

        if (order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.NOT_TAKEN_MASTER_ATTACHED)) {
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
                        if (!response.isSuccessful()) {
                            return;
                        }

                        order.mergeSimpleResponse(response.body());
                        loading.dismiss();

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
                        Log.d("Order update", "fuck you");
                        loading.dismiss();
                    }
                });
    }

    public String getOrderId() {
        return order.getId();
    }
}
