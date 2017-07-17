package com.gunsnhony.dropofhoney.support;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Hugh on 7/12/2017.
 */

public class AllPhotos {
    private static ArrayList<Photo> allPhotos = new ArrayList<Photo>();
    public static Photo DefaultPhoto = new Photo();

    public static ArrayList<Photo> getAllPhotos() {

        return allPhotos;
    }

    public static void setAllPhotos(ArrayList<Photo> allPhotos) {
        AllPhotos.allPhotos = allPhotos;
    }

    public static Photo getPhoto(int pos) {
        int size = allPhotos.size();
        try {
            if( pos < size )
                return allPhotos.get(pos);
        }catch (ArrayIndexOutOfBoundsException aioobe) {
            Log.e("ALLPhotos", aioobe.getMessage() + " pos referenced is : " +  Integer.toString(pos) + " and Size is : " + Integer.toString(size), aioobe);
            aioobe.printStackTrace();
        }
        return DefaultPhoto;
    }

    public static void setPhoto(Photo photo) {
        AllPhotos.allPhotos.add(photo);
    }

    public static void removeAll() {
        AllPhotos.allPhotos.clear();
        System.gc();
    }
}
