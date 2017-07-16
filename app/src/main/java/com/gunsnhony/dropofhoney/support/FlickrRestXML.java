package com.gunsnhony.dropofhoney.support;

/**
 * Created by Hugh on 7/14/2017.
 */
import android.net.Uri;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class FlickrRestXML {

    public static final String FlickerGetRecentType     = "FLKR_GET_RECENT";
    public static final String FlickerOwnerSearchType   = "FLKR_OWN_SEARCH";
    public static final String FlickerSearchType        = "FLKR_KEY_WORD_SEARCH";


    /* This is a sample Rest Response for Photo Infomration
        <photos page="1" pages="10" perpage="100" total="1000">
            <photo id="35089710614" owner="21646269@N00" secret="f91048547b"
                server="4279" farm="5" title="July 2017"
                ispublic="1" isfriend="0" isfamily="0" />
            <photo id="35089710724" owner="149945840@N05" secret="37616037d1"
                server="4301" farm="5" title=""
                ispublic="1" isfriend="0" isfamily="0" />

            <!-- with url_s extra -->
            <photo id="35090458394" owner="132811212@N05" secret="2a96f7d1eb"
            server="4315" farm="5" title="Time for my adventure - Airport, church & Grandma ! 20170715 Saturday #sky #askyphotographer #potd"
            ispublic="1" isfriend="0" isfamily="0"
            url_s="https://farm5.staticflickr.com/4315/35090458394_2a96f7d1eb_m.jpg"
            height_s="180" width_s="240" />
            <photo id="35090460494" owner="152623014@N05" secret="c26fa637f6"
            server="4231" farm="5" title=""
            ispublic="1" isfriend="0" isfamily="0"
            url_s="https://farm5.staticflickr.com/4231/35090460494_c26fa637f6_m.jpg"
            height_s="160" width_s="240" />
        </photos>   */
    public static ArrayList<Photo> getRecentFlickrPhotos(String urlString, String photoSize, int count) throws XmlPullParserException, IOException
    {
        ArrayList<Photo> photos = new ArrayList<>();

        // www.xmlpull.org
        //directly from http://www.xmlpull.org/v1/download/unpacked/src/java/samples/MyXmlPullApp.java
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        // factory.setNamespaceAware(true);  // A little confused on this point.  Not sure if I need this or not
        XmlPullParser xpParser = factory.newPullParser();
        //xpParser.setInput(new StringReader(RestEasy.urlToString( DOH_Constants.GetRecentURL )));
        xpParser.setInput(new StringReader(RestEasy.urlToString(urlString)));
        int eventType = xpParser.getEventType();
        do {
            if(eventType == xpParser.START_DOCUMENT) {

            } else if(eventType == xpParser.START_TAG) {
                if( xpParser.getName().equals("photo"))
                {
                    Photo photo = new Photo();
                    /*
                    getAttributeValue
                        added in API level 1
                        String getAttributeValue (String namespace, String name)
                        Returns the attributes value identified by namespace URI
                        and namespace localName. If namespaces are disabled
                        namespace must be null. If current event type is not
                        START_TAG then IndexOutOfBoundsException will be thrown.
                     */
                    photo.photoId     = xpParser.getAttributeValue(null, "id");
                    photo.owner       = xpParser.getAttributeValue(null, "owner");
                    photo.secret      = xpParser.getAttributeValue(null, "secret");
                    photo.serverId    = xpParser.getAttributeValue(null, "server");
                    photo.farmId      = xpParser.getAttributeValue(null, "farm");
                    photo.title       = xpParser.getAttributeValue(null, "title");
                    photo.retrieveURL = xpParser.getAttributeValue(null, photoSize);
                    photo.bitmap      = RestEasy.GetPhotoRestStream(photo.retrieveURL);
                    photos.add(photo);
                }
            }
            eventType = xpParser.next();
        }while (eventType != xpParser.END_DOCUMENT && photos.size() < count);
        return photos;
    }

    public static ArrayList<Photo> getOwnerFlickrPhotos(String urlString, String photoSize, int count) throws XmlPullParserException, IOException
    {
        ArrayList<Photo> photos = new ArrayList<>();

        // www.xmlpull.org
        //directly from http://www.xmlpull.org/v1/download/unpacked/src/java/samples/MyXmlPullApp.java
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        // factory.setNamespaceAware(true);  // A little confused on this point.  Not sure if I need this or not
        XmlPullParser xpParser = factory.newPullParser();
        xpParser.setInput(new StringReader( RestEasy.urlToString(urlString)));
        int eventType = xpParser.getEventType();
        do {
            if(eventType == xpParser.START_DOCUMENT) {

            } else if(eventType == xpParser.START_TAG) {
                if( xpParser.getName().equals("photo"))
                {
                    Photo photo = new Photo();
                    /*
                    getAttributeValue
                        added in API level 1
                        String getAttributeValue (String namespace, String name)
                        Returns the attributes value identified by namespace URI
                        and namespace localName. If namespaces are disabled
                        namespace must be null. If current event type is not
                        START_TAG then IndexOutOfBoundsException will be thrown.
                     */
                    photo.photoId     = xpParser.getAttributeValue(null, "id");
                    photo.owner       = xpParser.getAttributeValue(null, "owner");
                    photo.secret      = xpParser.getAttributeValue(null, "secret");
                    photo.serverId    = xpParser.getAttributeValue(null, "server");
                    photo.farmId      = xpParser.getAttributeValue(null, "farm");
                    photo.title       = xpParser.getAttributeValue(null, "title");
                    photo.retrieveURL = assembleFlickrOwnerPhotoURL(photo, photoSize.substring(photoSize.length()-1));
                    photo.bitmap      = RestEasy.GetPhotoRestStream(photo.retrieveURL);
                    photos.add(photo);
                }
            }
            eventType = xpParser.next();
        }while (eventType != xpParser.END_DOCUMENT && photos.size() < count);
        return photos;
    }

    public static String assembleFlickerSearchURL(String searchString){
        String url = Uri.parse(DOH_Constants.FlikrApiRoot).buildUpon()
                .appendQueryParameter("method", DOH_Constants.FlikrPhotoSearch)
                .appendQueryParameter("api_key", DOH_Constants.FlikrApiKey)
                .appendQueryParameter("extras", DOH_Constants.FlikrPhotoSearchSmallExtra)
                .appendQueryParameter("text", searchString)
                .build().toString();
        //"https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + photoId + "_" +  secret + "_" + size + "." + fileType;
        return url;
    }


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

    public static String assembleFlickrOwnerPhotoURL(Photo photo, String size) {
        String url = "https://farm" + photo.farmId + ".staticflickr.com/" + photo.serverId + "/" + photo.photoId + "_" + photo.secret + "_" + size + ".jpg";
        return url;
    }

}
