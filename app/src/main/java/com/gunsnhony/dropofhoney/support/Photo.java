package com.gunsnhony.dropofhoney.support;

import android.graphics.Bitmap;

/**
 * Created by Hugh on 7/12/2017.
 * Photo class holds the information about a photo that is needed in various parts
 * of Drop of Honey.  The goal is to have the Photo class have the information needed
 * when asked to be able to assemble sub queries such as retrieve the owners photo stream
 * or display a bitmap associated with the photo at the time of queue entry.
 */

public class Photo {
    // URL info
    public String farmId;
    public String serverId;
    public String photoId;
    public String secret;
    public String size = "m";
    public String streamOrigin = "";

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