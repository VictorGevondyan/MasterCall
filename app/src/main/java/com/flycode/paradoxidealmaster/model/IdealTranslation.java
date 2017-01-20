package com.flycode.paradoxidealmaster.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.flycode.paradoxidealmaster.settings.AppSettings;

import io.realm.RealmObject;

/**
 * Created by acerkinght on 1/11/17.
 */

public class IdealTranslation extends RealmObject implements Parcelable {
    private String base;
    private String en;
    private String ru;
    private String am;

    public IdealTranslation() {

    }

    protected IdealTranslation(Parcel in) {
        base = in.readString();
        en = in.readString();
        ru = in.readString();
        am = in.readString();
    }

    public static final Creator<IdealTranslation> CREATOR = new Creator<IdealTranslation>() {
        @Override
        public IdealTranslation createFromParcel(Parcel in) {
            return new IdealTranslation(in);
        }

        @Override
        public IdealTranslation[] newArray(int size) {
            return new IdealTranslation[size];
        }
    };

    public String getTranslationForLocale(Context context) {
        String locale = AppSettings.sharedSettings(context).getLanguage();
        String en = getEn();
        String ru = getRu();
        String am = getAm();

        if (en != null && !en.isEmpty() && locale.equals(AppSettings.LANGUAGES.EN)) {
            return en;
        }

        if (ru != null && !ru.isEmpty() && locale.equals(AppSettings.LANGUAGES.RU)) {
            return ru;
        }

        if (am != null && !am.isEmpty() && locale.equals(AppSettings.LANGUAGES.HY)) {
            return am;
        }

        return getBase();
    }

    public boolean containsString(String string) {
        String en = getEn();
        String ru = getRu();
        String am = getAm();
        String base = getBase();

        if (en != null && en.toLowerCase().contains(string)) {
            return true;
        }

        if (ru != null && ru.toLowerCase().contains(string)) {
            return true;
        }

        if (am != null && am.toLowerCase().contains(string)) {
            return true;
        }

        if (base.toLowerCase().contains(string)) {
            return true;
        }

        return false;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getAm() {
        return am;
    }

    public void setAm(String am) {
        this.am = am;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(base);
        parcel.writeString(en);
        parcel.writeString(ru);
        parcel.writeString(am);
    }
}
