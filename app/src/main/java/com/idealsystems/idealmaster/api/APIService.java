package com.idealsystems.idealmaster.api;

import com.idealsystems.idealmaster.api.body.GCMBody;
import com.idealsystems.idealmaster.api.body.LocationBody;
import com.idealsystems.idealmaster.api.body.LoginBody;
import com.idealsystems.idealmaster.api.response.IdealFeedbackListResponse;
import com.idealsystems.idealmaster.api.response.OrderResponse;
import com.idealsystems.idealmaster.api.response.OrdersListResponse;
import com.idealsystems.idealmaster.api.response.SimpleOrderResponse;
import com.idealsystems.idealmaster.api.response.TransactionsListResponse;
import com.idealsystems.idealmaster.model.AuthToken;
import com.idealsystems.idealmaster.model.IdealService;
import com.idealsystems.idealmaster.model.User;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @GET ("/api/transactions/own")
    Call<TransactionsListResponse> getTransactions(@Header("Authorization") String authToken,
                                                   @Query("date[start]") Date startDate,
                                                   @Query("date[end]") Date endDate);

    @GET("api/services?noPopulate=true")
    Call<ArrayList<IdealService>> getServices(@Header("Authorization") String authToken);

    @GET("/api/comments/master/{masterId}?limit=3")
    Call<IdealFeedbackListResponse> getFeedbackByMaster(@Header("Authorization") String authToken, @Path("masterId") String masterId);

    @DELETE("/api/devices/{token}")
    Call<Void> deleteToken(@Header("Authorization") String authToken, @Path("token") String tokenId);
}