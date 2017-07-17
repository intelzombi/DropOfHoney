package com.gunsnhony.dropofhoney.support;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Queue;

/**
 * Created by Hugh on 7/12/2017.
 */

public class AllPhotos {
    private static Deque<Photo> photoDeque = new ArrayDeque<>();
    private static Deque<Photo> photoOwnerDeque = new ArrayDeque<>();
    private static Deque<Photo> residualPhotos = new ArrayDeque<>();
    public static Photo DefaultPhoto = new Photo();
    public static boolean isOwner = false;

    public static Deque<Photo> getResidualPhotos()
    {
        return residualPhotos;
    }

    public static Deque<Photo> getPhotoDeque()
    {
        if(isOwner)
            return photoOwnerDeque;
        else
            return photoDeque;
    }

    public static void removeHalf()
    {
        if(isOwner) {
            int size = photoOwnerDeque.size();
            for(int i=0; i<size/2; i++)
            {
                photoOwnerDeque.removeFirst();
            }
        }
        else {
            int size = photoDeque.size();
            for(int i=0; i<size/2; i++)
            {
                photoDeque.removeFirst();
            }
        }
    }

    public static void removeAll() {
        photoDeque.clear();
        photoOwnerDeque.clear();
        residualPhotos.clear();
    }
}
