package com.flycode.paradoxidealmaster.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by acerkinght on 8/22/16.
 */
public class DateUtils {
    private static SimpleDateFormat orderDateFormat;
    private static SimpleDateFormat birthdayDateFormat;

    static {
        orderDateFormat = new SimpleDateFormat("dd/MM/yyyy'  'HH:mm", Locale.US);
        birthdayDateFormat = new SimpleDateFormat("MMMM dd',' yyyy", Locale.US);
    }

    public static String infoDateStringFromDate(Date date) {
        return orderDateFormat.format(date);
    }

    public static String birthdayDateFormat(Date date) {
        return birthdayDateFormat.format(date);
    }
}
