package com.flycode.paradoxidealmaster;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.body.LocationBody;
import com.flycode.paradoxidealmaster.api.response.OrderResponse;
import com.flycode.paradoxidealmaster.api.response.OrdersListResponse;
import com.flycode.paradoxidealmaster.constants.IntentConstants;
import com.flycode.paradoxidealmaster.model.IdealService;
import com.flycode.paradoxidealmaster.model.Order;
import com.flycode.paradoxidealmaster.model.User;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.settings.UserData;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anhaytananun on 15.06.16.
 */
public class IdealMasterApplication extends Application implements LocationListener {
    private static IdealMasterApplication application;

    private Location lastLocation;

    public static IdealMasterApplication sharedApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("IdealRealm")
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        updateServices();
        updateLocations();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pingServer(lastLocation);
            }
        }, 120000, 120000);
    }

    public void updateServices() {
        if (AppSettings.sharedSettings(this).isUserLoggedIn()) {
            APIBuilder
                    .getIdealAPI()
                    .getServices(
                            AppSettings.sharedSettings(this).getBearerToken()
                    )
                    .enqueue(new Callback<ArrayList<IdealService>>() {
                        @Override
                        public void onResponse(Call<ArrayList<IdealService>> call, final Response<ArrayList<IdealService>> response) {
                            Realm
                                    .getDefaultInstance()
                                    .executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.insertOrUpdate(response.body());
                                        }
                                    });
                        }

                        @Override
                        public void onFailure(Call<ArrayList<IdealService>> call, Throwable t) {
                        }
                    });
        }
    }

    public void updateLocations() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50,
                600000, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50,
                600000, this);
    }

    @Override
    public void onLocationChanged(Location location) {
//        if (location.getAccuracy() > 20) {
//            return;
//        }

        lastLocation = location;

        pingServer(lastLocation);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void pingServer(Location location) {
        if (location == null) {
            return;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (AppSettings.sharedSettings(this).isUserLoggedIn()
                && isConnected && location != null) {
            APIBuilder
                    .getIdealAPI()
                    .updateLocation(
                            AppSettings.sharedSettings(this).getBearerToken(),
                            new LocationBody(location.getLatitude(), location.getLongitude(), this)
                    ).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("PING CODE", String.valueOf(response.code()));
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("PING CODE", "FAILURE");
                }
            });
        }
    }
}