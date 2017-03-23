package com.idealsystems.idealmaster.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idealsystems.idealmaster.R;
import com.idealsystems.idealmaster.layouts.RatingLayout;
import com.idealsystems.idealmaster.model.IdealFeedback;
import com.idealsystems.idealmaster.utils.DateUtils;
import com.idealsystems.idealmaster.utils.TypefaceLoader;

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

        nameTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK, context));
        dateTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK, context));
        commentTextView.setTypeface(TypefaceLoader.loadTypeface(context.getAssets(), TypefaceLoader.AVENIR_BOOK, context));
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
