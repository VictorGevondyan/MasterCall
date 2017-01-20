package com.flycode.paradoxidealmaster.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

/**
 * Created by victor on 12/8/15.
 */
public class AppSettings {
    public static class LANGUAGES {
        public static final String EN = "en";
        public static final String HY = "hy";
        public static final String RU = "ru";
    }

    private static AppSettings sharedSettings;

    private final String PREFERENCES_NAME = "appSettingsPreferencesMaster";
    private final String IS_USER_LOGGED_IN = "isUserLoggedIn";
    private final String TOKEN = "token";
    private final String LANGUAGE = "language";

    private final SharedPreferences settingsPreferences;

    private boolean isUserLoggedIn;

    private String token;
    private String language;

    public static AppSettings sharedSettings(Context context) {
        if (sharedSettings == null) {
            sharedSettings = new AppSettings(context);
        }

        return sharedSettings;
    }

    private AppSettings(Context context) {
        settingsPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);

        String defaultLanguage = Locale.getDefault().getLanguage();

        if (!defaultLanguage.equals(LANGUAGES.EN)
                && !defaultLanguage.equals(LANGUAGES.HY)
                && !defaultLanguage.equals(LANGUAGES.RU)) {
            defaultLanguage = LANGUAGES.EN;
        }

        isUserLoggedIn = settingsPreferences.getBoolean(IS_USER_LOGGED_IN, false);
        token = settingsPreferences.getString(TOKEN, "");
        language = settingsPreferences.getString(LANGUAGE, defaultLanguage);
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public void setIsUserLoggedIn(boolean isUserLoggedIn) {
        this.isUserLoggedIn = isUserLoggedIn;
        settingsPreferences
                .edit()
                .putBoolean(IS_USER_LOGGED_IN, isUserLoggedIn)
                .commit();
    }

    public String getBearerToken() {
        return "Bearer " + token;
    }

    public void setToken(String token) {
        this.token = token;
        settingsPreferences
                .edit()
                .putString(TOKEN, token)
                .commit();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        settingsPreferences
                .edit()
                .putString(LANGUAGE, language)
                .commit();
    }
}
