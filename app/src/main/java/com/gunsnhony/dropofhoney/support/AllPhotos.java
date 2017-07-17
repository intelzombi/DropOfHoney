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
    private static ArrayList<Photo> allPhotos = new ArrayList<Photo>();
    private static HashMap<String, Photo> photoMap = new HashMap<>();
    private static Deque<Photo> photoDeque = new ArrayDeque<>();
    public static boolean usingMap = false;
    public static Photo DefaultPhoto = new Photo();

    public class pair<K, V> {
        public K key;
        public V value;
    }

    public static ArrayList<Photo> getAllPhotos() {

        return allPhotos;
    }

    public static HashMap<String, Photo> getPhotoMap() {
        return photoMap;
    }

    public static void setAllPhotos(ArrayList<Photo> allPhotos)
    {
        AllPhotos.allPhotos = allPhotos;
    }

    public static void mapAllPhotos(HashMap<String,Photo> map)
    {
        photoMap = map;
    }

    public static Photo getPhoto(int pos) {
        int size = allPhotos.size();
        try {
            if (pos < size)
                return allPhotos.get(pos);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            Log.e("ALLPhotos", aioobe.getMessage() + " pos referenced is : " + Integer.toString(pos) + " and Size is : " + Integer.toString(size), aioobe);
            aioobe.printStackTrace();
        }
        return DefaultPhoto;
    }

    public static Photo getPhoto(String url)
    {
        Photo photo = photoMap.get(url);
        if(photo != null)
        {
            return photo;
        }else{
            return DefaultPhoto;
        }
    }

    public static void setPhoto(Photo photo)
    {
        if(!usingMap) {
            AllPhotos.allPhotos.add(photo);
        }else{
            photoMap.put(photo.retrieveURL, photo);
        }
    }

    public static Deque<Photo> getPhotoDeque()
    {
        return photoDeque;
    }

    public static void removeAll() {
        photoDeque.clear();
        photoMap.clear();
        AllPhotos.allPhotos.clear();
        System.gc();
    }
}
