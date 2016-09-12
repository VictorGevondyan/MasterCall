package com.flycode.paradoxidealmaster.adapters.viewholders;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.api.APIBuilder;
import java.util.ArrayList;

/**
 * Created by acerkinght on 9/12/16.
 */
public class MasterPortfolioViewHolder extends SuperViewHolder {
    private ArrayList<ImageView> imageViews;
    private PortfolioProvider provider;
    private Context context;
    int imageSize;

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
                imageViews.add((ImageView) linearLayout.getChildAt(imageIndex));
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

    public interface PortfolioProvider {
        ArrayList<String> getPortfolio();
    }
}
