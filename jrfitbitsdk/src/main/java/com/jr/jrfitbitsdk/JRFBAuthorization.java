package com.jr.jrfitbitsdk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.List;
import java.util.Random;

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
    private static final String PARAM_EXPIRES = "expires_in";

    private static final String EXPIRE_TIME = "2592000";
    private static final String CODE_CHALLENGE = "code_challenge";
    private static final String CODE_CHALLENGE_METHOD = "code_challenge_method";
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String encodeBySHA256(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            messageDigest.update(str.getBytes());
            Log.e("code_challenge", getFormattedText(messageDigest.digest()));
            return getFormattedText(messageDigest.digest());

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

    }

    /**
     *      * Takes the raw bytes from the digest and formats them correct.
     *      * @param bytes the raw bytes from the digest.
     *      * @return the formatted bytes.
     *     
     */
    private static String getFormattedText(byte[] bytes) {

        int len = bytes.length;

        StringBuilder buf = new StringBuilder(len * 2);


        for (int j = 0; j < len; j++) {

            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);

            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);

        }
        return buf.toString();
    }

    public void login(Activity activity, Collection<String> scope,String code_verifier) {

        Uri builtUri = null;
            builtUri = Uri.parse(BASE_AUTH_URL)
                    .buildUpon()
                    .appendQueryParameter(PARAM_RESPONSE_TYPE, "code")
                    .appendQueryParameter(PARAM_CLIENT_ID, JRFitbitSDK.clientId)
                    .appendQueryParameter(PARAM_REDIRECT_URI, JRFitbitSDK.callbackUrl)
                    .appendQueryParameter(PARAM_SCOPE, TextUtils.join(" ", scope))
                    .appendQueryParameter(CODE_CHALLENGE,code_verifier)
                    .appendQueryParameter(CODE_CHALLENGE_METHOD, "plain")
    //                .appendQueryParameter(CODE_CHALLENGE_METHOD, "plain")
                    .build();


        Log.i("Auth login1", builtUri.toString());
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(builtUri);
        activity.startActivity(i);

    }

    public static String getRandomString(int length) {
        String str = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(10);
            sb.append(str.charAt(number));
        }
        Log.e("code_challenge",sb.toString());
        return sb.toString();
    }

    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        Log.e("code_challenge",sbu.toString());
        return sbu.toString();
    }

    public JRFBAuthToken handleCallbackData(Intent intent) {

        JRFBAuthToken authToken = null;
        Uri uri = intent.getData();
        String encodedFragment = uri.getEncodedFragment();
        String code=uri.getQueryParameter("code");
        Log.i("code", code);
        return authToken;
    }

}
