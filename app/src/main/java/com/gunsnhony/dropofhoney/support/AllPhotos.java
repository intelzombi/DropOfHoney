package com.gunsnhony.dropofhoney.support;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

/**
 * Created by Hugh on 7/12/2017.
 */

public class AllPhotos {
    private static Deque<Photo> photoDeque = new ArrayDeque<>();
    public static Photo DefaultPhoto = new Photo();

    public static Deque<Photo> getPhotoDeque()
    {
        return photoDeque;
    }

    public static void removeAll() {
        photoDeque.clear();
        System.gc();
    }
}
