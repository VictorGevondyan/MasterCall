package com.flycode.paradoxidealmaster.api.body;

import android.content.Context;

import com.flycode.paradoxidealmaster.utils.DeviceUtil;

/**
 * Created by acerkinght on 7/28/16.
 */
public class LocationBody {
    private double[] geo;
    private String deviceId;

    public LocationBody(double latitude, double longitude, Context context) {
        this.geo = new double[] {
                latitude,
                longitude
        };
        this.deviceId = DeviceUtil.getDeviceId(context);
    }

    public double[] getGeo() {
        return geo;
    }

    public void setGeo(double[] geo) {
        this.geo = geo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
