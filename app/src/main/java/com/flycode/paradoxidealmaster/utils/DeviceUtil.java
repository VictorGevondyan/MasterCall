package com.flycode.paradoxidealmaster.utils;

import android.content.Context;
import android.provider.Settings;

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
}
