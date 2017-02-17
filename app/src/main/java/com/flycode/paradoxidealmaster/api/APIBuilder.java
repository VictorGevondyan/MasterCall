package com.flycode.paradoxidealmaster.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 7/28/16 __ acerkinght .
 */
public class APIBuilder {
//   private static String BASE_URL = "http://192.168.0.111:1999";
//   private static String BASE_URL = "http://192.168.0.111:1994";
//   private static String BASE_URL = "http://192.168.0.101:1994";
//    private static String BASE_URL = "http://192.168.0.110:1994";
//    private static String BASE_URL = "http://fly.co.de:1994";
    private static final String BASE_URL = "http://31.187.70.102:1994";
//    private static final String BASE_URL = "http://109.75.45.248:1999";
//    private static final String BASE_URL = "http://37.252.85.80:1999";
//    private static final String BASE_URL = "http://192.168.0.114:1994";

    private static APIService idealAPI;

    public static APIService getIdealAPI() {
        if (idealAPI == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new GsonUTCDateAdapter())
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

    public static String getImageUrl(int width, int height, String image) {
        return BASE_URL + "/api/images/" + width + "/" + height + "/" + image;
    }

    public static String getImageUrl(String image) {
        return BASE_URL + "/assets/images/upload/" + image;
    }

    private static class GsonUTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
        private final DateFormat dateFormat;

        public GsonUTCDateAdapter() {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(dateFormat.format(date));
        }

        @Override public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            try {
                return dateFormat.parse(jsonElement.getAsString());
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }
    }
}
