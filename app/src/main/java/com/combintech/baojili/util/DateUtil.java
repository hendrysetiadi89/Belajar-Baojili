package com.combintech.baojili.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Hendry Setiadi
 */

public class DateUtil {

    public static final String FORMAT_YYYY_MM_DD_STRIPE = "yyyy-MM-dd";
    public static final String FORMAT_D_MMMM_YYYY = "d MMMM yyyy";
    public static final String FORMAT_YYYY_MM_DD_STRIPE_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_EEE_D_MM_YYYY_HH_mm_ss = "EEE, d MM yyyy - HH:mm:ss";
    public static final String FORMAT_EEE_D_MM_YYYY_HH_mm = "EEE, d MM yyyy - HH:mm";

    private static final Locale DEFAULT_LOCALE = new Locale("in", "id");

    @SuppressLint("SimpleDateFormat")
    public static String getStringDateAfter(int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, count);
        Date newDate = calendar.getTime();
        return new SimpleDateFormat("dd/MM/yyyy").format(newDate);
    }

    public static String formatDate(String currentFormat, String newFormat, String dateString){
        return formatDate(currentFormat, newFormat, dateString, DEFAULT_LOCALE);
    }

    private static String formatDate(String currentFormat, String newFormat, String dateString, Locale locale){

        try{
            DateFormat fromFormat = new SimpleDateFormat(currentFormat, locale);
            fromFormat.setLenient(false);
            DateFormat toFormat = new SimpleDateFormat(newFormat, locale);
            toFormat.setLenient(false);
            Date date = fromFormat.parse(dateString);
            return toFormat.format(date);
        }catch (Exception e){
            e.printStackTrace();
            return dateString;
        }

    }
}
