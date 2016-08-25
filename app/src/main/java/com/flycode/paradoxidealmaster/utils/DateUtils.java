package com.flycode.paradoxidealmaster.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by acerkinght on 8/22/16.
 */
public class DateUtils {
    private static SimpleDateFormat orderDateFormat;

    static {
        orderDateFormat = new SimpleDateFormat("dd/MM/yyyy'  'HH:mm", Locale.US);
//        orderDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String orderDateStringFromDate(Date date) {
        return orderDateFormat.format(date);
    }
}
