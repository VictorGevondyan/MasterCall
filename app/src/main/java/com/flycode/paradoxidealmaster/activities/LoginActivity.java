package com.flycode.paradoxidealmaster.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flycode.paradoxidealmaster.IdealMasterApplication;
import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.body.LoginBody;
import com.flycode.paradoxidealmaster.dialogs.LoadingProgressDialog;
import com.flycode.paradoxidealmaster.gcm.GCMSubscriber;
import com.flycode.paradoxidealmaster.model.AuthToken;
import com.flycode.paradoxidealmaster.model.User;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.settings.UserData;
import com.flycode.paradoxidealmaster.utils.ErrorNotificationUtil;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends SuperActivity implements View.OnClickListener {
    private static final String PASSWORD = "password";
    private static final String LOGIN = "login";
    private LoadingProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPreferences", 0);
        String password = sharedPreferences.getString(PASSWORD, "");
        String login = sharedPreferences.getString(LOGIN, "");

        if (savedInstanceState != null) {
            password = savedInstanceState.getString(PASSWORD, "");
            login = savedInstanceState.getString(LOGIN, "");
        }

        loading = new LoadingProgressDialog(this);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Typeface icomoonTypeface = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON, this);
        Typeface avenirLightTypeface = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_LIGHT, this);
        Typeface avenirBlackTypeface = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_BLACK, this);

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

        nameEditText.setText(login);
        passwordEditText.setText(password);

        nameEditText.setTypeface(avenirLightTypeface);
        passwordEditText.setTypeface(avenirLightTypeface);

        Button signInButton = (Button) findViewById(R.id.sign_in);
        signInButton.setTypeface(avenirBlackTypeface);
        signInButton.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        EditText nameEditText = (EditText) findViewById(R.id.name);
        EditText passwordEditText = (EditText) findViewById(R.id.password);

        outState.putString(LOGIN, nameEditText.getText().toString());
        outState.putString(PASSWORD, passwordEditText.getText().toString());
    }

    @Override
    public void onClick(View view) {
        EditText nameEditText = (EditText) findViewById(R.id.name);
        EditText passwordEditText = (EditText) findViewById(R.id.password);
        if (nameEditText.length() == 0 || passwordEditText.length() == 0) {
            new MaterialDialog.Builder(LoginActivity.this)
                    .title(R.string.error)
                    .content(R.string.username_password_not_empty)
                    .positiveText(R.string.ok)
                    .show();
            return;
        }
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
                        if (!response.isSuccessful()) {
                            loading.dismiss();

                            ErrorNotificationUtil.showErrorForCode(response.code(), LoginActivity.this);

                            return;
                        }

                        AppSettings appSettings = AppSettings.sharedSettings(LoginActivity.this);
                        appSettings.setToken(response.body().getToken());
                        appSettings.setIsUserLoggedIn(true);

                        loadUser();
                        IdealMasterApplication.sharedApplication().updateServices();
                    }

                    @Override
                    public void onFailure(Call<AuthToken> call, Throwable t) {
                        loading.dismiss();
                        ErrorNotificationUtil.showErrorForCode(0, LoginActivity.this);
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
                        loading.dismiss();

                        if (!response.isSuccessful()) {
                            ErrorNotificationUtil.showErrorForCode(response.code(), LoginActivity.this);
                            return;
                        }

                        boolean permittedUser = UserData.sharedData(LoginActivity.this).storeUser(response.body(), "master");

                        if (permittedUser) {
                            SharedPreferences sharedPreferences = getSharedPreferences("LoginPreferences", 0);
                            EditText nameEditText = (EditText) findViewById(R.id.name);
                            EditText passwordEditText = (EditText) findViewById(R.id.password);

                            sharedPreferences
                                    .edit()
                                    .putString(LOGIN, nameEditText.getText().toString())
                                    .putString(PASSWORD, passwordEditText.getText().toString())
                                    .apply();

                            try {
                                GCMSubscriber.registerForGcm(LoginActivity.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        ErrorNotificationUtil.showErrorForCode(0, LoginActivity.this);
                        loading.dismiss();
                    }
                });
    }
}
