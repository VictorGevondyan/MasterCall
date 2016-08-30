package com.flycode.paradoxidealmaster.api;

import com.flycode.paradoxidealmaster.api.body.GCMBody;
import com.flycode.paradoxidealmaster.api.body.LocationBody;
import com.flycode.paradoxidealmaster.api.body.LoginBody;
import com.flycode.paradoxidealmaster.api.body.ProfileBody;
import com.flycode.paradoxidealmaster.api.response.OrderResponse;
import com.flycode.paradoxidealmaster.api.response.OrdersListResponse;
import com.flycode.paradoxidealmaster.api.response.SimpleOrderResponse;
import com.flycode.paradoxidealmaster.model.AuthToken;
import com.flycode.paradoxidealmaster.model.IdealService;
import com.flycode.paradoxidealmaster.model.User;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by acerkinght on 7/28/16.
 */
public interface APIService {
    @POST("auth/local")
    Call<AuthToken> login(@Body LoginBody loginBody);

    @GET("/api/users/me")
    Call<User> getUser(@Header("Authorization") String authToken);

    @POST("/api/devices")
    Call<Void> registerGCMToken(@Header("Authorization") String authToken, @Body GCMBody gcmBody);

    @POST("/api/points")
    Call<Void> updateLocation(@Header("Authorization") String authToken, @Body LocationBody locationBody);

    @GET("/api/orders/own")
    Call<OrdersListResponse> getOrders(@Header("Authorization") String authToken,
                                       @Query("updated[start]") Date startDate,
                                       @Query("updated[end]") Date endDate,
                                       @Query("status")String[] statuses,
                                       @Query("onlyCount") boolean onlyCount);

    @GET("/api/orders/{orderId}")
    Call<OrderResponse> getOrder(@Header("Authorization") String authToken, @Path("orderId") String orderId);

    @PUT("/api/orders/{orderId}/{action}")
    Call<SimpleOrderResponse> makeOrderAction(@Header("Authorization") String authToken, @Path("orderId") String orderId, @Path("action") String action);
}