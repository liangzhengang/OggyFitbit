package com.jr.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jr.jrfitbitsdk.DefaultApiCallback;
import com.jr.jrfitbitsdk.JRFBActivitySumAPI;
import com.jr.jrfitbitsdk.JRFBActivitySummaryInterface;
import com.jr.jrfitbitsdk.JRFBBaseAPI;
import com.jr.jrfitbitsdk.JRFitbitSDK;
import com.jr.jrfitbitsdk.model.JRFBActivitySummary;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;


public class HomeActivity extends AppCompatActivity implements JRFBActivitySummaryInterface {

    public String CLIENT_ID = "22DBG2";
    public String CODE_VERIFIER = "code_verifier";
    public String CLIENT_SECRET = "7c43c2a1b002108812c3f5e108e58424";

    @Override
    public void didFetchActivity(JRFBBaseAPI api, JRFBActivitySummary result) {
        Log.i("ActivityHome", "Activ = " + result.getFairlyActiveMinutes() + " " + result.getLightlyActiveMinutes());
    }

    @Override
    public void didFailFetchingActivity(JRFBBaseAPI api) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CODE_VERIFIER = getRandomString(43);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> list = new ArrayList<String>();
                list.add("activity");
                list.add("weight");
                list.add("nutrition");
                if (JRFitbitSDK.getInstance().isAuthorized()) {
                    JRFitbitSDK.getInstance().logWeight("70.0","2019-2-28","",new DefaultApiCallback(){
                        @Override
                        public void onLogWeight() {
                            Log.d(TAG, "onLogWeight: ");
                        }
                    });
                } else {
                    JRFitbitSDK.getInstance().authorize(HomeActivity.this, list);
                }
            }
        });
        initOkGo();
        JRFitbitSDK.getInstance().initialize(CLIENT_ID, "senssunlife://home", Base64.encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(), Base64.DEFAULT), CODE_VERIFIER, MyApp.mApp);

        Log.i("Test", "HEREREEE");

        if (JRFitbitSDK.getInstance().isAuthorized()) {
            Log.i("Test", "Authed");
//            Toast.makeText(this,"authed",5).show();
        }


    }

    private static final String TAG = "HomeActivity";
    public static String getRandomString(int length) {
        String str = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(10);
            sb.append(str.charAt(number));
        }
        Log.e("code_challenge", sb.toString());
        return sb.toString();
    }

    private void initOkGo() {
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpHeaders headers = new HttpHeaders();
        String token = "";
        headers.put("Authorization", "Basic MjJEQkcyOjdjNDNjMmExYjAwMjEwODgxMmMzZjVlMTA4ZTU4NDI0");    //header不支持中文，不允许有特殊字符
        headers.put("Content-Type", "application/x-www-form-urlencoded");    //header不支持中文，不允许有特殊字符

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
        //第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        //builder.addInterceptor(new ChuckInterceptor(this));

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失


        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(getApplication())                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers);              //全局公共头


//                .addCommonParams(params);                       //全局公共参数
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("Test", "HEREREEE2");

        if (intent != null) {
            JRFitbitSDK.getInstance().getCode(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i("Test", "Settings");

            JRFBActivitySumAPI summary = new JRFBActivitySumAPI(this);
            summary.fetchYesterdaysActivitySummary();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
