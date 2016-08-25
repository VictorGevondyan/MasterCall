package com.flycode.paradoxidealmaster.utils.threads;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.response.OrderResponse;
import com.flycode.paradoxidealmaster.api.response.OrdersListResponse;
import com.flycode.paradoxidealmaster.constants.OrderStatusConstants;
import com.flycode.paradoxidealmaster.model.Order;
import com.flycode.paradoxidealmaster.settings.AppSettings;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by acerkinght on 8/22/16.
 */
public class OrderUpdateThread extends Thread {
    private Handler handler;
    private Context context;

    public OrderUpdateThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                RealmResults<Order> orderRealmResults = Realm
                        .getDefaultInstance()
                        .where(Order.class)
                        .findAllSorted("updated", Sort.DESCENDING);

                Date date = null;

                if (orderRealmResults.size() > 0) {
                    date = orderRealmResults.get(0).getUpdated();
                }

                APIBuilder
                        .getIdealAPI()
                        .getOrders(
                                AppSettings.sharedSettings(context).getBearerToken(),
                                date, null,
                                new String[] {OrderStatusConstants.STARTED, OrderStatusConstants.PAUSED,
                                        OrderStatusConstants.CANCELED, OrderStatusConstants.FINISHED},
                                false
                        )
                        .enqueue(new retrofit2.Callback<OrdersListResponse>() {
                            @Override
                            public void onResponse(Call<OrdersListResponse> call, Response<OrdersListResponse> response) {
                                if (response.isSuccessful()) {
                                    final ArrayList<Order> newOrders = new ArrayList<>();

                                    for (OrderResponse orderResponse : response.body().getObjs()) {
                                        newOrders.add(Order.fromResponse(orderResponse));
                                    }

                                    Realm
                                            .getDefaultInstance()
                                            .executeTransactionAsync(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    realm.insertOrUpdate(newOrders);
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onFailure(Call<OrdersListResponse> call, Throwable t) {

                            }
                        });
            }
        };
        Looper.loop();
    }

    public void updateOrders() {
        if (handler == null) {
            return;
        }

        handler.dispatchMessage(new Message());
    }

    public void quiteLooper() {
        handler.getLooper().quit();
    }
}
