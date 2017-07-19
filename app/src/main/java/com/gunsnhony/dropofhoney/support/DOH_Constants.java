package com.gunsnhony.dropofhoney.support;

import android.net.Uri;

/**
 * Created by Hugh on 7/12/2017.
 */

public class DOH_Constants {
    public static final String FlikrApiKey                    = "2c9c76745c098fe9a5ae934a6ae4545d";
    public static final String FlikrApiSecret                 = "ef4d3a314bf7b365";
    public static final String TwitterAccessToken             = "<YOUR_TWITTER_ACCESS_TOKEN_GOES_HERE>";
    public static final String TwitterAccessTokenScret        = "<YOUR_TWITTER_ACCESS_TOKEN_SECRET_GOES_HERE>";
    public static final String FlikrApiRoot                   = "https://api.flickr.com/services/rest/";
    public static final String FlikrGetRecent                 = "flickr.photos.getRecent";
    public static final String FlikrPhotoSearch               = "flickr.photos.search";
    public static final String FlikrPhotoSearchThumbExtra     = "url_t";
    public static final String FlikrPhotoSearchSmallExtra     = "url_n";
    public static final String FlikrPhotoSearchMediumExtra    = "url_c";

    public static final String GetRecentURL = Uri.parse(DOH_Constants.FlikrApiRoot).buildUpon()
            .appendQueryParameter("method", DOH_Constants.FlikrGetRecent)
            .appendQueryParameter("api_key", DOH_Constants.FlikrApiKey)
            .appendQueryParameter("extras", DOH_Constants.FlikrPhotoSearchThumbExtra)
            .build().toString();
}
