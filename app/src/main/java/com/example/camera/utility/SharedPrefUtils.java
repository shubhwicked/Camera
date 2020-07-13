package com.example.camera.utility;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {

    private static SharedPrefUtils sharedPrefUtils;
    private final String SHARED_PREF = "SHARED_PREF";
    private final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
    private final String STAY_SHOOTING = "STAY_SHOOTING";
    private final String TOUCH_CLICK = "TOUCH_CLICK";
    private final String SAVE_DIRECTORY_PATH = "SAVE_DIRECTORY_PATH";
    private final String CITY = "CITY";
    private final String RATE_CLICKED = "RATE_CLICKED";
    private SharedPrefUtils() {

    }

    public static SharedPrefUtils getInstance() {
        if (sharedPrefUtils == null) {
            sharedPrefUtils = new SharedPrefUtils();
        }
        return sharedPrefUtils;
    }

    private SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    }

    public void setFirstLaunch(Context context, boolean value) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putBoolean(IS_FIRST_LAUNCH, value);
        editor.apply();
    }

    public boolean isFirstLaunch(Context context) {
        return getSharedPref(context).getBoolean(IS_FIRST_LAUNCH, true);
    }

    public void setStayShooting(Context context, boolean value) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putBoolean(STAY_SHOOTING, value);
        editor.apply();
    }

    public boolean isStayShooting(Context context) {
        return getSharedPref(context).getBoolean(STAY_SHOOTING, true);
    }

    public void setTouchClick(Context context, boolean value) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putBoolean(TOUCH_CLICK, value);
        editor.apply();
    }

    public boolean isTouchClick(Context context) {
        return getSharedPref(context).getBoolean(TOUCH_CLICK, false);
    }

    public void setSaveDirectoryPath(Context context, String value) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putString(SAVE_DIRECTORY_PATH, value);
        editor.apply();
    }

    public String getSaveDirectoryPath(Context context) {
        return getSharedPref(context).getString(SAVE_DIRECTORY_PATH, ImageUtility.getInstance().getFileDirectoryPath());
    }

    public void setCityName(Context context, String value) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putString(CITY, value);
        editor.apply();
    }

    public String getCityName(Context context) {
        return getSharedPref(context).getString(CITY, null);
    }

    public void setRateClicked(Context context, boolean value) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putBoolean(RATE_CLICKED, value);
        editor.apply();
    }

    public boolean isRateClicked(Context context) {
        return getSharedPref(context).getBoolean(RATE_CLICKED, false);
    }

}
