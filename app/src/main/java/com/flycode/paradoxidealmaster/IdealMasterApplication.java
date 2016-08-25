package com.flycode.paradoxidealmaster;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.response.OrderResponse;
import com.flycode.paradoxidealmaster.api.response.OrdersListResponse;
import com.flycode.paradoxidealmaster.model.IdealService;
import com.flycode.paradoxidealmaster.model.Order;
import com.flycode.paradoxidealmaster.settings.AppSettings;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anhaytananun on 15.06.16.
 */
public class IdealMasterApplication extends Application {
    private static IdealMasterApplication application;

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
    }
}
