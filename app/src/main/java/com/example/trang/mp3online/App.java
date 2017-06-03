package com.example.trang.mp3online;

import android.app.Application;
import android.content.Context;

/**
 * Created by Trang on 5/22/2017.
 */

public class App extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        this.mContext = this;
        super.onCreate();
    }

    public static Context getmContext() {
        return mContext;
    }
}
