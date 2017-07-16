package com.gunsnhony.dropofhoney;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

/**
 * Created by Hugh on 7/15/2017.
 */

public class PhotoFillerHandler<TK> extends HandlerThread {

    Handler handler;
    Handler photoHandler;
    Listener<TK> photoListener;
    public PhotoFillerHandler(){super("PFH"); };
    public PhotoFillerHandler( Handler phHandler )
    {
        super("PFH");
        photoHandler = phHandler;
    }

    public interface Listener<TK> {
        void PhotoRetrieved(TK token, Bitmap thumbnail);
    }


    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // is Downloaded
                if( msg.what == 0)
                {
                    TK tk = (TK) msg.obj;
                    handleBitmap(tk);
                }
            }
        };
    }

    public void pumpMessage(TK tk, String url) {

        handler
                .obtainMessage(0, tk)
                .sendToTarget();
    }

    private void handleBitmap(final TK bytes)
    {

    }
}
