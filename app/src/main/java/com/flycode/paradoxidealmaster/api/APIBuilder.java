package com.flycode.paradoxidealmaster.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by acerkinght on 7/28/16.
 */
public class APIBuilder {
    private static String BASE_URL = "http://192.168.0.111:1999";
//    private static String BASE_URL = "http://fly.co.de:1994";
    private static APIService idealAPI;

    public static APIService getIdealAPI() {
        if (idealAPI == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();

            idealAPI = new Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(APIService.class);
        }

        return idealAPI;
    }
}
