package com.flycode.paradoxidealmaster.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView imexGroupTextView = (TextView) findViewById(R.id.imex_group);
        TextView allRightReservedTextView = (TextView) findViewById(R.id.all_right_reserved);

        Typeface avenirBookTypeface = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_BOOK);

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
