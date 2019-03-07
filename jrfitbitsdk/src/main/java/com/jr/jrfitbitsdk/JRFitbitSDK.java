package com.jr.jrfitbitsdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jr.jrfitbitsdk.model.CodeBean;
import com.jr.jrfitbitsdk.parser.APIContants;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;


import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;


/**
 * Created by Jithin Roy on 12/20/15.
 */
public class JRFitbitSDK {
    public final static String CODESTRING = "codeString";
    private static final String PREFS_NAME = "fitbit_pref";
    public static String clientId = null;
    public static String callbackUrl;
    public static String authorization;
    public static String code_verifier;

    private static JRFitbitSDK instance = null;
    SharedPreferences settings;
    private JRFBAuthorization auth = null;
    private JRFBAuthToken token = null;
    private CodeBean codeBean = null;
    private Context context;
    private List<APICallback> callbacks = new ArrayList<>();

    protected JRFitbitSDK() {
        // Exists only to defeat instantiation.
    }

    public static JRFitbitSDK getInstance() {
        if (instance == null) {
            instance = new JRFitbitSDK();
        }
        return instance;
    }

    public boolean isAuthorized() {
        return codeBean != null && (!TextUtils.isEmpty(codeBean.getAccess_token()));
    }

    public String userId() {
        if (this.codeBean != null) {
            return this.codeBean.getUser_id();
        }
        return null;
    }

    public String authToken() {
        if (this.codeBean != null) {
            return this.codeBean.getAccess_token();
        }
        return null;
    }

    public void initialize(String clientId, String callbackURL, String authorization, String code_verifier, Application context) {
        JRFitbitSDK.clientId = clientId;
        JRFitbitSDK.callbackUrl = callbackURL;
        JRFitbitSDK.code_verifier = code_verifier;
        this.context = context.getApplicationContext();
        JRFitbitSDK.authorization = authorization;
        this.token = new JRFBAuthToken(this.context.getApplicationContext());
        settings = context.getSharedPreferences(PREFS_NAME, 0);
        initOkGo(context, authorization);
        String codeString = settings.getString(CODESTRING, null);
        if (!TextUtils.isEmpty(codeString)) {
            codeBean = new Gson().fromJson(codeString, CodeBean.class);
        }
    }


    private void initOkGo(Application context, String authorization) {
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", "Basic " + authorization.trim());    //header不支持中文，不允许有特殊字符

        headers.put("Content-Type", "application/x-www-form-urlencoded");    //header不支持中文，不允许有特殊字符
        Log.e("authorization", headers.toJSONString());
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(context)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        OkGo.getInstance().init(context)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers);              //全局公共头
    }

    public void onRecieveIntent(Intent intent) {

        if (this.auth == null) return;
        this.token = this.auth.handleCallbackData(intent);
        if (this.token != null) {
            this.token.saveTokenDetails(this.context);
        }
        this.auth = null;
    }

    public void getCode(Intent intent) {
        Uri uri = intent.getData();
        String encodedFragment = uri.getEncodedFragment();
        String code = uri.getQueryParameter("code");
        if (code != null) {
            HttpParams params = new HttpParams();
            params.put("client_id", clientId);     //param支持中文,直接传,不要自己编码
            params.put("grant_type", "authorization_code");     //param支持中文,直接传,不要自己编码
            params.put("redirect_uri", callbackUrl);     //param支持中文,直接传,不要自己编码
            params.put("code", code);     //param支持中文,直接传,不要自己编码
            params.put("code_verifier", code_verifier);
            OkGo.<String>post("https://api.fitbit.com/oauth2/token").params(params).execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    codeBean = new Gson().fromJson(response.body(), CodeBean.class);
                    Log.e("onSuccess", new Gson().toJson(codeBean) + response.body());

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(CODESTRING, response.body());
                    editor.apply();

                    for (APICallback cb : callbacks) {
                        cb.onGetPkceCode(codeBean);
                    }
                }
            });
        }
    }

    public void authorize(Activity activity, Collection<String> scope) {
        this.auth = new JRFBAuthorization();
        this.auth.login(activity, scope, code_verifier);
    }

    public void logout() {
        this.codeBean = null;
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(CODESTRING, "");
        editor.apply();
    }

    //===============================================================
    //===============================================================

    public boolean isNetworkAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void addCallback(APICallback callback) {
        callbacks.add(callback);
    }

    public void refreshToken() {
        HttpParams params = new HttpParams();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", codeBean.getRefresh_token());
        params.put("expires_in", "28800");
        OkGo.<String>post(APIContants.tokenAPI).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                codeBean = new Gson().fromJson(response.body(), CodeBean.class);
                Log.e("onSuccess", new Gson().toJson(codeBean) + response.body());

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(CODESTRING, response.body());
                editor.apply();
                for (APICallback callback : callbacks) {
                    callback.onRefreshToken(codeBean);
                }
            }
        });
    }

    public void logWeight(String weight, String date, String time, final APICallback apiCallback) {
        HttpParams params = new HttpParams();
        params.put("weight", weight);
        params.put("date", date);
        params.put("time", time);
        OkGo.<String>post(APIContants.logWeight).params(params).headers("Authorization", "Bearer " + codeBean.getAccess_token().trim()).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                if (apiCallback != null)
                    apiCallback.onLogWeight();
            }
        });
    }

    public void logFat(String fat, String date, String time, final APICallback apiCallback) {
        HttpParams params = new HttpParams();
        params.put("fat", fat);
        params.put("date", date);
        params.put("time", time);
        OkGo.<String>post(APIContants.logFat).params(params).headers("Authorization", "Bearer " + codeBean.getAccess_token().trim()).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                if (apiCallback != null)
                    apiCallback.onLogFat();
            }
        });
    }
}