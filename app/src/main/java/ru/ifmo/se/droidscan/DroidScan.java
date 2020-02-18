package ru.ifmo.se.droidscan;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class DroidScan extends Application {
    private static Context context;
    private Activity mCurrentActivity = null;
    private static DroidScan droidScanInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getAppContext();
        droidScanInstance = this;
    }

    public static DroidScan getInstance() {
      return droidScanInstance;
    }

    public static Context getAppContext() {
        return DroidScan.context;
    }

    public void setCurrentActivity (Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity ;
    }

    public Activity getCurrentActivity () {
        return mCurrentActivity ;
    }
}
