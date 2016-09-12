package com.jr.jrfitbitsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Collection;

/**
 * Created by Jithin Roy on 12/20/15.
 */
public class JRFitbitSDK {

    public static String clientId = null;
    public static String callbackUrl;

    private static JRFitbitSDK instance = null;
    private JRFBAuthorization auth = null;
    private JRFBAuthToken token = null;

    private Context context;

    protected JRFitbitSDK() {
        // Exists only to defeat instantiation.
    }

    public static JRFitbitSDK getInstance() {
        if(instance == null) {
            instance = new JRFitbitSDK();
        }
        return instance;
    }

    public boolean isAuthorized() {
        return (this.token.getAuthToken() != null &&
                !this.token.isExpired());
    }

    public String userId() {
        if (this.token != null) {
            return this.token.getUserId();
        }
        return null;
    }

    public String authToken() {
        if (this.token != null) {
            return this.token.getAuthToken();
        }
        return null;
    }

    public void initialize(String clientId, String callbackURL, Context context) {
        JRFitbitSDK.clientId = clientId;
        JRFitbitSDK.callbackUrl = callbackURL;
       this.context = context;
        this.token = new JRFBAuthToken(this.context);
    }

    public void onRecieveIntent(Intent intent) {

        if (this.auth == null) return;
        this.token = this.auth.handleCallbackData(intent);
        if (this.token != null) {
            this.token.saveTokenDetails(this.context);
        }
        this.auth = null;
    }

    public void authorize(Activity activity, Collection<String>scope) {
        this.auth = new JRFBAuthorization();
        this.auth.login(activity,scope);
    }

    public void logout() {
        this.token.deleteToken(this.context);
    }

    //===============================================================
    //===============================================================

    public boolean isNetworkAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}