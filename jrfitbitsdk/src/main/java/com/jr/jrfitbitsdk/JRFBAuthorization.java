package com.jr.jrfitbitsdk;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import android.text.TextUtils;
import android.util.Log;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

/**
 * Created by Jithin Roy on 12/20/15.
 */
public class JRFBAuthorization {

    private static final String BASE_AUTH_URL = "https://www.fitbit.com/oauth2/authorize";
    private static final String REFRESH_TOKEN_URL = "https://api.fitbit.com/oauth2/token";

    private static final String PARAM_RESPONSE_TYPE = "response_type";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_REDIRECT_URI = "redirect_uri";
    private static final String PARAM_SCOPE = "scope";
    private static final String PARAM_EXPIRES= "expires_in";

    private static final String EXPIRE_TIME = "2592000";

    public void login(Activity activity, Collection<String>scope) {

        Uri builtUri = Uri.parse(BASE_AUTH_URL)
                .buildUpon()
                .appendQueryParameter(PARAM_RESPONSE_TYPE, "token")
                .appendQueryParameter(PARAM_CLIENT_ID, JRFitbitSDK.clientId)
                .appendQueryParameter(PARAM_REDIRECT_URI, JRFitbitSDK.callbackUrl)
                .appendQueryParameter(PARAM_SCOPE, TextUtils.join(",",scope))
                .appendQueryParameter(PARAM_EXPIRES, EXPIRE_TIME)
                .appendQueryParameter("display", "touch")
                .build();

        Log.i("Auth login1",builtUri.toString());
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(builtUri);
        activity.startActivity(i);

    }

    public JRFBAuthToken handleCallbackData(Intent intent) {

        JRFBAuthToken authToken = null;
        Uri uri = intent.getData();
        String encodedFragment = uri.getEncodedFragment();
        Log.i("JRFBAuthorization",encodedFragment);
        if (encodedFragment != null) {
            String expires_in = encodedFragment.substring(encodedFragment.indexOf("&expires_in=") + 12, encodedFragment.length());
            if (expires_in.indexOf("&") > 0)
                expires_in = expires_in.substring(0,expires_in.indexOf("&"));
            String token = encodedFragment.substring(encodedFragment.indexOf("&access_token=") + 14, encodedFragment.length());
            if (token.indexOf("&") > 0)
                token = token.substring(0,token.indexOf("&"));

            String userID = encodedFragment.substring(encodedFragment.indexOf("&user_id=") + 9, encodedFragment.length());
            if (userID.indexOf("&") > 0)
                userID = userID.substring(0,userID.indexOf("&"));

            authToken = new JRFBAuthToken(token, expires_in, userID);
            Log.i("Another test",userID);
        }

        return authToken;
    }


}
