package com.gunsnhony.dropofhoney;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;


/**
 * Created by Hugh on 7/12/2017.
 */

public class primary extends FragmentActivity {

    private ImageBrowserFragment imageBrowserFragment;
    protected static Context context;
    private static final String TAG = "DOH PrimaryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.primary);
        AddImageBrowserFragment();
    }

    protected void AddImageBrowserFragment()
    {
        Log.d(TAG, "adding Image Browser Fragment");
        imageBrowserFragment = new ImageBrowserFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.ImageBrowserFragmentPH, imageBrowserFragment, "IBF_TAG");
        ft.hide(imageBrowserFragment);
        ft.commit();
    }

    public static void showImageBrowserFragment()
    {
        final primary activity = (primary) context;
        final Fragment f = activity.getSupportFragmentManager().findFragmentByTag("IBF_TAG");
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.show(f);
        ft.commit();
    }

 }
