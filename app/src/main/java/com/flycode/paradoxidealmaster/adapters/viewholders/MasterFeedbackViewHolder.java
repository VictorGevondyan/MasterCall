package com.flycode.paradoxidealmaster.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.layouts.RatingLayout;
import com.flycode.paradoxidealmaster.model.IdealFeedback;
import com.flycode.paradoxidealmaster.utils.DateUtils;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

/**
 * Created by acerkinght on 9/12/16.
 */
public class MasterFeedbackViewHolder extends SuperViewHolder {
    private TextView commentTextView;
    private TextView nameTextView;
    private TextView dateTextView;
    private RatingLayout ratingLayout;

    private MasterFeedbackProvider provider;

    public static MasterFeedbackViewHolder getInstance(ViewGroup parent, Context context, MasterFeedbackProvider provider) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_master_feeback, parent, false);

        return new MasterFeedbackViewHolder(itemView, context, provider);
    }

    private MasterFeedbackViewHolder(View itemView, Context context, MasterFeedbackProvider provider) {
        super(itemView);

        this.provider = provider;

        commentTextView = (TextView) itemView.findViewById(R.id.comment);
        nameTextView = (TextView) itemView.findViewById(R.id.name);
        dateTextView = (TextView) itemView.findViewById(R.id.date);
        ratingLayout = (RatingLayout) itemView.findViewById(R.id.rating);

        nameTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK));
        dateTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK));
        commentTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK));
    }

    @Override
    public void setupForPosition(int position) {
        IdealFeedback idealFeedback = provider.getFeedbackForPosition(getAdapterPosition());

        commentTextView.setText(idealFeedback.getComments().get(0).getComment());
        nameTextView.setText(idealFeedback.getUserFullName());
        dateTextView.setText(DateUtils.birthdayDateFormat(idealFeedback.getComments().get(0).getUpdated()));
        ratingLayout.setCurrentRating((int) Math.round(idealFeedback.getRating()));
    }

    public interface MasterFeedbackProvider {
        IdealFeedback getFeedbackForPosition(int position);
    }
}
