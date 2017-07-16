package com.gunsnhony.dropofhoney.support;

import java.util.ArrayList;

/**
 * Created by Hugh on 7/12/2017.
 */

public class AllPhotos {
    private static ArrayList<Photo> allPhotos = new ArrayList<Photo>();

    public static ArrayList<Photo> getAllPhotos() {

        return allPhotos;
    }

    public static void setAllPhotos(ArrayList<Photo> allPhotos) {
        AllPhotos.allPhotos = allPhotos;
    }

    public static Photo getPhoto(int pos) {
        return allPhotos.get(pos);
    }

    public static void setPhoto(Photo photo) {
        AllPhotos.allPhotos.add(photo);
    }

    public static void removeAll() {
        AllPhotos.allPhotos.clear();
        System.gc();
    }
}
