package com.flycode.paradoxidealmaster.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.adapters.ProfileAdapter;
import com.flycode.paradoxidealmaster.layouts.RatingLayout;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

import java.util.Locale;

/**
 * Created by acerkinght on 9/3/16.
 */
public class ProfileViewHolder extends SuperViewHolder implements View.OnClickListener {
    private ProfileProvider provider;
    private ProfileActionListener listener;

    private TextView iconTextView;
    private TextView titleTextView;
    private TextView languageTextView;
    private Button actionButton;
    private RatingLayout ratingLayout;
    private LinearLayout languageLayout;
    private ProfileAdapter.OnChangeLanguageListener changeLanguageListener;

    public static ProfileViewHolder getInstance(ViewGroup parent, Context context, ProfileAdapter.OnChangeLanguageListener changeLanguageListener, ProfileProvider provider, ProfileActionListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_master_profile, parent, false);

        return new ProfileViewHolder(itemView, context, changeLanguageListener, provider, listener);
    }

    public ProfileViewHolder(View itemView, Context context, ProfileAdapter.OnChangeLanguageListener changeLanguageListener, ProfileProvider provider, ProfileActionListener listener) {
        super(itemView);

        this.provider = provider;
        this.listener = listener;
        this.changeLanguageListener = changeLanguageListener;

        iconTextView = (TextView) itemView.findViewById(R.id.icon);
        titleTextView = (TextView) itemView.findViewById(R.id.title);
        languageTextView = (TextView) itemView.findViewById(R.id.language_value);
        actionButton = (Button) itemView.findViewById(R.id.action);
        ratingLayout = (RatingLayout) itemView.findViewById(R.id.rating);
        languageLayout = (LinearLayout) itemView.findViewById(R.id.language_container);

        titleTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK, context));
        iconTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.ICOMOON, context));
        actionButton.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.ICOMOON, context));
        languageTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK, context));

        TextView languageDropTextView = (TextView) itemView.findViewById(R.id.language_drop);
        languageDropTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.ICOMOON, context));

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

        if (provider.isLanguage(position)) {
            languageLayout.setVisibility(View.VISIBLE);
            languageLayout.setOnClickListener(this);
            languageTextView.setText(AppSettings.sharedSettings(languageTextView.getContext()).getLanguage().toUpperCase(Locale.US));
        } else {
            languageLayout.setVisibility(View.GONE);
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
        } else if (view.getId() == R.id.language_container) {
            changeLanguageListener.onChangeLanguage();
        }
    }

    public interface ProfileProvider {
        String getProfileValueForPosition(int position);
        String getProfileIconForPosition(int position);
        boolean isExpendable(int position);
        boolean isExpended(int position);
        boolean showsRating(int position);
        boolean isLanguage(int position);
        double getProfileRating();
        int getColor(int position);
    }

    public interface ProfileActionListener {
        void onActionClicked(int position);
    }
}
