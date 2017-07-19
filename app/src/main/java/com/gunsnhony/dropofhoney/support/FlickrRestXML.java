package com.gunsnhony.dropofhoney.support;

/**
 * Created by Hugh on 7/14/2017.
 * This is the the XML parsing phase of the Rest retrieval.  This class uses
 * the XMLPullParser mechanism to parse an xml string that is retrieved from
 * Flickr server.  the XMLPullParser uses the following spec: "http://www.xmlpull.org/"
 * It also has some utility functions to assemble the needed Rest URL's
 * FlickrRestXML depends on the EasyRest Class for it's Http connection and byte retrieval.
 */
import android.net.Uri;
import android.support.v4.util.ArraySet;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;

public class FlickrRestXML {
    public static final String FlickerGetRecentType     = "FLKR_GET_RECENT";
    public static final String FlickerOwnerSearchType   = "FLKR_OWN_SEARCH";
    public static final String FlickerSearchType        = "FLKR_KEY_WORD_SEARCH";
    public static Set<String> idSet = new ArraySet<>();
    public static Set<String> requestSet = new ArraySet<>();

    public static ArrayList<Photo> getRecentFlickrPhotos(String urlString, String photoSize, int count) throws XmlPullParserException, IOException
    {
        ArrayList<Photo> photos = new ArrayList<>();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpParser = factory.newPullParser();
        xpParser.setInput(new StringReader(RestEasy.urlToString(urlString)));
        int eventType = xpParser.getEventType();
        do {
            if(eventType == xpParser.START_DOCUMENT) {

            } else if(eventType == xpParser.START_TAG) {
                if( xpParser.getName().equals("photo"))
                {
                    Photo photo = new Photo();
                    String photoId = xpParser.getAttributeValue(null, "id");
                    if(!idSet.contains(photoId)) {
                        photo.photoId = photoId;
                        photo.owner = xpParser.getAttributeValue(null, "owner");
                        photo.secret = xpParser.getAttributeValue(null, "secret");
                        photo.serverId = xpParser.getAttributeValue(null, "server");
                        photo.farmId = xpParser.getAttributeValue(null, "farm");
                        photo.title = xpParser.getAttributeValue(null, "title");
                        photo.retrieveURL = xpParser.getAttributeValue(null, photoSize);
                        if(photo.retrieveURL == null)
                            photo.retrieveURL = assembleFlickrOwnerPhotoURL(photo, photo.size);
                        photo.streamOrigin = FlickerGetRecentType;
                        photos.add(photo);
                        requestSet.add(photoId);
                    }
                }
            }
            int tries = 0, max_tries = 4;
            boolean success = false;
            while(!success && tries < max_tries)
            try {
                tries++;
                eventType = xpParser.next();
                success = true;
            } catch (XmlPullParserException xppe)
            {
                Log.e("FlickrRest", "Flickr Stream photo XMLPullParserException :" + xppe.getLocalizedMessage());
                if(tries == max_tries)
                    throw xppe;
            }
        }while (eventType != xpParser.END_DOCUMENT && photos.size() < count);
        if(photos.size()!=0) {
            for (String v : requestSet) {
                idSet.add(v);
            }
            idSet.clear();
        }
        return photos;
    }

    public static ArrayList<Photo> getOwnerFlickrPhotos(String urlString, String photoSize, int count) throws XmlPullParserException, IOException
    {
        ArrayList<Photo> photos = new ArrayList<>();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpParser = factory.newPullParser();
        xpParser.setInput(new StringReader( RestEasy.urlToString(urlString)));
        int eventType = xpParser.getEventType();
        do {
            if(eventType == xpParser.START_DOCUMENT) {

            } else if(eventType == xpParser.START_TAG) {
                if( xpParser.getName().equals("photo"))
                {
                    Photo photo = new Photo();
                    photo.photoId     = xpParser.getAttributeValue(null, "id");
                    photo.owner       = xpParser.getAttributeValue(null, "owner");
                    photo.secret      = xpParser.getAttributeValue(null, "secret");
                    photo.serverId    = xpParser.getAttributeValue(null, "server");
                    photo.farmId      = xpParser.getAttributeValue(null, "farm");
                    photo.title       = xpParser.getAttributeValue(null, "title");
                    photo.retrieveURL = assembleFlickrOwnerPhotoURL(photo, photoSize.substring(photoSize.length()-1));
                    photo.streamOrigin = FlickerOwnerSearchType;
                    photos.add(photo);
                }
            }
            int tries = 0, max_tries = 4;
            boolean success = false;
            while(!success && tries < max_tries)
                try {
                    tries++;
                    eventType = xpParser.next();
                    success = true;
                } catch (XmlPullParserException xppe)
                {
                    Log.e("FlickerRest", "Trouble Maker URL : " + urlString);
                    Log.e("FlickrRest", "Owner photo XMLPullParserException :" + xppe.getLocalizedMessage());
                    if(tries == max_tries)
                        throw xppe;
                }
        }while (eventType != xpParser.END_DOCUMENT && photos.size() < count);
        return photos;
    }

    // URL to build
    //"https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + photoId + "_" +  secret + "_" + size + "." + fileType;
    public static String assembleFlickerSearchURL(String searchString){
        String url = Uri.parse(DOH_Constants.FlikrApiRoot).buildUpon()
                .appendQueryParameter("method", DOH_Constants.FlikrPhotoSearch)
                .appendQueryParameter("api_key", DOH_Constants.FlikrApiKey)
                .appendQueryParameter("extras", DOH_Constants.FlikrPhotoSearchThumbExtra)
                .appendQueryParameter("text", searchString)
                .build().toString();
        return url;
    }

    // URL to build
    //https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=<API KEY GOES HERE>&user_id=56603367%40N03&extras=url_s&format=rest
    public static String assembleFlickerOwnerSearchURL(String userId, String url_size){
        String url = Uri.parse(DOH_Constants.FlikrApiRoot).buildUpon()
                .appendQueryParameter("method", DOH_Constants.FlikrPhotoSearch)
                .appendQueryParameter("api_key", DOH_Constants.FlikrApiKey)
                .appendQueryParameter("user_id", userId)
                .appendQueryParameter("extras", url_size)
                .appendQueryParameter("format", "rest")
                .build().toString();
        return url;
    }
    
    public static String assembleFlickerGetRecentURL(String url_size)
    {
        String url = Uri.parse(DOH_Constants.FlikrApiRoot).buildUpon()
                .appendQueryParameter("method", DOH_Constants.FlikrGetRecent)
                .appendQueryParameter("api_key", DOH_Constants.FlikrApiKey)
                .appendQueryParameter("extras", url_size)
                .build().toString();
        return url;
    }

    public static String assembleFlickrOwnerPhotoURL(Photo photo, String size) {
        String url = "https://farm" + photo.farmId + ".staticflickr.com/" + photo.serverId + "/" + photo.photoId + "_" + photo.secret + "_" + size + ".jpg";
        return url;
    }
}
