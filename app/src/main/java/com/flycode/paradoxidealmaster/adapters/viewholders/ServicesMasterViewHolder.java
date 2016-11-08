package com.flycode.paradoxidealmaster.adapters.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.model.IdealMasterService;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

/**
 * Created by acerkinght on 8/30/16.
 */
public class ServicesMasterViewHolder extends SuperViewHolder implements View.OnClickListener {
    private Context context;
    private MasterServiceProvider provider;
    private OnItemClickListener listener;

    private TextView costTextView;
    private TextView unitTextView;
    private TextView titleTextView;
    private View dash;

    public static ServicesMasterViewHolder initialize(Context context, ViewGroup parent, MasterServiceProvider provider, OnItemClickListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_service_master, parent, false);
        return new ServicesMasterViewHolder(itemView, context, provider, listener);
    }

    private ServicesMasterViewHolder(View itemView, Context context, MasterServiceProvider provider, OnItemClickListener listener) {
        super(itemView);

        this.context = context;
        this.provider = provider;
        this.listener = listener;

        costTextView = (TextView) itemView.findViewById(R.id.cost);
        unitTextView = (TextView) itemView.findViewById(R.id.unit);
        titleTextView = (TextView) itemView.findViewById(R.id.title);
        dash = itemView.findViewById(R.id.dash);

        costTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK));
        titleTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK));
    }

    @Override
    public void setupForPosition(int position) {
        IdealMasterService service = provider.getMasterServiceForPosition(position);

        costTextView.setText(context.getString(R.string.cost_formatted, service.getCost()));

        if (service.getUnit() != null && !service.getUnit().isEmpty()) {
            unitTextView.setText("/" + service.getUnit());
        }

        titleTextView.setText(service.getName());

        try {
            dash.setBackgroundColor(Color.parseColor(service.getColor()));
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View view) {
        listener.onItemClicked(this, getAdapterPosition());
    }

    public interface MasterServiceProvider {
        IdealMasterService getMasterServiceForPosition(int position);
    }
}
