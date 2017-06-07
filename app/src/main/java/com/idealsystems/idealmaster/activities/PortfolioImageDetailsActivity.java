package com.idealsystems.idealmaster.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.idealsystems.idealmaster.R;
import com.idealsystems.idealmaster.adapters.viewholders.MasterPortfolioViewHolder;
import com.idealsystems.idealmaster.api.APIBuilder;
import com.idealsystems.idealmaster.dialogs.LoadingProgressDialog;
import com.idealsystems.idealmaster.utils.TypefaceLoader;

import java.util.ArrayList;

public class PortfolioImageDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private MasterPortfolioViewHolder.PortfolioProvider provider;
    private int imageSize;
    private LoadingProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_image_detailes);

        loading = new LoadingProgressDialog(this);

        setupActionBar();

        Button backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(this);
        backButton.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON, this));

        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        imageSize = metrics.widthPixels / 3;

       loadImage();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            onBackPressed();
        }
    }

    private void setupActionBar() {
        ImageView actionBarBackgroundImageView = (ImageView) findViewById(R.id.action_background);
        actionBarBackgroundImageView.setImageResource(R.drawable.new_orders_background);

        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(R.string.portfolio);
        titleTextView.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_MEDIUM, this));

        Button backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(this);
        backButton.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON, this));
    }

    private void loadImage() {
        Intent intent = getIntent();
        int portfolioImagePosition = intent.getIntExtra(MasterPortfolioViewHolder.EXTRA_MESSAGE_POSITION, 0);
        ArrayList<String> portfolio = intent.getStringArrayListExtra(MasterPortfolioViewHolder.EXTRA_MESSAGE_PORTFOLIO);

        ImageView portfolioImageDetails = (ImageView) findViewById(R.id.portfolio_image_details);

        loading.show();
        Glide
                .with(this)
                .load(APIBuilder.getImageUrl(portfolio.get(portfolioImagePosition)))
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        loading.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        loading.dismiss();
                        return false;
                    }
                })
                .into(portfolioImageDetails);
    }
}
