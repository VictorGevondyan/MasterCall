package com.idealsystems.idealmaster.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.idealsystems.idealmaster.model.IdealMasterService;
import com.idealsystems.idealmaster.model.IdealService;
import com.idealsystems.idealmaster.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by victor on 12/25/15.
 */
public class UserData {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static UserData sharedData;

    private static final String ID = "_id";
    private static final String USERNAME = "username";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String IMAGE = "image";
    private static final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String BALANCE = "balance";
    private static final String RATING = "rating";
    private static final String SEX = "sex";
    private static final String STICKER = "sticker";
    private static final String PORTFOLIO = "portfolio";

    private final SharedPreferences dataPreferences;

    private String id;
    private String name;
    private String surname;
    private String username;
    private String image;
    private Date dateOfBirth;
    private ArrayList<String> portfolio;
    private int balance;
    private float rating;
    private boolean sex;
    private boolean sticker;

    public static UserData sharedData(Context context) {
        if (sharedData == null) {
            sharedData = new UserData(context);
        }

        return sharedData;
    }

    private UserData(Context context) {
        String PREFERENCES_NAME = "DriverDataPreferences";
        dataPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        id = dataPreferences.getString(ID, "");
        username = dataPreferences.getString(USERNAME, "");
        name = dataPreferences.getString(NAME, "");
        surname = dataPreferences.getString(SURNAME, "");
        image = dataPreferences.getString(IMAGE, "");
        portfolio = new ArrayList<>(dataPreferences.getStringSet(PORTFOLIO, new HashSet<String>()));
        balance = dataPreferences.getInt(BALANCE, 0);
        rating = dataPreferences.getFloat(RATING, 0);
        sex = dataPreferences.getBoolean(SEX, true);
        sticker = dataPreferences.getBoolean(STICKER, false);

        long dateTime = dataPreferences.getLong(DATE_OF_BIRTH, -1);

        if (dateTime >= 0) {
            dateOfBirth = new Date(dateTime);
        }
    }

    public boolean storeUser(final User user, String requiredRole) {
        if (!user.getRole().equals(requiredRole)) {
            return false;
        }

        username = user.getUsername();
        id = user.getId();
        name = user.getName();
        surname = user.getSurname();
        image = user.getImage();
        portfolio = user.getPortfolio();
        balance = user.getBalance();
        rating = user.getFeedback() != null ? user.getFeedback().getStars() : 0;
        sex = user.isSex();
        dateOfBirth = user.getDateOfBirth();
        sticker = user.isSticker();

        dataPreferences
                .edit()
                .putString(ID, id)
                .putString(USERNAME, username)
                .putString(NAME, name)
                .putString(SURNAME, surname)
                .putString(IMAGE, image)
                .putLong(DATE_OF_BIRTH, dateOfBirth == null ? -1 : dateOfBirth.getTime())
                .putStringSet(PORTFOLIO, new HashSet<>(portfolio))
                .putInt(BALANCE, balance)
                .putFloat(RATING, rating)
                .putBoolean(SEX, sex)
                .putBoolean(STICKER, sticker)
                .apply();

        Realm
                .getDefaultInstance()
                .executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<IdealService> rootServices = realm
                                .where(IdealService.class)
                                .findAll();

                        for (IdealService service : rootServices) {
                            if (service.isFinal()) {
                                for (IdealMasterService masterService : user.getServices()) {
                                    if (masterService.getId().equals(service.getId())) {
                                        masterService.setColor(service.getColor());
                                    }
                                }
                            }
                        }

                        realm.insertOrUpdate(user.getServices());
                    }
                });

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

    public String getImage() {
        return image;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public ArrayList<String> getPortfolio() {
        return portfolio;
    }

    public int getBalance() {
        return balance;
    }

    public float getRating() {
        return rating;
    }

    public boolean isSex() {
        return sex;
    }

    public boolean isSticker() {
        return sticker;
    }
}


















