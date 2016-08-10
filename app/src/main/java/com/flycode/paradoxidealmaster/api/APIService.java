package com.flycode.paradoxidealmaster.api;

import com.flycode.paradoxidealmaster.api.body.GCMBody;
import com.flycode.paradoxidealmaster.api.body.LocationBody;
import com.flycode.paradoxidealmaster.api.body.LoginBody;
import com.flycode.paradoxidealmaster.model.AuthToken;
import com.flycode.paradoxidealmaster.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by acerkinght on 7/28/16.
 */
public interface APIService {
    String BASE_URL = "http://192.168.0.111:1994";

    @POST("auth/local")
    Call<AuthToken> login(@Body LoginBody loginBody);

    @GET("/api/users/me")
    Call<User> getUser(@Header("Authorization") String authToken);

    @POST("/api/devices")
    Call<Void> registerGCMToken(@Header("Authorization") String authToken, @Body GCMBody gcmBody);

    @POST("/api/points")
    Call<Void> updateLocation(@Header("Authorization") String authToken, @Body LocationBody locationBody);
}
