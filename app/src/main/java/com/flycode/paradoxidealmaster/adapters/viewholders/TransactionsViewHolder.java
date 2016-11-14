package com.flycode.paradoxidealmaster.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.utils.DateUtils;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

import java.util.Date;

/**
 * Created - Schumakher on 26-Aug-16.
 */
public class TransactionsViewHolder extends SuperViewHolder {
    private TextView toTextView;
    private TextView timeTextView;
    private TextView costTextView;
    private TextView statusTextView;

    private Context context;

    private TransactionsProvider provider;

    public static TransactionsViewHolder getInstance(ViewGroup parent, Context context, TransactionsProvider provider) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_transaction, parent, false);

        return new TransactionsViewHolder(itemView, context, provider);
    }

    private TransactionsViewHolder(View itemView, Context context, TransactionsProvider provider) {
        super(itemView);

        this.context = context;
        this.provider = provider;

        toTextView = processSection(itemView.findViewById(R.id.to_section), R.string.icon_person, R.string.to);
        timeTextView = processSection(itemView.findViewById(R.id.time_section), R.string.icon_clock, R.string.time);
        costTextView = processSection(itemView.findViewById(R.id.cost_section), R.string.icon_cost, R.string.cost);
        statusTextView = processSection(itemView.findViewById(R.id.status_section), R.string.icon_comment, R.string.payment_type);
    }

    @Override
    public void setupForPosition(int position) {
        toTextView.setText(provider.getToForPosition(position));
        timeTextView.setText(DateUtils.infoDateStringFromDate(provider.getTimeForPosition(position)));
        costTextView.setText(String.valueOf(provider.getCostForPosition(position)));
        statusTextView.setText(provider.getStatusForPosition(position));
    }

    private TextView processSection(View section, int icon, int title) {
        TextView iconTextView = (TextView) section.findViewById(R.id.icon);
        TextView titleTextView = (TextView) section.findViewById(R.id.title);
        TextView valueTextView = (TextView) section.findViewById(R.id.value);

        iconTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.ICOMOON, context));
        titleTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK, context));
        valueTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK, context));

        iconTextView.setText(icon);
        titleTextView.setText(title);

        return valueTextView;
    }

    public interface TransactionsProvider {
        String getToForPosition(int position);
        Date getTimeForPosition(int position);
        int getCostForPosition(int position);
        String getStatusForPosition(int position);
    }
}