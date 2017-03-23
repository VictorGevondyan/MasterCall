package com.idealsystems.idealmaster.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.idealsystems.idealmaster.adapters.viewholders.SuperViewHolder;
import com.idealsystems.idealmaster.adapters.viewholders.TransactionsViewHolder;
import com.idealsystems.idealmaster.model.IdealTransaction;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created - Schumakher on 25-Aug-16.
 */
public class TransactionAdapter extends RecyclerView.Adapter<SuperViewHolder> implements TransactionsViewHolder.TransactionsProvider {
    private Context context;
    private ArrayList<IdealTransaction> transactions;

    public TransactionAdapter(Context context, ArrayList<IdealTransaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public SuperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return TransactionsViewHolder.getInstance(parent, context, this);
    }

    @Override
    public void onBindViewHolder(SuperViewHolder holder, int position) {
        holder.setupForPosition(position);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public String getToForPosition(int position) {
        return transactions.get(position).getToFullName();
    }

    @Override
    public Date getTimeForPosition(int position) {
        return transactions.get(position).getDate();
    }

    @Override
    public int getCostForPosition(int position) {
        return transactions.get(position).getMoneyAmount();
    }

    @Override
    public String getStatusForPosition (int position) {
        return transactions.get(position).getPaymentType();
    }

    public void setTransaction(ArrayList<IdealTransaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }
}
