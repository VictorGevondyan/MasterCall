package com.flycode.paradoxidealmaster.api;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;

import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.settings.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by victor on 12/14/15.
 */
public class APITalker {
    // Url constants
    private final String BASE_URL = "http://192.168.0.111:1999";
    private final String BASE_API_URL = BASE_URL+ "/api";
    private final String LOGIN_URL = "/auth/local";
    private final String DEVICES_URL = "/devices";
    private final String POINTS_URL = "/points";
    private final String ME_URL = "/me";
    private final String USERS_URL = "/users";

    /**
	 * Singletone
	 */

    private static APITalker apiTalker = null;

    private AsyncHttpClient asyncHttpClient;

    private APITalker() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setMaxRetriesAndTimeout(0, 10000);
        asyncHttpClient.setConnectTimeout(10000);
            asyncHttpClient.setResponseTimeout(10000);
    }

    public static APITalker sharedTalker() {
        if (apiTalker == null) {
            apiTalker = new APITalker();
        }

        return apiTalker;
    }

    /**
     * Public Methods
     */

    public void login(final Context context, final String username, final String password, final OnLoginListener listener) {
        final JSONObject requestJSON = new JSONObject();

        try {
            requestJSON.put("username", username);
            requestJSON.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        StringEntity requestEntity;

        try {
            requestEntity = new StringEntity(requestJSON.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String url = BASE_URL + LOGIN_URL;

        asyncHttpClient.post(context, url, requestEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject responseJSON = new JSONObject(new String(responseBody));
                    AppSettings.sharedSettings(context).setToken(responseJSON.getString("token"));
                    AppSettings.sharedSettings(context).setIsUserLoggedIn(true);
                } catch (JSONException e) {
                    if (listener != null) {
                        listener.onLoginFailure(500);
                    }

                    e.printStackTrace();
                }

                if (listener != null) {
                    listener.onLoginSuccess();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable) {
                if (listener != null) {
                    listener.onLoginFailure(statusCode);
                }
            }
        });
    }


    public void getUser(final Context context, final OnGetUserListener listener) {
        if (!authenticate(context)) {
            if (listener != null) {
                listener.onGetUserFailure(401);
            }

            return;
        }

        String url = BASE_API_URL + USERS_URL + ME_URL;

        asyncHttpClient.get(context, url, null, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject responseJSON = new JSONObject(new String(responseBody));

                    if (!UserData.sharedData(context).storeUser(responseJSON, "master")) {
                        if (listener != null) {
                            listener.onGetUserFailure(403);
                        }
                    }
                } catch (JSONException e) {
                    if (listener != null) {
                        listener.onGetUserFailure(500);
                    }

                    e.printStackTrace();
                }

                if (listener != null) {
                    listener.onGetUserSuccess();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onGetUserFailure(statusCode);
                }
            }
        });
    }

    public void registerGCMToken(Context context, final String registrationId, final OnGCMTokenRegisteredListener listener) {
        if (!authenticate(context)) {
            if (listener != null) {
                listener.onGCMTokenRegistrationFailure(401);
            }

            return;
        }

        final JSONObject requestJSON = new JSONObject();

        try {
            requestJSON.put("token", registrationId);
            requestJSON.put("deviceId", getDeviceId(context));
            requestJSON.put("type", "android");
            requestJSON.put("osVersion", Build.VERSION.RELEASE);
            requestJSON.put("deviceInfo", Build.MANUFACTURER + " " + Build.MODEL);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        StringEntity requestEntity;

        try {
            requestEntity = new StringEntity(requestJSON.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String url = BASE_API_URL + DEVICES_URL;

        asyncHttpClient.post(context, url, requestEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (listener != null) {
                    listener.onGCMTokenRegistrationSuccess(registrationId);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onGCMTokenRegistrationFailure(statusCode);
                }
            }
        });
    }

    public void pingLocation(Context context, String orderId, Location location, boolean doItSync) {
        if (authenticate(context)) {
            return;
        }

        final JSONObject requestJSON = new JSONObject();

        try {
            requestJSON.put("deviceId", getDeviceId(context));
            requestJSON.put("order", orderId);
            requestJSON.put("geo", locationToJsonArray(location.getLatitude(), location.getLongitude()));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        StringEntity requestEntity;

        try {
            requestEntity = new StringEntity(requestJSON.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String url = BASE_API_URL + POINTS_URL;

        asyncHttpClient.post(context, url, requestEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void setTokenCookie(Context context, String token) {
        CookieStore cookieStore = new PersistentCookieStore(context);
        asyncHttpClient.setCookieStore(cookieStore);
        cookieStore.clear();
        cookieStore.addCookie(new BasicClientCookie("token", token));
        asyncHttpClient.addHeader("Authorization", "Bearer " + token);
    }

    private boolean authenticate(Context context) {
        if (AppSettings.sharedSettings(context).getToken() == null) {
            return false;
        }

        setTokenCookie(context, AppSettings.sharedSettings(context).getToken());
        return true;
    }

    private String getDeviceId(Context context) {
        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    private JSONArray locationToJsonArray(double latitude, double longitude) throws JSONException {
        JSONArray locationJSON = new JSONArray();

        locationJSON.put(latitude);
        locationJSON.put(longitude);

        return locationJSON;
    }
}
