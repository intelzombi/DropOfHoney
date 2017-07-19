package com.gunsnhony.dropofhoney.support;

import java.util.ArrayList;

/**
 * Created by Hugh on 7/16/2017.
 * Simple interface used by the PhotoFillerHandler.
 * and instanciated by the Browser instance of PhotoFillerHandler Listener.
 * the Browser doesn't implement this directly. it has an instance of the
 * Handler that implements it when constructed as an anonymous function.
 */

public interface RestReadyListener {
    void onRestReady(ArrayList<Photo> photos);
}
