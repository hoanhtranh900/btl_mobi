package com.sangnk.btl_mobi;

import android.app.Application;
import android.content.Context;

public class Myapp extends Application {

    private static Myapp instance;

    public static Myapp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }


    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}