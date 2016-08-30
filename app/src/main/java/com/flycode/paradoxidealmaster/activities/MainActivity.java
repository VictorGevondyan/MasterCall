package com.flycode.paradoxidealmaster.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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
import com.flycode.paradoxidealmaster.model.Order;
import com.flycode.paradoxidealmaster.model.User;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.settings.UserData;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends SuperActivity {
    private static final String NEW_ORDERS_COUNT = "newOrdersCount";
    private static final String MY_ORDERS_COUNT = "myOrdersCount";

    private MenuAdapter adapter;
    private int newOrdersCount;
    private int myOrdersCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new MenuAdapter(this);

        GridView menuGridView = (GridView) findViewById(R.id.menu);
        assert menuGridView != null;
        menuGridView.setAdapter(adapter);
        menuGridView.setOnItemClickListener(menuClickListener);

        newOrdersCount = 0;
        myOrdersCount = 0;

        if (savedInstanceState != null) {
            newOrdersCount = savedInstanceState.getInt(NEW_ORDERS_COUNT);
            myOrdersCount = savedInstanceState.getInt(MY_ORDERS_COUNT);
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

                        newOrdersCount = response.body().getCount();

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<OrdersListResponse> call, Throwable t) {
                        Log.d("Failed", "to load count of new orders");
                    }
                });

        APIBuilder
                .getIdealAPI()
                .getOrders(
                        AppSettings.sharedSettings(this).getBearerToken(),
                        null, null,
                        new String[] {OrderStatusConstants.PAUSED, OrderStatusConstants.STARTED, OrderStatusConstants.WAITING_FINISHED,
                                OrderStatusConstants.WAITING_FAVORITE, OrderStatusConstants.WAITING_PAUSED},
                        true
                )
                .enqueue(new Callback<OrdersListResponse>() {
                    @Override
                    public void onResponse(Call<OrdersListResponse> call, Response<OrdersListResponse> response) {
                        if (!response.isSuccessful()) {
                            return;
                        }

                        myOrdersCount = response.body().getCount();

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<OrdersListResponse> call, Throwable t) {

                    }
                });

        APIBuilder
                .getIdealAPI()
                .getUser(AppSettings.sharedSettings(this).getBearerToken())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (!response.isSuccessful()) {
                            return;
                        }

                        UserData
                                .sharedData(MainActivity.this)
                                .storeUser(response.body(), "master");

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(NEW_ORDERS_COUNT, newOrdersCount);
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
        private Typeface avenirLight;

        public MenuAdapter(Context context) {
            super(context, R.layout.item_menu);

            this.context = context;
            this.titles = context.getResources().getStringArray(R.array.menu_titles);
            this.dashes = context.getResources().getStringArray(R.array.menu_dashes);
            this.icons = context.getResources().getStringArray(R.array.menu_icons);
            this.icomoon = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON);
            this.avenirRoman = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_ROMAN);
            this.avenirLight = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_LIGHT);
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
            TextView bigCounterTextView = (TextView) convertView.findViewById(R.id.big_counter);
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
                smallCounterTextView.setText(String.valueOf(newOrdersCount));
                smallCounterTextView.setTypeface(avenirRoman);
            } else if (position == 1) {
                smallCounterTextView.setVisibility(View.VISIBLE);
                smallCounterTextView.setBackgroundResource(R.drawable.rounded_rect_blue);
                smallCounterTextView.setText(String.valueOf(myOrdersCount));
                smallCounterTextView.setTypeface(avenirRoman);
            } else {
                smallCounterTextView.setVisibility(View.GONE);
            }

            if (position == 3) {
                bigCounterTextView.setVisibility(View.VISIBLE);
                bigCounterTextView.setText(String.valueOf(UserData.sharedData(MainActivity.this).getBalance()));
                bigCounterTextView.setTypeface(avenirLight);
            } else {
                bigCounterTextView.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return 6;
        }
    }
}
