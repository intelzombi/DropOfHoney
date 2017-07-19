package com.gunsnhony.dropofhoney.support;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

/**
 * Created by Hugh on 7/15/2017.
 * The Threading to keep the UI thread from ANR baiting is broken up into two parts.
 * There is the AsyncTask which is an inner class to the Fragment which handles the
 * initial retrieval of the XML Rest strings containing the list of photo details
 * desired for download.  The PhotoFillerHandler is the work horse that retrieves the
 * individual photos. the HandlerThread was chosen specifically because it has a looper
 * or "message pump"
 * It takes the parthially assembled photos presented by the AsyncTask and the
 * FlickrRestXML class and serially retrieves the bitmap for each photo and stores
 * it in the Photo instance.  Once the bitmap is retrieved The Photo is returned back
 * to the handler listener int the Browser fragment.
 */

public class PhotoFillerHandler extends HandlerThread {
    private static final int MESSAGE_RETRIEVE = 1;

    Handler handler;
    Handler photoHandler;
    photoListener ptoListener;

    public PhotoFillerHandler(){super("PFH"); };
    public PhotoFillerHandler( Handler phHandler )
    {
        super("PFH");
        photoHandler = phHandler;
    }

    public interface photoListener {
        void PhotoRetrieved(Photo photo);
    }

    public void setListener(photoListener listener) {
        ptoListener = listener;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // is Downloaded
                if( msg.what == MESSAGE_RETRIEVE)
                {
                    Photo photo = (Photo) msg.obj;
                    handleBitmap(photo);
                }
            }
        };
    }

    public void pumpMessage(Photo photo) {
        Log.d("EasyHandler","Pump Photo URL: " + photo.retrieveURL);
        if( photo.retrieveURL != null) {
            handler
                    .obtainMessage(MESSAGE_RETRIEVE, photo)
                    .sendToTarget();
        }
    }

    private void handleBitmap(final Photo photo)
    {
        if(photo.retrieveURL != null) {
            photo.bitmap = RestEasy.urlToBitmap(photo.retrieveURL);
            if (photo.bitmap == null)
                Log.d("EasyHandler", "Bitmap came back null. URL: " + photo.retrieveURL);
            ptoListener.PhotoRetrieved(photo);
        }else{
            Log.d("EasyHandler", "URL came back null");
        }
    }
}
