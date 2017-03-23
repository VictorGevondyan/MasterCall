package com.idealsystems.idealmaster.adapters.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.idealsystems.idealmaster.R;
import com.idealsystems.idealmaster.api.APIBuilder;
import com.idealsystems.idealmaster.model.IdealService;
import com.idealsystems.idealmaster.utils.DeviceUtil;
import com.idealsystems.idealmaster.views.CircleView;

/**
 * Created by acerkinght on 8/30/16.
 */
public class ServicesViewHolder extends SuperViewHolder implements View.OnTouchListener {
    private Rect rect;

    private Context context;
    private ServiceProvider provider;
    private OnItemClickListener listener;

    private ImageView imageView;
    private TextView titleTextView;
    private CircleView balloonCircleView;
    private CircleView balloonOutlineCircleView;

    private int imageSide;

    public static ServicesViewHolder initialize(Context context, ViewGroup parent, ServiceProvider provider, OnItemClickListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_service, parent, false);
        return new ServicesViewHolder(itemView, context, provider, listener);
    }

    public ServicesViewHolder(View itemView, Context context, ServiceProvider provider, OnItemClickListener listener) {
        super(itemView);

        itemView.setOnTouchListener(this);

        this.context = context;
        this.provider = provider;
        this.listener = listener;

        balloonCircleView = (CircleView) itemView.findViewById(R.id.balloon);
        balloonOutlineCircleView = (CircleView) itemView.findViewById(R.id.balloon_outline);
        this.imageView = (ImageView) itemView.findViewById(R.id.image);
        this.titleTextView = (TextView) itemView.findViewById(R.id.title);

        balloonOutlineCircleView.setIsOutlineOnly(true);

        imageSide = (int) DeviceUtil.getPxForDp(context, 50);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

        if (motionEvent.getAction() == MotionEvent.ACTION_UP
                && rect.contains(view.getLeft() + (int) motionEvent.getX(), view.getTop() + (int) motionEvent.getY())) {
            if (listener != null) {
                listener.onItemClicked(this, getAdapterPosition());
            }

            if (provider.isServiceExpended(getAdapterPosition())) {
                balloonOutlineCircleView.setVisibility(View.VISIBLE);
            } else {
                balloonOutlineCircleView.setVisibility(View.INVISIBLE);
            }

            return true;
        }

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            balloonOutlineCircleView.setVisibility(View.VISIBLE);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            balloonOutlineCircleView.setVisibility(View.INVISIBLE);
        }

        return true;
    }

    @Override
    public void setupForPosition(int position) {
        IdealService service = provider.getService(position);

        try {
            balloonCircleView.setColor(Color.parseColor(service.getColor()));
            balloonOutlineCircleView.setColor(Color.parseColor(service.getColor()));
        } catch (Exception e) {

        }

        if (provider.isServiceExpended(getAdapterPosition())) {
            balloonOutlineCircleView.setVisibility(View.VISIBLE);
        } else {
            balloonOutlineCircleView.setVisibility(View.INVISIBLE);
        }

        titleTextView.setText(service.getTranslatedName(context));
        Glide
                .with(context)
                .load(APIBuilder.getImageUrl(imageSide, imageSide, service.getImage()))
                .asBitmap()
                .into(imageView);
    }

    public interface ServiceProvider {
        IdealService getService(int position);
        boolean isServiceExpended(int position);
    }
}