package com.flycode.paradoxidealmaster.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

import io.realm.Realm;
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

                            createStyleAndShowNotification(
                                    context,
                                    notificationIntent,
                                    newOrderText + " - " + order.getServiceName(),
                                    newOrderText,
                                    order.getServiceName(),
                                    order.getId());

                            LocalBroadcastManager
                                    .getInstance(context)
                                    .sendBroadcast(new Intent(IntentConstants.ACTION_NEW_ORDER).putExtra(IntentConstants.EXTRA_ORDER, order));
                        }

                        @Override
                        public void onFailure(Call<OrderResponse> call, Throwable t) {

                        }
                    });
        } else if (title.equals("order_started")) {
            processOrder(
                    context,
                    intent.getStringExtra("message"),
                    context.getString(R.string.order_was_started),
                    IntentConstants.ACTION_ORDER_STARTED);
        } else if (title.equals("order_paused")) {
            processOrder(
                    context,
                    intent.getStringExtra("message"),
                    context.getString(R.string.order_was_paused),
                    IntentConstants.ACTION_ORDER_PAUSED
            );
        }
    }

    private void processOrder(final Context context, final String orderId, final String title, final String action) {
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

                        final Order order = Order.fromResponse(response.body());

                        Order existingOrder = Realm
                                .getDefaultInstance()
                                .where(Order.class)
                                .equalTo("id", order.getId())
                                .findFirst();

                        if (existingOrder != null
                                && existingOrder.getUpdated().after(order.getUpdated())) {
                            return;
                        }

                        Realm
                                .getDefaultInstance()
                                .executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.insertOrUpdate(order);
                                    }
                                });

                        Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
                        notificationIntent.setClass(context.getApplicationContext(), MainActivity.class);
                        notificationIntent.putExtra(IntentConstants.EXTRA_ORDER, order);

                        createStyleAndShowNotification(
                                context,
                                notificationIntent,
                                title + " - " + order.getServiceName(),
                                title,
                                order.getServiceName(),
                                order.getId());

                        LocalBroadcastManager
                                .getInstance(context)
                                .sendBroadcast(new Intent(action).putExtra(IntentConstants.EXTRA_ORDER, order));
                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {

                    }
                });
    }

    private void createStyleAndShowNotification(final Context context, Intent notificationIntent, String ticker, String title, String message, String identifier) {
        if (Build.VERSION.SDK_INT < 16) {
            NotificationCompat.Style oldStyle = new NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(message);

            showNotification(
                    context,
                    oldStyle,
                    null,
                    notificationIntent,
                    ticker,
                    identifier,
                    title,
                    message
            );
        } else {
            Notification.Style newStyle = new Notification.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(message);

            showNotification(
                    context,
                    null,
                    newStyle,
                    notificationIntent,
                    ticker,
                    identifier,
                    title,
                    message
            );
        }
    }

    private void showNotification(final Context context, NotificationCompat.Style oldStyle, Notification.Style newStyle, Intent notificationIntent, String ticker, String identifier, String title, String message) {
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

        if (Build.VERSION.SDK_INT < 16) {
            Notification notification = new NotificationCompat.Builder(context)
                    .setVibrate(new long[]{1000, 1000})
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contentIntent)
                    .setTicker(ticker)
                    .setStyle(oldStyle)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build();

            notification.flags |= NotificationCompat.FLAG_AUTO_CANCEL;
            notification.flags |= NotificationCompat.FLAG_SHOW_LIGHTS;

            notification.defaults |= NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_SOUND;

            notificationManager.notify(identifier, 0, notification);
        } else {
            Notification.Builder notificationBuilder = new Notification.Builder(context)
                    .setVibrate(new long[]{1000, 1000})
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(contentIntent)
                    .setTicker(ticker)
                    .setStyle(newStyle)
                    .setPriority(Notification.PRIORITY_MAX);

            if (Build.VERSION.SDK_INT >= 21) {
                notificationBuilder
                        .setCategory(Notification.CATEGORY_EVENT)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setColor(context.getResources().getColor(R.color.ideal_red));
            }

            Notification notification = notificationBuilder.build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;

            notification.defaults |= Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;

            notificationManager.notify(identifier, 0, notification);
        }
    }
}
