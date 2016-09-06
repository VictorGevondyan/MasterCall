package com.flycode.paradoxidealmaster.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.fragments.MasterProfileFragment;
import com.flycode.paradoxidealmaster.fragments.MasterServicesFragment;
import com.flycode.paradoxidealmaster.model.User;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.settings.UserData;
import com.meg7.widget.SvgImageView;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends SuperActivity implements ViewPager.OnPageChangeListener {
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingsViewPagerAdapter adapter = new SettingsViewPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);

        if (viewPager.getCurrentItem() == 0) {
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.green));
        } else {
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.ideal_red));
        }

        APIBuilder
                .getIdealAPI()
                .getUser(AppSettings.sharedSettings(this).getBearerToken())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, final Response<User> response) {
                        if (!response.isSuccessful()) {
                            return;
                        }

                        UserData
                                .sharedData(SettingsActivity.this)
                                .storeUser(response.body(), "master");

                        Realm
                                .getDefaultInstance()
                                .executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.insertOrUpdate(response.body().getServices());
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            return;
        }

        final SvgImageView imageView = (SvgImageView) findViewById(R.id.master_image);
        Glide
                .with(this)
                .load(APIBuilder.getImageUrl(imageView.getMeasuredWidth(), imageView.getMeasuredHeight(), UserData.sharedData(this).getImage()))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(resource);
                        imageView.invalidate();
                    }
                });
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_down_out);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.green));
        } else {
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.ideal_red));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class SettingsViewPagerAdapter extends FragmentStatePagerAdapter {

        public SettingsViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new MasterProfileFragment();
            } else {
                return new MasterServicesFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.profile);
            } else {
                return getString(R.string.pricing);
            }
        }
    }
}
