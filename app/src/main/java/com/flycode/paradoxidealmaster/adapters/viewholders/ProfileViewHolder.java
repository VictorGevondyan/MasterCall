package com.flycode.paradoxidealmaster.adapters.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.adapters.ProfileAdapter;
import com.flycode.paradoxidealmaster.layouts.RatingLayout;
import com.flycode.paradoxidealmaster.model.IdealFeedback;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

import java.util.ArrayList;

/**
 * Created by acerkinght on 9/3/16.
 */
public class ProfileViewHolder extends SuperViewHolder implements View.OnClickListener {
    private ProfileProvider provider;
    private ProfileActionListener listener;

    private TextView iconTextView;
    private TextView titleTextView;
    private Button actionButton;
    private RatingLayout ratingLayout;

    public static ProfileViewHolder getInstance(ViewGroup parent, Context context, ProfileProvider provider, ProfileActionListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_profile, parent, false);

        return new ProfileViewHolder(itemView, context, provider, listener);
    }

    public ProfileViewHolder(View itemView, Context context, ProfileProvider provider, ProfileActionListener listener) {
        super(itemView);

        this.provider = provider;
        this.listener = listener;

        iconTextView = (TextView) itemView.findViewById(R.id.icon);
        titleTextView = (TextView) itemView.findViewById(R.id.title);
        actionButton = (Button) itemView.findViewById(R.id.action);
        ratingLayout = (RatingLayout) itemView.findViewById(R.id.rating);

        titleTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK));
        iconTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.ICOMOON));
        actionButton.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.ICOMOON));

        actionButton.setOnClickListener(this);
    }

    @Override
    public void setupForPosition(int position) {
        if (provider.isExpendable(position)) {
            actionButton.setText(R.string.icon_triangle);
            actionButton.setVisibility(View.VISIBLE);
            actionButton.setOnClickListener(this);

            if (provider.isExpended(getAdapterPosition())) {
                actionButton.setRotation(180);
            } else {
                actionButton.setRotation(0);
            }
        } else {
            actionButton.setVisibility(View.GONE);
        }

        if (provider.showsRating(position)) {
            ratingLayout.setVisibility(View.VISIBLE);
            ratingLayout.setCurrentRating((int) Math.round(provider.getProfileRating()));
        } else {
            ratingLayout.setVisibility(View.GONE);
        }

        iconTextView.setText(provider.getProfileIconForPosition(position));
        titleTextView.setText(provider.getProfileValueForPosition(position));

        iconTextView.setTextColor(provider.getColor(position));

        actionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.action) {
            listener.onActionClicked(getAdapterPosition());
        } else {

        }
    }

    public interface ProfileProvider {
        String getProfileValueForPosition(int position);
        String getProfileIconForPosition(int position);
        boolean isExpendable(int position);
        boolean isExpended(int position);
        boolean showsRating(int position);
        double getProfileRating();
        int getColor(int position);
    }

    public interface ProfileActionListener {
        void onActionClicked(int position);
    }
}
