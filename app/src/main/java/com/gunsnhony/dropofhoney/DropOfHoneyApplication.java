package com.gunsnhony.dropofhoney;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.gunsnhony.dropofhoney.support.AllPhotos;
import com.gunsnhony.dropofhoney.support.FlickrRestXML;
import com.gunsnhony.dropofhoney.support.RProxy;

import java.util.ArrayList;

/**
 * Created by Hugh on 7/15/2017.
 */

public class DropOfHoneyApplication extends Application {

    public static AllPhotos currentPhotoStream = new AllPhotos();
    public static RProxy RProxy = null;

    @Override
    public void onCreate() {
        super.onCreate();
        try{
            RProxy = new RProxy(this);
        }catch (Exception e) {
            Log.e("DropOfHoneyApplication", "Failed initialize RProxy.", e);
        }
        AllPhotos.usingMap = FlickrRestXML.usingMap = false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
