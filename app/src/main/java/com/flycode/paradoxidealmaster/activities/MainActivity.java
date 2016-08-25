package com.flycode.paradoxidealmaster.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.response.OrdersListResponse;
import com.flycode.paradoxidealmaster.constants.IntentConstants;
import com.flycode.paradoxidealmaster.constants.OrderStatusConstants;
import com.flycode.paradoxidealmaster.gcm.GCMSubscriber;
import com.flycode.paradoxidealmaster.model.IdealService;
import com.flycode.paradoxidealmaster.model.Order;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;
import com.flycode.paradoxidealmaster.utils.threads.OrderUpdateThread;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends SuperActivity {
    private static final String ORDERS_COUNT = "ordersCount";

    private MenuAdapter adapter;
    private int ordersCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new MenuAdapter(this);

        GridView menuGridView = (GridView) findViewById(R.id.menu);
        assert menuGridView != null;
        menuGridView.setAdapter(adapter);
        menuGridView.setOnItemClickListener(menuClickListener);

        ordersCount = 0;

        if (savedInstanceState != null) {
            ordersCount = savedInstanceState.getInt(ORDERS_COUNT);
        }

        try {
            GCMSubscriber.registerForGcm(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Order order = getIntent().getParcelableExtra(IntentConstants.EXTRA_ORDER);

        if (order != null) {
            startActivity(new Intent(this, OrderDetailsActivity.class).putExtra(IntentConstants.EXTRA_ORDER, order));
            overridePendingTransition(R.anim.slide_up_in, R.anim.hold);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        APIBuilder
                .getIdealAPI()
                .getOrders(
                        AppSettings.sharedSettings(this).getBearerToken(),
                        null, null,
                        new String[] {OrderStatusConstants.NOT_TAKEN},
                        true
                )
                .enqueue(new Callback<OrdersListResponse>() {
                    @Override
                    public void onResponse(Call<OrdersListResponse> call, Response<OrdersListResponse> response) {
                        if (!response.isSuccessful()) {
                            return;
                        }

                        ordersCount = response.body().getCount();

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<OrdersListResponse> call, Throwable t) {

                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ORDERS_COUNT, ordersCount);
    }

    AdapterView.OnItemClickListener menuClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                startActivity(
                        new Intent(MainActivity.this, OrderListActivity.class)
                                .putExtra(IntentConstants.EXTRA_ORDER_LIST_TYPE, IntentConstants.VALUE_ORDER_LIST_NEW));
                overridePendingTransition(R.anim.slide_up_in, R.anim.hold);
            } else if (position == 1) {
                startActivity(
                        new Intent(MainActivity.this, OrderListActivity.class)
                                .putExtra(IntentConstants.EXTRA_ORDER_LIST_TYPE, IntentConstants.VALUE_ORDER_LIST_TAKEN));
                overridePendingTransition(R.anim.slide_up_in, R.anim.hold);
            } else if (position == 2) {
                startActivity(
                        new Intent(MainActivity.this, OrderListActivity.class)
                                .putExtra(IntentConstants.EXTRA_ORDER_LIST_TYPE, IntentConstants.VALUE_ORDER_LIST_HISTORY));
                overridePendingTransition(R.anim.slide_up_in, R.anim.hold);
            } else if (position == 5) {
                AppSettings.sharedSettings(MainActivity.this).setIsUserLoggedIn(false);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }
    };

    private class MenuAdapter extends ArrayAdapter {
        private Context context;
        private String titles[];
        private String dashes[];
        private String icons[];
        private Typeface icomoon;
        private Typeface avenirRoman;

        public MenuAdapter(Context context) {
            super(context, R.layout.item_menu);

            this.context = context;
            this.titles = context.getResources().getStringArray(R.array.menu_titles);
            this.dashes = context.getResources().getStringArray(R.array.menu_dashes);
            this.icons = context.getResources().getStringArray(R.array.menu_icons);
            this.icomoon = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON);
            this.avenirRoman = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_ROMAN);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_menu, parent, false);
            }

            TextView iconTextView = (TextView) convertView.findViewById(R.id.icon);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
            TextView smallCounterTextView = (TextView) convertView.findViewById(R.id.small_counter);
            View dashView = convertView.findViewById(R.id.dash);
            View bottomView = convertView.findViewById(R.id.bottom);
            View rightView = convertView.findViewById(R.id.right);

            iconTextView.setTypeface(icomoon);
            titleTextView.setTypeface(avenirRoman);

            iconTextView.setText(icons[position]);
            titleTextView.setText(titles[position]);
            dashView.setBackgroundColor(Color.parseColor(dashes[position]));

            rightView.setVisibility(position % 2 == 0 ? View.VISIBLE : View.INVISIBLE);
            bottomView.setVisibility(position < 4 ? View.VISIBLE : View.INVISIBLE);

            if (position == 0) {
                smallCounterTextView.setVisibility(View.VISIBLE);
                smallCounterTextView.setBackgroundResource(R.drawable.rounded_rect_orange);
                smallCounterTextView.setText(String.valueOf(ordersCount));
                smallCounterTextView.setTypeface(avenirRoman);
            } else {
                smallCounterTextView.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return 6;
        }
    }
}
