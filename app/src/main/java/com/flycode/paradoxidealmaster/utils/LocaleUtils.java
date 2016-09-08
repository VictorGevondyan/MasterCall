package com.flycode.paradoxidealmaster.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by anhaytananun on 23.12.15.
 */
public class LocaleUtils {
    public static void setLocale(Activity activity, String languageCode) {
        Resources resources = activity.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        android.content.res.Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(languageCode);
        resources.updateConfiguration(configuration, displayMetrics);
    }
}
