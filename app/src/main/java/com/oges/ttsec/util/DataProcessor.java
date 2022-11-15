package com.oges.ttsec.util;

import android.content.Context;
import android.content.SharedPreferences;

public class DataProcessor {

    private static Context context;
    //private static SharedPreferences sharedPref;
    public final static String PREFS_NAME = "ttsec_prefs";
    //private static SharedPreferences.Editor editor;

    public DataProcessor(Context context) {
        this.context = context;
    }

    public static void setToken(String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getToken(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
       // return prefs.getString("TOKEN", "DNF");
        return prefs.getString("USERID", null);
    }

    public static void setUserId(String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getUserId(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // return prefs.getString("TOKEN", "DNF");
        return prefs.getString("USERID", null);
    }

    public static void setCompanyId(String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getCompanyId(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // return prefs.getString("TOKEN", "DNF");
        return prefs.getString("COMPANYID", null);
    }
    public static String getEventId(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // return prefs.getString("TOKEN", "DNF");
        return prefs.getString("EVENTID", null);
    }

    public static void setEventId(String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getImeIId(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // return prefs.getString("TOKEN", "DNF");
        return prefs.getString("IMEIID", null);
    }

    public static void setImeIId(String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getVerificationCode(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // return prefs.getString("TOKEN", "DNF");
        return prefs.getString("VERIFICATIONCODE", null);
    }

    public static void setVerificationCode(String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }






    public static String getflashCode(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // return prefs.getString("TOKEN", "DNF");
        return prefs.getString("FLASH", "1");
    }




    public static void setflashCode(String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }






    public static String getDeviceStatus(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // return prefs.getString("TOKEN", "DNF");
        return prefs.getString("DEVICESTATUS", null);
    }

    public static void setDeviceStatus(String key, String value) {  // setting zone access on or off
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getConfigStatus(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // return prefs.getString("TOKEN", "DNF");
        return prefs.getString("CONFIG_STATUS", "");
    }


    public static String getContactStatus(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // return prefs.getString("TOKEN", "DNF");
        return prefs.getString("CONTACT_STATUS", "0");
    }

    public static void setContactStatus(String key, String value) { //To set the check in check out config
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }



    public static void setConfigStatus(String key, String value) { //To set the check in check out config
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

     public static void clear() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        editor.commit();
    }
}
