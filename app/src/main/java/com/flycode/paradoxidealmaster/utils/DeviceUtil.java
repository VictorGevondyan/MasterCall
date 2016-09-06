package com.flycode.paradoxidealmaster.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by acerkinght on 7/28/16.
 */
public class DeviceUtil {
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(
                context.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    public static float getPxForDp(Context context, float dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics() ;
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }
}
