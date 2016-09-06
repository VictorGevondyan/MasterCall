package com.flycode.paradoxidealmaster.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.model.IdealMasterService;
import com.flycode.paradoxidealmaster.model.IdealService;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

/**
 * Created by acerkinght on 8/30/16.
 */
public class ServicesDetailedViewHolder extends SuperViewHolder implements View.OnClickListener {
    private Context context;
    private DetailedServiceProvider provider;
    private OnItemClickListener listener;

    private TextView costTextView;
    private TextView titleTextView;

    public static ServicesDetailedViewHolder initialize(Context context, ViewGroup parent, DetailedServiceProvider provider, OnItemClickListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_service_detailed, parent, false);
        return new ServicesDetailedViewHolder(itemView, context, provider, listener);
    }

    private ServicesDetailedViewHolder(View itemView, Context context, DetailedServiceProvider provider, OnItemClickListener listener) {
        super(itemView);

        this.context = context;
        this.provider = provider;
        this.listener = listener;

        costTextView = (TextView) itemView.findViewById(R.id.cost);
        titleTextView = (TextView) itemView.findViewById(R.id.title);

        costTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK));
        titleTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK));
    }

    @Override
    public void setupForPosition(int position) {
        IdealMasterService service = provider.getDetailedServiceForPosition(position);

        costTextView.setText(context.getString(R.string.cost_formatted, service.getCost()));
        titleTextView.setText(service.getName());
    }

    @Override
    public void onClick(View view) {
        listener.onItemClicked(this, getAdapterPosition());
    }

    public interface DetailedServiceProvider {
        IdealMasterService getDetailedServiceForPosition(int position);
    }
}
