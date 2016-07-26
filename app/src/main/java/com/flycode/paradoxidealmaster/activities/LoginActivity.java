package com.flycode.paradoxidealmaster.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.api.APITalker;
import com.flycode.paradoxidealmaster.api.OnGetUserListener;
import com.flycode.paradoxidealmaster.api.OnLoginListener;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

public class LoginActivity extends Activity implements OnLoginListener, OnGetUserListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface icomoonTypeface = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON);
        Typeface avenirLightTypeface = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_LIGHT);
        Typeface avenirBlackTypeface = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_BLACK);

        TextView passwordIconTextView = (TextView) findViewById(R.id.icon_username);
        TextView lockIconTextView = (TextView) findViewById(R.id.icon_lock);

        passwordIconTextView.setTypeface(icomoonTypeface);
        lockIconTextView.setTypeface(icomoonTypeface);

        TextView masterTextView = (TextView) findViewById(R.id.master);
        TextView welcomeIconTextView = (TextView) findViewById(R.id.welcome);

        masterTextView.setTypeface(avenirBlackTypeface);
        welcomeIconTextView.setTypeface(avenirBlackTypeface);

        EditText nameEditText = (EditText) findViewById(R.id.name);
        EditText passwordEditText = (EditText) findViewById(R.id.password);

        nameEditText.setTypeface(avenirLightTypeface);
        passwordEditText.setTypeface(avenirLightTypeface);

        Button signInButton = (Button) findViewById(R.id.sign_in);
        signInButton.setTypeface(avenirBlackTypeface);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameEditText = (EditText) findViewById(R.id.name);
                EditText passwordEditText = (EditText) findViewById(R.id.password);

                APITalker
                        .sharedTalker()
                        .login(
                            LoginActivity.this,
                            nameEditText.getText().toString(),
                            passwordEditText.getText().toString(),
                            LoginActivity.this);
            }
        });
    }

    @Override
    public void onLoginSuccess() {
        APITalker
                .sharedTalker()
                .getUser(getApplicationContext(), this);
    }

    @Override
    public void onLoginFailure(int status) {

    }

    @Override
    public void onGetUserSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    public void onGetUserFailure(int status) {

    }
}
