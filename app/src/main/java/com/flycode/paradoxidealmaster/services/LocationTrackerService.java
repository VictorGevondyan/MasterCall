package com.flycode.paradoxidealmaster.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.body.LocationBody;
import com.flycode.paradoxidealmaster.settings.AppSettings;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by victor on 12/8/15.
 */
public class LocationTrackerService extends Service implements LocationListener {
    public static final String LOCATION_BROADCAST  = LocationTrackerService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_ACCURACY = "extraAccuracy";
    public static final String EXTRA_DISTANCE = "extraDistance";
    public static final String EXTRA_FROM_LOCATION = "extraFromLocation";
    public static final String EXTRA_TO_LOCATION = "extraToLocation";
    public static final String EXTRA_ELAPSED_DISTANCE = "extraElapsedDistance";
    public static final String EXTRA_IS_GPS_ENABLED = "extraIsGPSEnabled";

    public static final String ACTION_GPS_STATUS = LocationTrackerService.class.getName() + "actionGPSStatus";

    private static final Object LOCK = new Object();

    private static final float LOCATION_REFRESH_DISTANCE = 500;
    private static final long LOCATION_REFRESH_TIME = 10 * 60 * 1000;

    private Location location;

    private boolean networkEnabled;
    private boolean gpsEnabled;

    private boolean ignoreFlight;

    private int timerRejectCounter;

    private Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timerRejectCounter = 0;

        // Start Foreground service
//        Notification notification = new NotificationCompat.Builder(this)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle("Taxi Driver Application")
//            .setContentText("Your location is being tracked")
//            .build();
//        Intent activityIntent = new Intent(this, MainActivity.class);
//
//        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        notification.flags|=Notification.FLAG_NO_CLEAR;
//
//        startForeground(1337, notification);

        // Start tracking on user location

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return START_STICKY;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, this);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pingServer(location);
            }
        }, 120000, 120000);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            super.onDestroy();

            startService(new Intent(this, LocationTrackerService.class));

            return;
        }

        locationManager.removeUpdates(this);

        super.onDestroy();

        timer.cancel();
        timer.purge();

        if (AppSettings.sharedSettings(this).isUserLoggedIn()) {
            startService(new Intent(this, LocationTrackerService.class));
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    private void pingServer(Location location) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (AppSettings.sharedSettings(this).isUserLoggedIn() && isConnected && location != null) {
            APIBuilder
                    .getIdealAPI()
                    .updateLocation(
                            AppSettings.sharedSettings(this).getBearerToken(),
                            new LocationBody(location.getLatitude(), location.getLongitude(), this)
                    ).enqueue(null);
        }
    }

    /**
     * LocationListener Methods
     */

    @Override
    public void onLocationChanged(final Location location) {
        synchronized (LOCK) {
            computeDistance(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {

    }

    @Override
    public void onProviderDisabled(String providerName) {
        if (providerName.equals(LocationManager.GPS_PROVIDER)) {
            gpsEnabled = false;
        } else if (providerName.equals(LocationManager.NETWORK_PROVIDER)) {
            networkEnabled = false;
        }

        if (!gpsEnabled) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_IS_GPS_ENABLED, false);
            intent.setAction(ACTION_GPS_STATUS);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onProviderEnabled(String providerName) {
        if (!gpsEnabled && !networkEnabled) {
            ignoreFlight = true;
        }

        if (providerName.equals(LocationManager.GPS_PROVIDER)) {
            gpsEnabled = true;
        } else if (providerName.equals(LocationManager.NETWORK_PROVIDER)) {
            networkEnabled = true;
        }

        if (gpsEnabled) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_IS_GPS_ENABLED, true);
            intent.setAction(ACTION_GPS_STATUS);
            sendBroadcast(intent);
        }
    }

    /**
     * Location Processor Methods
     */

    public void computeDistance(final Location location) {
        // Check if location has desired accuracy
        if (location.getAccuracy() > 30) {
            return;
        }

        this.location = location;

        pingServer(location);
    }

    public void sendLocationBroadcast(double accuracy, double distance, double elapsedDistance, Location from, Location to) {
        Intent locationBroadcastIntent = new Intent(LOCATION_BROADCAST);
        locationBroadcastIntent.putExtra(EXTRA_ACCURACY, accuracy);
        locationBroadcastIntent.putExtra(EXTRA_DISTANCE, distance);
        locationBroadcastIntent.putExtra(EXTRA_ELAPSED_DISTANCE, elapsedDistance);
        locationBroadcastIntent.putExtra(EXTRA_FROM_LOCATION, from);
        locationBroadcastIntent.putExtra(EXTRA_TO_LOCATION, to);

        sendBroadcast(locationBroadcastIntent);
    }
}
