package com.flycode.paradoxidealmaster.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

import java.util.ArrayList;

/**
 * Created by acerkinght on 8/30/16.
 */
public class RatingLayout extends LinearLayout implements View.OnClickListener {
    private static final int MAX_RATING = 5;

    private OnRatingChangedListener listener;
    private float ratingSize = 16;
    private int currentRating = 0;
    private boolean isEditable = false;
    private ArrayList<Button> ratingButtons = new ArrayList<>();

    public RatingLayout(Context context) {
        super(context);
        init();
    }

    public RatingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RatingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setOrientation(LinearLayout.HORIZONTAL);

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int index = 0 ; index < MAX_RATING ; index++) {
            Button button = (Button) layoutInflater.inflate(R.layout.section_rate, this, false);
            button.setTag(R.layout.section_rate, index);
            button.setTextSize(ratingSize);
            button.setTypeface(TypefaceLoader.loadTypeface(getContext().getAssets(), TypefaceLoader.ICOMOON));
            button.setOnClickListener(this);

            if (currentRating > index) {
                button.setText(R.string.icon_empty_star);
            } else {
                button.setText(R.string.icon_star);
            }

            addView(button);
            ratingButtons.add(button);
        }
    }

    @Override
    public void onClick(View view) {
        if (!isEditable) {
            return;
        }

        setCurrentRating((Integer) view.getTag(R.layout.section_rate));

        if (listener != null) {
            listener.onRatingChanged(this, currentRating);
        }
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public void setCurrentRating(int currentRating) {
        this.currentRating = currentRating;

        for (int index = 0 ; index < MAX_RATING ; index++) {
            Button button = ratingButtons.get(index);

            if (currentRating > index) {
                button.setText(R.string.icon_star);
            } else {
                button.setText(R.string.icon_empty_star);
            }
        }
    }

    public void setRatingSize(float ratingSize) {
        this.ratingSize = ratingSize;

        for (Button button : ratingButtons) {
            button.setTextSize(ratingSize);
        }
    }

    public interface OnRatingChangedListener {
        void onRatingChanged(RatingLayout ratingLayout, int currentRating);
    }
}
