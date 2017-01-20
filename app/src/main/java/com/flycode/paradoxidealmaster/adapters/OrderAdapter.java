package com.flycode.paradoxidealmaster.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.flycode.paradoxidealmaster.adapters.viewholders.OnItemClickListener;
import com.flycode.paradoxidealmaster.adapters.viewholders.OrderViewHolder;
import com.flycode.paradoxidealmaster.adapters.viewholders.SuperViewHolder;
import com.flycode.paradoxidealmaster.model.IdealService;
import com.flycode.paradoxidealmaster.model.Order;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

/**
 * Created by acerkinght on 8/22/16.
 */
public class OrderAdapter extends RecyclerView.Adapter<SuperViewHolder> implements OnItemClickListener, OrderViewHolder.OrderProvider {
    private Context context;
    private ArrayList<Order> orders;
    private OnOrderItemClickListener listener;

    public OrderAdapter(Context context, ArrayList<Order> orders, OnOrderItemClickListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @Override
    public SuperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return OrderViewHolder.getInstance(parent, context, this, this);
    }

    @Override
    public void onBindViewHolder(SuperViewHolder holder, int position) {
        holder.setupForPosition(position);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onItemClicked(RecyclerView.ViewHolder viewHolder, int position) {
        listener.onOrderItemClick(orders.get(position), position);
    }

    @Override
    public String getLocationForPosition(int position) {
        return orders.get(position).getLocationName();
    }

    @Override
    public String getTitleForPosition(int position) {
        return orders.get(position).getTranslatedServiceName(context);
    }

    @Override
    public String getColorForPosition(int position) {
        if (orders.get(position).getServiceColor() == null
                || orders.get(position).getServiceColor().equals("#FFFFFF")) {
            IdealService idealService = Realm
                    .getDefaultInstance()
                    .where(IdealService.class)
                    .equalTo("id", orders.get(position).getServiceId())
                    .findFirst();

            if (idealService != null) {
                return idealService.getColor();
            }
        }

        return orders.get(position).getServiceColor();
    }

    @Override
    public Date getDateForPosition(int position) {
        return orders.get(position).getOrderTime();
    }

    public void setOrders(ArrayList<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    public void insertOrder(Order order) {
        for (Order existingOrder : orders) {
            if (existingOrder.getId().equals(order.getId())) {
                return;
            }
        }

        orders.add(0, order);

        notifyItemInserted(0);
    }

    public interface OnOrderItemClickListener {
        void onOrderItemClick(Order order, int position);
    }
}
