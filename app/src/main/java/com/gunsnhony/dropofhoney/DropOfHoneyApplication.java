package com.gunsnhony.dropofhoney;

import android.app.Application;
import android.content.res.Configuration;


/**
 * Created by Hugh on 7/15/2017.
 * High level Application class used for critical initialization or
 * Global instances needed ubiquitously by the application
 * This is just implemented here as boiler plate
 */

public class DropOfHoneyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
