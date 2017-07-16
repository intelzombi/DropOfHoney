package com.gunsnhony.dropofhoney.support;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Hugh on 7/12/2017.
 */

public class Photo {
    // URL info
    public String farmId;
    public String serverId;
    public String photoId;
    public String secret;
    public String size = "m";
    public String fileType = "jpg";

    // Notation Info
    public String title;
    public String owner;
    public String retrieveURL;

    public Bitmap bitmap;


    public void setUrlLink(String url)
    {
        retrieveURL = url;
    }
}