package com.flycode.paradoxidealmaster.adapters.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.utils.DateUtils;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;
import com.flycode.paradoxidealmaster.views.CircleView;

import java.util.Date;

/**
 * Created by acerkinght on 8/22/16.
 */
public class OrderViewHolder extends SuperViewHolder implements View.OnTouchListener {
    private CircleView balloonCircleView;
    private TextView titleTextView;
    private TextView dateValueTextView;
    private TextView locationValueTextView;

    private Context context;
    private Rect rect;

    private OrderProvider provider;
    private OnItemClickListener listener;

    public static OrderViewHolder getInstance(ViewGroup parent, Context context, OrderProvider provider, OnItemClickListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_order, parent, false);

        return new OrderViewHolder(itemView, context, provider, listener);
    }

    private OrderViewHolder(View itemView, Context context, OrderProvider provider, OnItemClickListener listener) {
        super(itemView);

        itemView.setOnTouchListener(this);

        this.context = context;
        this.provider = provider;
        this.listener = listener;

        balloonCircleView = (CircleView) itemView.findViewById(R.id.balloon);
        titleTextView = (TextView) itemView.findViewById(R.id.title);

        balloonCircleView.setBackgroundColor(Color.GREEN);

        titleTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK));

        dateValueTextView = processSection(itemView.findViewById(R.id.date_section), R.string.icon_calendar, R.string.date);
        locationValueTextView = processSection(itemView.findViewById(R.id.location_section), R.string.icon_marker, R.string.location);
    }

    @Override
    public void setupForPosition(int position) {
        String location = provider.getLocationForPosition(position);

        titleTextView.setText(provider.getTitleForPosition(position));
        locationValueTextView.setText(location == null || location.isEmpty() ? "N/A" : location);
        dateValueTextView.setText(DateUtils.orderDateStringFromDate(provider.getDateForPosition(position)));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

        if (motionEvent.getAction() == MotionEvent.ACTION_UP
                && rect.contains(view.getLeft() + (int) motionEvent.getX(), view.getTop() + (int) motionEvent.getY())) {
            if (listener != null) {
                listener.onItemClicked(this, getAdapterPosition());
            }

            return true;
        }

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
        } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
        }

        return true;
    }

    private TextView processSection(View section, int icon, int title) {
        TextView iconTextView = (TextView) section.findViewById(R.id.icon);
        TextView titleTextView = (TextView) section.findViewById(R.id.title);
        TextView valueTextView = (TextView) section.findViewById(R.id.value);

        iconTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.ICOMOON));
        titleTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK));
        valueTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK));

        iconTextView.setText(icon);
        titleTextView.setText(title);

        return valueTextView;
    }

    public interface OrderProvider {
        String getLocationForPosition(int position);
        String getTitleForPosition(int position);
        Date getDateForPosition(int position);
    }
}
