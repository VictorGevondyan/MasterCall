package com.flycode.paradoxidealmaster.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

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
    }

    @Override
    protected void onPause() {
        super.onPause();

        ACTIVE_COUNTER--;

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(newOrderBroadcastReceiver);
    }

    private BroadcastReceiver newOrderBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Order order = intent.getParcelableExtra(IntentConstants.EXTRA_ORDER);
            onNewOrderReceived(order);
        }
    };

    public static int getActiveCounter() {
        return ACTIVE_COUNTER;
    }

    public void onNewOrderReceived(Order order) {}
}
