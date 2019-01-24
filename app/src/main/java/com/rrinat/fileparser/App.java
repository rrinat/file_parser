package com.rrinat.fileparser;

import android.annotation.SuppressLint;
import android.app.Application;

public class App extends Application {

    @SuppressLint("StaticFieldLeak")
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }
}
