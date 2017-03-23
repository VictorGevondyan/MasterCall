package com.idealsystems.idealmaster.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.idealsystems.idealmaster.R;
import com.idealsystems.idealmaster.activities.PortfolioImageDetailsActivity;
import com.idealsystems.idealmaster.api.APIBuilder;

import java.util.ArrayList;

/**
 * Created by acerkinght on 9/12/16.
 */
public class MasterPortfolioViewHolder extends SuperViewHolder implements View.OnClickListener{
    private ArrayList<ImageView> imageViews;
    private PortfolioProvider provider;
    private Context context;
    private int imageSize;
    public final static String EXTRA_MESSAGE_POSITION = "com.flycode.paradoxidealmaster.MESSAGE_POSITION";
    public final static String EXTRA_MESSAGE_PORTFOLIO = "com.flycode.paradoxidealmaster.MESSAGE_PORTFOLIO";

    public static MasterPortfolioViewHolder getInstance(ViewGroup parent, Context context, PortfolioProvider provider) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_portfolio, parent, false);

        return new MasterPortfolioViewHolder(itemView, context, provider);
    }

    private MasterPortfolioViewHolder(View itemView, Context context, PortfolioProvider provider) {
        super(itemView);

        this.context = context;
        this.provider = provider;

        imageViews = new ArrayList<>();

        LinearLayout container = (LinearLayout) itemView;

        for (int layoutIndex = 0 ; layoutIndex < container.getChildCount() ; layoutIndex++) {
            LinearLayout linearLayout = (LinearLayout) container.getChildAt(layoutIndex);

            for (int imageIndex = 0 ; imageIndex < linearLayout.getChildCount() ; imageIndex++) {
                ImageView imageView = (ImageView) linearLayout.getChildAt(imageIndex);
                imageView.setOnClickListener(this);
                imageView.setClickable(true);
                imageViews.add(imageView);
            }
        }

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        imageSize = metrics.widthPixels / 3;
    }

    @Override
    public void setupForPosition(int position) {
        ArrayList<String> portfolio = provider.getPortfolio();

        for (int index = 0 ; index < portfolio.size() ; index++) {
            Glide
                    .with(context)
                    .load(APIBuilder.getImageUrl(imageSize, imageSize, portfolio.get(index)))
                    .asBitmap()
                    .into(imageViews.get(index));
        }
    }


    @Override
    public void onClick(View view) {
        for (int index = 0 ; index < imageViews.size() ; index++) {
            if (imageViews.get(index).equals(view)
                    && index < provider.getPortfolio().size()) {
                Intent intent = new Intent(context, PortfolioImageDetailsActivity.class);
                int portfolioImagePosition = index;
                intent.putExtra(EXTRA_MESSAGE_POSITION, portfolioImagePosition);
                intent.putStringArrayListExtra(EXTRA_MESSAGE_PORTFOLIO, provider.getPortfolio());
                context.startActivity(intent);
            }
        }
    }


    public interface PortfolioProvider {
        ArrayList<String> getPortfolio();
    }
}
