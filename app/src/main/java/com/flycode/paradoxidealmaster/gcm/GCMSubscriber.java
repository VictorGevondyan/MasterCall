package com.flycode.paradoxidealmaster.gcm;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.body.GCMBody;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GCMSubscriber {
    public static void registerForGcm(final Activity activity) throws IOException {
        if (!GCMUtils.checkPlayServices(activity)) {
            return;
        }

        GCMUtils.gcm = GoogleCloudMessaging.getInstance(activity.getApplicationContext());

        String registrationId = GCMUtils.getRegistrationId(activity);

        if (registrationId.isEmpty()) {
            registerBackground(activity);
        } else {
            sendingToServer(registrationId, activity);
        }
    }

    private static void registerBackground(final Context context) {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... asyncParams) {
                String registrationId;
                try {
                    GCMUtils.gcm.unregister();
                    registrationId = GCMUtils.gcm.register(GCMUtils.SENDER_ID);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "wtf";
                }

                return registrationId;
            }

            @Override
            protected void onPostExecute(final String registrationId) {
                if (registrationId.equals("wtf")) {
                    return;
                }

                sendingToServer(registrationId, context);
            }
        }.execute(null, null, null);
    }

    public static void sendingToServer(final String registrationId, final Context context) {
        APIBuilder
                .getIdealAPI()
                .registerGCMToken(
                        AppSettings.sharedSettings(context).getBearerToken(),
                        new GCMBody(registrationId, context)
                ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    GCMUtils.storeRegistrationId(context,registrationId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
