package com.flycode.paradoxidealmaster.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.adapters.OrderAdapter;
import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.response.OrderResponse;
import com.flycode.paradoxidealmaster.api.response.OrdersListResponse;
import com.flycode.paradoxidealmaster.constants.IntentConstants;
import com.flycode.paradoxidealmaster.constants.OrderStatusConstants;
import com.flycode.paradoxidealmaster.model.Order;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListActivity extends SuperActivity implements RealmChangeListener<RealmResults<Order>>,OrderAdapter.OnOrderItemClickListener, View.OnClickListener {
    private OrderAdapter adapter;
    private String type;
    private boolean alreadyUpdated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        alreadyUpdated = false;

        type = getIntent().getStringExtra(IntentConstants.EXTRA_ORDER_LIST_TYPE);

        ImageView actionBarBackgroundImageView = (ImageView) findViewById(R.id.action_background);

        TextView titleTextView = (TextView) findViewById(R.id.title);

        if (type.equals(IntentConstants.VALUE_ORDER_LIST_NEW)) {
            actionBarBackgroundImageView.setImageResource(R.drawable.new_orders_background);
            titleTextView.setText(R.string.new_orders);
        } else if (type.equals(IntentConstants.VALUE_ORDER_LIST_HISTORY)) {
            actionBarBackgroundImageView.setImageResource(R.drawable.finished_orders_background);
            titleTextView.setText(R.string.finished_orders);
        } else if (type.equals(IntentConstants.VALUE_ORDER_LIST_TAKEN)) {
            actionBarBackgroundImageView.setImageResource(R.drawable.my_orders_background);
            titleTextView.setText(R.string.my_orders);
        }

        titleTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_MEDIUM));

        Button backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(this);
        backButton.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON));

        adapter = new OrderAdapter(this, new ArrayList<Order>(), this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.orders_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        alreadyUpdated = false;

        if (type.equals(IntentConstants.VALUE_ORDER_LIST_NEW)) {
            loadOrdersViaServer(new String[] {OrderStatusConstants.NOT_TAKEN, OrderStatusConstants.NOT_TAKEN_MASTER_ATTACHED});
        } else {
            loadOrdersViaDatabase();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onChange(RealmResults<Order> orderRealmResults) {
        ArrayList<Order> orders = new ArrayList<>();

        for (int index = 0 ; index < orderRealmResults.size() ; index++) {
            orders.add(orderRealmResults.get(index));
        }

        if (orders.isEmpty()) {
            findViewById(R.id.noOrdersTV).setVisibility(View.VISIBLE);
        }

        adapter.setOrders(orders);

        if (!alreadyUpdated) {
            alreadyUpdated = true;
            loadOrdersViaServer(new String[] {OrderStatusConstants.STARTED, OrderStatusConstants.PAUSED,
                            OrderStatusConstants.CANCELED, OrderStatusConstants.FINISHED,
                            OrderStatusConstants.WAITING_FAVORITE, OrderStatusConstants.WAITING_PAUSED, OrderStatusConstants.WAITING_FINISHED});
        }
    }

    @Override
    public void onOrderItemClick(Order order, int position) {
        startActivity(new Intent(this, OrderDetailsActivity.class).putExtra(IntentConstants.EXTRA_ORDER, order));
        overridePendingTransition(R.anim.slide_up_in, R.anim.hold);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_down_out);
    }

    @Override
    public void onNewOrderReceived(Order order) {
        super.onNewOrderReceived(order);

        adapter.insertOrder(order);
    }

    private void loadOrdersViaServer(final String[] statuses) {
        APIBuilder
                .getIdealAPI()
                .getOrders(
                        AppSettings.sharedSettings(this).getBearerToken(),
                        null, null,
                        statuses,
                        false
                )
                .enqueue(new Callback<OrdersListResponse>() {
                    @Override
                    public void onResponse(Call<OrdersListResponse> call, final Response<OrdersListResponse> response) {
                        if (!response.isSuccessful()) {
                            return;
                        }

                        new AsyncTask<Void, Void, ArrayList<Order>>() {

                            @Override
                            protected ArrayList<Order> doInBackground(Void... args) {
                                final ArrayList<Order> newOrders = new ArrayList<>();
                                final ArrayList<OrderResponse> orderResponses = response.body().getObjs();

                                for (OrderResponse orderResponse : orderResponses) {
                                    newOrders.add(Order.fromResponse(orderResponse));
                                }

                                return newOrders;
                            }

                            @Override
                            protected void onPostExecute(final ArrayList<Order> orders) {
                                super.onPostExecute(orders);

                                if (type.equals(IntentConstants.VALUE_ORDER_LIST_NEW)) {
                                    adapter.setOrders(orders);
                                } else {
                                    Realm
                                            .getDefaultInstance()
                                            .executeTransactionAsync(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    realm.insertOrUpdate(orders);
                                                }
                                            });
                                }
                            }
                        }.execute();
                    }

                    @Override
                    public void onFailure(Call<OrdersListResponse> call, Throwable t) {
                        Log.d("can't load orders", "what a pity");
                    }
                });
    }

    private void loadOrdersViaDatabase() {
        RealmQuery<Order> query = Realm
                .getDefaultInstance()
                .where(Order.class);

        if (type.equals(IntentConstants.VALUE_ORDER_LIST_HISTORY)) {
            query
                    .equalTo("status", OrderStatusConstants.FINISHED)
                    .or()
                    .equalTo("status", OrderStatusConstants.CANCELED);
        } else if (type.equals(IntentConstants.VALUE_ORDER_LIST_TAKEN)) {
            query
                    .equalTo("status", OrderStatusConstants.STARTED)
                    .or()
                    .equalTo("status", OrderStatusConstants.PAUSED)
                    .or()
                    .equalTo("status", OrderStatusConstants.WAITING_FAVORITE)
                    .or()
                    .equalTo("status", OrderStatusConstants.WAITING_FINISHED)
                    .or()
                    .equalTo("status", OrderStatusConstants.WAITING_PAUSED);
        }

        query
                .findAllSortedAsync("updated", Sort.DESCENDING)
                .addChangeListener(this);
    }

    private class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private Context context;

        public DividerItemDecoration(Context context) {
            this.context = context;
        }

        @Override
        public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            Paint paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(context.getResources().getColor(R.color.lighter_grey));

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int left = 0;
                int right = parent.getWidth();

                canvas.drawLine(left, top, right, top, paint);
            }
        }
    }
}
