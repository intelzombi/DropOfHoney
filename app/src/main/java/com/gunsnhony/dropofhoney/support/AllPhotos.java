package com.gunsnhony.dropofhoney.support;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by Hugh on 7/12/2017.
 * Photo Holder class
 * Respository for Default stock photos
 * photoDeque hold the returned photos from the Flickr GetRecent Rest call
 * photoOwnerDeque holds the stream of interest photos (owner photos).
 * There are Spin funtions to sping the queues forward or backward.
 * Utilities for clearing the queues as well as diminishing the size for
 * partial refresh
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

    public static Deque<Photo> getPhotoOwnerDeque()
    {
        return photoOwnerDeque;
    }
    public static Deque<Photo> getPhotoDeque()
    {
        return photoDeque;
    }

    public static void spinPhotoDeque(boolean forward)
    {
        if(photoDeque.isEmpty())
            photoDeque.push(DefaultPhoto);
        if (forward) {
            Photo photo = photoDeque.getFirst();
            photoDeque.removeFirst();
            photoDeque.addLast(photo);
        } else {
            Photo photo = photoDeque.getLast();
            photoDeque.removeLast();
            photoDeque.addFirst(photo);
        }
    }
    
    public static void spinPhotoOwnerDeque(boolean forward)
    {
        if(photoOwnerDeque.isEmpty())
            photoOwnerDeque.push(DefaultPhoto);
        if (forward) {
            Photo photo = photoOwnerDeque.getFirst();
            photoOwnerDeque.removeFirst();
            photoOwnerDeque.addLast(photo);
        } else {
            Photo photo = photoOwnerDeque.getLast();
            photoOwnerDeque.removeLast();
            photoOwnerDeque.addFirst(photo);
        }
    }
    
    // Motivation here is to remove only half of the photo stream.
    // Additionally the removal is every other one.
    // I can think of other strategies possibly for different user
    // experience.
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
                Photo photo = photoDeque.getFirst();
                photoDeque.removeFirst();
                photoDeque.addLast(photo);
            }
        }
    }
    
    // remove all but 10% of the queue.  Leave the user with
    // something to flip through until more photos are added.
    // I mix the order up so it's likely the order isn't stale
    public static void minimizeQueue()
    {
        if(isOwner) {
            int size = photoOwnerDeque.size();
            for(int i=0; i<(size/10)*9; i++)
            {
                photoOwnerDeque.removeFirst();
            }
        }
        else {
            int size = photoDeque.size();
            for(int i=0; i<(size/10)*9; i++)
            {
                photoDeque.removeFirst();
                Photo photo = photoDeque.getFirst();
                photoDeque.removeFirst();
                photoDeque.addLast(photo);
            }
        }
    }
    
    public static void removeAll() {
        photoDeque.clear();
        photoOwnerDeque.clear();
        residualPhotos.clear();
    }
}
