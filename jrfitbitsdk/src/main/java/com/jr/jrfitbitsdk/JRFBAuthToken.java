package com.jr.jrfitbitsdk;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jithin Roy on 1/2/16.
 */
public class JRFBAuthToken {

    private static final String PREFS_NAME = "Fitbit_Pref";
    private static final String PREFS_TOKEN_KEY = "Fitbit_Token";
    private static final String PREFS_EXPIRES_KEY = "Fitbit_Expires";
    private static final String PREFS_USER_ID = "Fitbit_User_ID";

    private String authToken = null;
    private String expireTime = null;
    private String userId = null;

    public JRFBAuthToken(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        this.authToken = settings.getString(PREFS_TOKEN_KEY, null);
        this.expireTime = settings.getString(PREFS_EXPIRES_KEY, null);
        this.userId = settings.getString(PREFS_USER_ID, null);
    }

    public JRFBAuthToken(String authToken, String expire, String userId) {
        this.authToken = authToken;
        this.userId = userId;
        this.expireTime = expire;
    }

    public boolean isExpired() {
        return  false;
    }

    public void saveTokenDetails(Context context) {

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(PREFS_TOKEN_KEY, this.authToken);
        editor.putString(PREFS_EXPIRES_KEY, this.expireTime);
        editor.putString(PREFS_USER_ID, this.userId);

        editor.commit();
    }

    public void deleteToken(Context context) {

        this.authToken = null;
        this.expireTime = null;

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(PREFS_TOKEN_KEY);
        editor.remove(PREFS_EXPIRES_KEY);
        editor.remove(PREFS_USER_ID);
        editor.commit();

    }

    public String getUserId() {
        return this.userId;
    }

    public  String getAuthToken() {
        return this.authToken;
    }


}
