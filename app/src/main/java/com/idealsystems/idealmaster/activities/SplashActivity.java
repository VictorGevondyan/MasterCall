package com.idealsystems.idealmaster.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.idealsystems.idealmaster.R;
import com.idealsystems.idealmaster.settings.AppSettings;
import com.idealsystems.idealmaster.utils.TypefaceLoader;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView imexGroupTextView = (TextView) findViewById(R.id.imex_group);
        TextView allRightReservedTextView = (TextView) findViewById(R.id.all_right_reserved);

        Typeface avenirBookTypeface = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_BOOK, this);

        assert imexGroupTextView != null;
        assert allRightReservedTextView != null;

        imexGroupTextView.setTypeface(avenirBookTypeface);
        allRightReservedTextView.setTypeface(avenirBookTypeface);
    }

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent;
                if (AppSettings.sharedSettings(SplashActivity.this).isUserLoggedIn()) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}
