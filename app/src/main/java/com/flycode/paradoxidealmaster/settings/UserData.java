package com.flycode.paradoxidealmaster.settings;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by victor on 12/25/15.
 */
public class UserData {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static UserData sharedData;

    private final String PREFERENCES_NAME = "DriverDataPreferences";

    private static final String ID = "_id";
    private static final String USERNAME = "username";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String BALANCE = "balance";
    private static final String SEX = "sex";

    private final SharedPreferences dataPreferences;

    private String id;
    private String name;
    private String surname;
    private String username;
    private Date dateOfBirth;
    private int balance;
    private boolean sex;

    public static UserData sharedData(Context context) {
        if (sharedData == null) {
            sharedData = new UserData(context);
        }

        return sharedData;
    }

    private UserData(Context context) {
        dataPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        id = dataPreferences.getString(ID, "");
        username = dataPreferences.getString(USERNAME, "");
        name = dataPreferences.getString(NAME, "");
        surname = dataPreferences.getString(SURNAME, "");
        balance = dataPreferences.getInt(BALANCE, 0);
        sex = dataPreferences.getBoolean(SEX, true);

        try {
            dateOfBirth = dateFormat.parse(dataPreferences.getString(DATE_OF_BIRTH, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean storeUser(JSONObject user, String requiredRole) throws JSONException {
        if (!user.getString("role").equals(requiredRole)) {
            return false;
        }

        // TODO: Make username required
        username = user.optString("username");
        id = user.getString("_id");
        name = user.optString("name");
        surname = user.optString("surname");
        balance = user.getInt("balance");
        sex = user.getBoolean("sex");

        try {
            dateOfBirth = dateFormat.parse(user.optString("dateOfBirth"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dataPreferences
                .edit()
                .putString(ID, id)
                .putString(USERNAME, username)
                .putString(NAME, name)
                .putString(SURNAME, surname)
                .putString(DATE_OF_BIRTH, user.optString("dateOfBirth"))
                .putInt(BALANCE, balance)
                .putBoolean(SEX, sex)
                .apply();

        return true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUsername() {
        return username;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public int getBalance() {
        return balance;
    }

    public boolean isSex() {
        return sex;
    }
}


















