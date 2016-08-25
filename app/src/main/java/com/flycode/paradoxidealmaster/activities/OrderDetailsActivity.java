package com.flycode.paradoxidealmaster.activities;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.constants.IntentConstants;
import com.flycode.paradoxidealmaster.model.Order;
import com.flycode.paradoxidealmaster.utils.DateUtils;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;
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

public class OrderDetailsActivity extends SuperActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {
    private static final String HAS_BEEN_ZOOMED = "hasBeenZoomed";

    private Order order;
    private MapView mapView;
    private GoogleMap googleMap;
    private Marker masterMarker;
    private Polyline polyline;
    private Location masterLocation;
    private boolean hasBeenZoomed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        order = getIntent().getParcelableExtra(IntentConstants.EXTRA_ORDER);

        if (savedInstanceState != null) {
            order = savedInstanceState.getParcelable(IntentConstants.EXTRA_ORDER);
            hasBeenZoomed = savedInstanceState.getBoolean(HAS_BEEN_ZOOMED);
        }

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);
        mapView.getMapAsync(this);

        setupActionBar();
        setupOrderDetails();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

        outState.putParcelable(IntentConstants.EXTRA_ORDER, order);
        outState.putBoolean(HAS_BEEN_ZOOMED, hasBeenZoomed);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
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
        }
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

        this.googleMap = googleMap;
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (masterLocation != null
                && masterLocation.distanceTo(location) < 50) {
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

        if (!hasBeenZoomed) {
            hasBeenZoomed = true;
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
//        actionBarBackgroundImageView.setImageResource(R.drawable.notifications_background);

        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(R.string.order);
        titleTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_MEDIUM));

        Button backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(this);
        backButton.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON));
    }

    private void setupOrderDetails() {
        View orderDetailsView;
        TextView titleTextView;
        TextView commentsTextView;
        TextView dateValueTextView;
        TextView locationValueTextView;
        TextView detailsValueTextView;

        orderDetailsView = findViewById(R.id.order_details);

        titleTextView = (TextView) orderDetailsView.findViewById(R.id.title);
        commentsTextView = (TextView) orderDetailsView.findViewById(R.id.details);

        dateValueTextView = processSection(orderDetailsView.findViewById(R.id.date_section), R.string.icon_calendar, R.string.date);
        locationValueTextView = processSection(orderDetailsView.findViewById(R.id.location_section), R.string.icon_marker, R.string.location);
        detailsValueTextView = processSection(orderDetailsView.findViewById(R.id.details_section), R.string.icon_details, R.string.details);

        titleTextView.setText(order.getServiceName());
        commentsTextView.setText(order.getDescription());
        dateValueTextView.setText(DateUtils.orderDateStringFromDate(order.getOrderTime()));

        String location = order.getLocationName();
        locationValueTextView.setText(location == null || location.isEmpty() ? "N/A" : location);

        String details;

        if (order.isServiceIsCountable()) {
            details = order.getStatus() + " " + order.getServiceCost() + "AMD";
        } else {
            details = order.getStatus() + " " + order.getQuantity() + " / " + (order.getQuantity() * order.getServiceCost()) + "AMD";
        }

        detailsValueTextView.setText(details);
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
}
