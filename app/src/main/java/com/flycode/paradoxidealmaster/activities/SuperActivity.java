package com.flycode.paradoxidealmaster.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.constants.IntentConstants;
import com.flycode.paradoxidealmaster.model.Order;

/**
 * Created by acerkinght on 8/18/16.
 */
public class SuperActivity extends AppCompatActivity {
    private static int ACTIVE_COUNTER = 0;

    @Override
    protected void onResume() {
        super.onResume();

        ACTIVE_COUNTER++;

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(newOrderBroadcastReceiver, new IntentFilter(IntentConstants.ACTION_NEW_ORDER));
        localBroadcastManager.registerReceiver(orderStartedBroadcastReceiver, new IntentFilter(IntentConstants.ACTION_ORDER_STARTED));
        localBroadcastManager.registerReceiver(orderPausedBroadcastReceiver, new IntentFilter(IntentConstants.ACTION_ORDER_PAUSED));
    }

    @Override
    protected void onPause() {
        super.onPause();

        ACTIVE_COUNTER--;

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(newOrderBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(orderStartedBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(orderPausedBroadcastReceiver);
    }

    private BroadcastReceiver newOrderBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Order order = intent.getParcelableExtra(IntentConstants.EXTRA_ORDER);
            onNewOrderReceived(order);
        }
    };

    private BroadcastReceiver orderStartedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Order order = intent.getParcelableExtra(IntentConstants.EXTRA_ORDER);
            showOrderDialog(R.string.order_was_started, R.string.order_was_started_long, order);
            onOrderStartedReceived(order);
        }
    };

    private BroadcastReceiver orderPausedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Order order = intent.getParcelableExtra(IntentConstants.EXTRA_ORDER);
            showOrderDialog(R.string.order_was_paused, R.string.order_was_paused_long, order);
            onOrderStartedReceived(order);
        }
    };

    private void showOrderDialog(int title, int message, final Order order) {
        new MaterialDialog.Builder(SuperActivity.this)
                .title(title)
                .content(message)
                .positiveText(R.string.more)
                .negativeText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();

                        if (SuperActivity.this instanceof OrderDetailsActivity) {
                            OrderDetailsActivity activity = (OrderDetailsActivity) SuperActivity.this;

                            if (activity.getOrderId().equals(order.getId())) {
                                return;
                            }
                        }

                        Intent intent = new Intent(SuperActivity.this, OrderDetailsActivity.class);
                        intent.putExtra(IntentConstants.EXTRA_ORDER, order);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_up_in, R.anim.hold);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static int getActiveCounter() {
        return ACTIVE_COUNTER;
    }

    public void onNewOrderReceived(Order order) {}
    public void onOrderStartedReceived(Order order) {}
}
