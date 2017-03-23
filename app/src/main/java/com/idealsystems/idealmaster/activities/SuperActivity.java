package com.idealsystems.idealmaster.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.idealsystems.idealmaster.R;
import com.idealsystems.idealmaster.constants.IntentConstants;
import com.idealsystems.idealmaster.model.Order;
import com.idealsystems.idealmaster.settings.AppSettings;
import com.idealsystems.idealmaster.utils.LocaleUtils;

/**
 * Created by acerkinght on 8/18/16.
 */
public class SuperActivity extends AppCompatActivity {
    private static int ACTIVE_COUNTER = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.setLocale(this, AppSettings.sharedSettings(this).getLanguage());
    }

    @Override
    protected void onResume() {
        super.onResume();

        ACTIVE_COUNTER++;

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(newOrderBroadcastReceiver, new IntentFilter(IntentConstants.ACTION_NEW_ORDER));
        localBroadcastManager.registerReceiver(orderStartedBroadcastReceiver, new IntentFilter(IntentConstants.ACTION_ORDER_STARTED));
        localBroadcastManager.registerReceiver(orderPausedBroadcastReceiver, new IntentFilter(IntentConstants.ACTION_ORDER_PAUSED));
        localBroadcastManager.registerReceiver(orderFinishedBroadcastReceiver, new IntentFilter(IntentConstants.ACTION_ORDER_FINISHED));
        localBroadcastManager.registerReceiver(orderCanceledBroadcastReceiver, new IntentFilter(IntentConstants.ACTION_ORDER_CANCELED));
        localBroadcastManager.registerReceiver(orderOfferBroadcastReceiver, new IntentFilter(IntentConstants.ACTION_NEW_OFFER));
    }

    @Override
    protected void onPause() {
        super.onPause();

        ACTIVE_COUNTER--;

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(newOrderBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(orderStartedBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(orderPausedBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(orderFinishedBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(orderCanceledBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(orderOfferBroadcastReceiver);
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

    private BroadcastReceiver orderFinishedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Order order = intent.getParcelableExtra(IntentConstants.EXTRA_ORDER);
            showOrderDialog(R.string.order_was_finished, R.string.order_was_finished_long, order);
            onOrderFinishedReceived(order);
        }
    };

    private BroadcastReceiver orderCanceledBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Order order = intent.getParcelableExtra(IntentConstants.EXTRA_ORDER);
            showOrderDialog(R.string.order_was_canceled, R.string.order_was_canceled_long, order);
            onOrderCanceledReceived(order);
        }
    };

    private BroadcastReceiver orderOfferBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Order order = intent.getParcelableExtra(IntentConstants.EXTRA_ORDER);
            showOrderDialog(R.string.order_was_offered, R.string.order_was_offered_long, order);
            onOrderOfferedReceived(order);
        }
    };

    private void showOrderDialog(int title, int message, final Order order) {
        if (this instanceof OrderDetailsActivity) {
            OrderDetailsActivity thisActivity = (OrderDetailsActivity) this;

            if (order.getId().equals(thisActivity.getOrderId())) {
                return;
            }
        }

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
    public void onOrderFinishedReceived(Order order) {}
    public void onOrderCanceledReceived(Order order) {}
    public void onOrderOfferedReceived(Order order) {}
}