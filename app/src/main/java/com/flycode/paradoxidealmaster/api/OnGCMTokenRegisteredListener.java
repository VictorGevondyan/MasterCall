package com.flycode.paradoxidealmaster.api;

/**
 * Created by anhaytananun on 25.12.15.
 */
public interface OnGCMTokenRegisteredListener {
    void onGCMTokenRegistrationSuccess(String registrationId);
    void onGCMTokenRegistrationFailure(int statusCode);
}
