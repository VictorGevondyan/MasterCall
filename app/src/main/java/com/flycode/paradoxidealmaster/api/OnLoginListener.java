package com.flycode.paradoxidealmaster.api;

/**
 * Created by anhaytananun on 05.07.16.
 */
public interface OnLoginListener {
    void onLoginSuccess();
    void onLoginFailure(int status);
}
