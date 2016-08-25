package com.flycode.paradoxidealmaster.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.activities.MainActivity;
import com.flycode.paradoxidealmaster.activities.SuperActivity;
import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.response.OrderResponse;
import com.flycode.paradoxidealmaster.constants.IntentConstants;
import com.flycode.paradoxidealmaster.model.Order;
import com.flycode.paradoxidealmaster.settings.AppSettings;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        for (String key : intent.getExtras().keySet()) {
            Object value =intent.getExtras().get(key);
            Log.d("ARSENIUM23", String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }

        String title = intent.getStringExtra("title");

        if (title.equals("order_not taken")) {
            String orderId = intent.getStringExtra("message");

            APIBuilder
                    .getIdealAPI()
                    .getOrder(
                            AppSettings.sharedSettings(context).getBearerToken(),
                            orderId
                    )
                    .enqueue(new Callback<OrderResponse>() {
                        @Override
                        public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                            if (!response.isSuccessful()) {
                                return;
                            }

                            Order order = Order.fromResponse(response.body());

                            String newOrderText = context.getString(R.string.new_order);

                            Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
                            notificationIntent.setClass(context.getApplicationContext(), MainActivity.class);
                            notificationIntent.putExtra(IntentConstants.EXTRA_ORDER, order);

                            NotificationCompat.Style style = new NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(newOrderText)
                                    .bigText(order.getServiceName());

                            showNotification(
                                    context,
                                    style,
                                    notificationIntent,
                                    newOrderText + " : " + order.getServiceName(),
                                    order.getId()
                            );

                            LocalBroadcastManager
                                    .getInstance(context)
                                    .sendBroadcast(new Intent(IntentConstants.ACTION_NEW_ORDER).putExtra(IntentConstants.EXTRA_ORDER, order));
                        }

                        @Override
                        public void onFailure(Call<OrderResponse> call, Throwable t) {

                        }
                    });
        }
    }

    private void showNotification(final Context context, NotificationCompat.Style style, Intent notificationIntent, String ticker, String identifier) {
        if (SuperActivity.getActiveCounter() > 0) {
            return;
        }

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(),
                notificationIntent,
                0/*PendingIntent.FLAG_CANCEL_CURRENT*/);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setVibrate(new long[]{1000, 1000})
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setTicker(ticker)
                .setStyle(style)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();

        notification.flags |= NotificationCompat.FLAG_AUTO_CANCEL;
        notification.flags |= NotificationCompat.FLAG_SHOW_LIGHTS;

        notification.defaults |= NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_SOUND;

        notificationManager.notify(identifier, 0, notification);
    }
}
