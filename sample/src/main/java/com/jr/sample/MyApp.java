package com.jr.sample;

import android.app.Application;

public class MyApp extends Application {
    public static Application mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }
}
