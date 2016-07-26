package com.flycode.paradoxidealmaster.gcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private static final String MESSAGE = "message";
    private static final String TYPE = "type";
    private static final String ORDER = "order";
    private static final String ARRIVAL_TIME = "arrivalTime";

    private static int idCounter = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {
        for (String key : intent.getExtras().keySet()) {
            Object value =intent.getExtras().get(key);
            Log.d("ARSENIUM23", String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }

        final JSONObject messageObject;

        try {
            messageObject = new JSONObject(intent.getExtras().getString(MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        if (messageObject.has(ORDER)) {
//            APITalker.sharedTalker().getOrder(context, messageObject.optString(ORDER), new GetOrderHandler() {
//                @Override
//                public void onGetOrderSuccess(Order order) {
//                    if (Order.isOldVersion(order, context)) {
//                        return;
//                    }
//
//                    int arrivalTime = messageObject.optInt(ARRIVAL_TIME, -1);
//
//                    generateUserNotification(context, order, arrivalTime, messageObject.optString(TYPE));
//                }
//
//                @Override
//                public void onGetOrderFailure(int statusCode) {
//
//                }
//            });
        }
    }
}
