package com.flycode.paradoxidealmaster.api.body;

import android.content.Context;
import android.os.Build;

import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.utils.DeviceUtil;

/**
 * Created by acerkinght on 7/28/16.
 */
public class GCMBody {
    private String token;
    private String deviceId;
    private String type;
    private String osVersion;
    private String deviceInfo;
    private String language;

    public GCMBody(String token, Context context) {
        this.token = token;
        this.deviceId = DeviceUtil.getDeviceId(context) + "_master";
        this.type = "android";
        this.osVersion = Build.VERSION.RELEASE;
        this.deviceInfo = Build.MANUFACTURER + " " + Build.MODEL;
        this.language = AppSettings.sharedSettings(context).getLanguage();

        if (this.language.equals("hy")) {
            language = "am";
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
