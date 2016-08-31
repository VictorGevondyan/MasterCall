package com.flycode.paradoxidealmaster.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.body.LoginBody;
import com.flycode.paradoxidealmaster.dialogs.LoadinProgressDialog;
import com.flycode.paradoxidealmaster.model.AuthToken;
import com.flycode.paradoxidealmaster.model.User;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.settings.UserData;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends SuperActivity implements View.OnClickListener {

    private LoadinProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loading = new LoadinProgressDialog(this);

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
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        EditText nameEditText = (EditText) findViewById(R.id.name);
        EditText passwordEditText = (EditText) findViewById(R.id.password);
        loading.show();

        LoginBody loginBody = new LoginBody(
                nameEditText.getText().toString(),
                passwordEditText.getText().toString());


        APIBuilder
                .getIdealAPI()
                .login(loginBody)
                .enqueue(new Callback<AuthToken>() {
                    @Override
                    public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                        if (response.isSuccessful()) {
                            AppSettings appSettings = AppSettings.sharedSettings(LoginActivity.this);
                            appSettings.setToken(response.body().getToken());
                            appSettings.setIsUserLoggedIn(true);

                            loadUser();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthToken> call, Throwable t) {
                        Log.d("Logging", "Jogging");
                    }
                });
    }

    private void loadUser() {
        APIBuilder
                .getIdealAPI()
                .getUser(AppSettings.sharedSettings(this).getBearerToken())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            boolean permittedUser = UserData.sharedData(LoginActivity.this).storeUser(response.body(), "master");
                            loading.dismiss();

                            if (permittedUser) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d("Bas Ass", "Very bad ass");
                    }
                });
    }
}
