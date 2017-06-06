package com.idealsystems.idealmaster.adapters.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idealsystems.idealmaster.R;
import com.idealsystems.idealmaster.utils.DateUtils;
import com.idealsystems.idealmaster.utils.TypefaceLoader;
import com.idealsystems.idealmaster.views.CircleView;

import java.util.Date;

/**
 * Created by acerkinght on 8/22/16.
 */
public class OrderViewHolder extends SuperViewHolder implements View.OnTouchListener {
    private CircleView balloonCircleView;
    private CircleView balloonOutlineCircleView;
    private TextView titleTextView;
    private TextView dateValueTextView;
    private TextView locationValueTextView;
    private TextView statusValueTextView;

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
        balloonOutlineCircleView = (CircleView) itemView.findViewById(R.id.balloon_outline);
        titleTextView = (TextView) itemView.findViewById(R.id.title);

        balloonOutlineCircleView.setIsOutlineOnly(true);

        titleTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK, context));

        dateValueTextView = processSection(itemView.findViewById(R.id.date_section), R.string.icon_calendar, R.string.date);
        locationValueTextView = processSection(itemView.findViewById(R.id.location_section), R.string.icon_marker, R.string.location);
        statusValueTextView = processSection(itemView.findViewById(R.id.status_section), R.string.icon_details, R.string.status);
    }

    @Override
    public void setupForPosition(int position) {
        String location = provider.getLocationForPosition(position);

        titleTextView.setText(provider.getTitleForPosition(position));
        locationValueTextView.setText(location == null || location.isEmpty() ? "N/A" : location);
        statusValueTextView.setText(provider.getStatusForPosition(position));
        dateValueTextView.setText(DateUtils.infoDateStringFromDate(provider.getDateForPosition(position)));

        try {
            balloonCircleView.setBackgroundColor(Color.parseColor(provider.getColorForPosition(position)));
            balloonOutlineCircleView.setBackgroundColor(Color.parseColor(provider.getColorForPosition(position)));
        } catch (Exception ignored) {

        }

        balloonOutlineCircleView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

        if (motionEvent.getAction() == MotionEvent.ACTION_UP
                && rect.contains(view.getLeft() + (int) motionEvent.getX(), view.getTop() + (int) motionEvent.getY())) {
            if (listener != null) {
                listener.onItemClicked(this, getAdapterPosition());
            }

            balloonOutlineCircleView.setVisibility(View.INVISIBLE);

            return true;
        }

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            balloonOutlineCircleView.setVisibility(View.VISIBLE);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            balloonOutlineCircleView.setVisibility(View.INVISIBLE);
        }

        return true;
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

    public interface OrderProvider {
        String getLocationForPosition(int position);
        String getTitleForPosition(int position);
        String getColorForPosition(int position);
        String getStatusForPosition(int position);
        Date getDateForPosition(int position);
    }
}
