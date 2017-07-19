# DropOfHoney
Fickr Application to flip through a photo stream. 

# User Interaction

![layout-2017-07-18-232026_with_notations](https://user-images.githubusercontent.com/13732960/28351191-e8ed3a68-6c13-11e7-9d11-52d5b7572269.png)
# Design choices
What is here;
An application that on start up loads a subset of the GetRecent Rest call list of photos and presents them to the user one at a time. The user flips forwards or backwards through the list.  The list is dynamically updated as photos are retrieved from the Photo Filler Handler (explained below).  The list is actually a Deque (double ended queue) that gets populated up to the point of max size. 
The two buttons on the bottom allow the user to refresh the Flickr Stream or Choose to inspect the Stream of the owner of the currently presented picture. If by chance the user clicks on ower stream with the stock picture it takes the user to my Flickr Stream https://www.flickr.com/photos/56603367@N03/. The usage is kind of a game in that the user soon learns that with every refresh the flip stream changes and there is no going back. The owner stream is cleared each time you enter. so once you look at it changes are diminished that it will be in the flip stream to inspect again.  Going back to the Flickr Stream has a high chance of clearing most of the photos out of the stream so you have to decide if looking at that owner stream is valuable enough to refresh the main queue.
Three radio buttons allow the user to choose what size photos they want to view.  While the presentation of the photos is fairly set the quality will change depending on which choice you make.  Why wouldn't you just choose the highest quality.  Response time, Memory considerations, and Bandwidth/data use.  This way the user can flip through the Flickr Stream at a low resolution and if they find a User Steam the want to inspect they can up the resolution.  The radio button choices only take affect after the next time you hit one of the refresh cycles.  So you might get into a user stream at low resolution and then hit the higher resolution and you can look at the photos at a higher quality.

I did not implement a progress bar.  There can be some lag on first start if the internet is slow or connection is weak. I've given a couple of Stock phots so there is something view. One of the TODOs would be to maybe put a color icon above each stream showing red for just a few photos in the queue which indicates that the handler is still retrieving photos or green once the queue has x number of photos in it.  

I chose to break down the Threading into two parts. The AsyncTask is just used to retrieve the list of photos from the Rest call and partially initialize the Photo instances minus the Bitmaps.  I chose to use a HandlerThread to do the lifting of grabing the actual bitmaps and finishing out the definition of the Photo instance.  It was chosen primarily because it has a built in looper (Message Pump) to iterate through the list of Bitmap retrieval requests.

The UI is rough and admittedly what I spent the least amount of time on and would not be ship ready.  I used the Constrant Layout for the ImageBrowser View. It is also developed with the Phone in mind with a locked vertical orientation. It works fine on the Tablet but lots of UI tweaking would need to be done. 

I took to heart the requirement of fliping left to right (or right to left) through the photo stream so I decided against any grid view or use of adapters. I attached a View.onTouchListener for the ImageView that houses the Photo of focus.  I chose the Fling gesture to implement.  

The Photo is a class that gets partially assembled and passed around but knows everything about itself it needs to at the point it is being queried. I felt for this simple application it would be nice to have the knowledge fully encapsulated at one common instance point so there wasn't a lot of cross talk to assemble information form different places. For instance this photo information could easily be thought of in terms of data base tables to join and query.  That would be way to heavy for this type of application. 

I played around with queue sizes and refresh stategies and the choices are endless. In the end I chose fixed size queue limits with agressive purging when the queue limit is reached. I also to keep from redownloading the same photos over and over cache photos that I've already prepped once and don't instantiate or process those items again.  Keep the stream fresh!

One last thing. This code was written from scratch using Android Studio 2.3 version starting with a blank activity. I used google, android developer sites, stack overflow and such to research topics and issues I ran into. But no code was copy pasted or plagerized for this exercise.  This is all me; Love it or Leave.  Thanks for taking time to preview the Drop of Honey application.  I thuroughly enjoyed the exercise. 

# General Architecture
* What follows is just a list of the class comments from the key files.  
* it is rearticulated here as a convienience to browsing the files.
* Hopefully the code is written well enough to be self explanitory with
* these notes as a guide.

# Image Browser Fragment
The main user presentation is encapsulated in the ImageBrowser Fragment.
 * The ImageBrowserFragment contains the main view for Drop of Honey
 * It uses the image_viewer.xml layout.
 * The main functionality of the view is to Display an Image that was
 * retrieved by either the GetRecent or Search Owner Rest requests.
 * From a view it displays said image along with the title, and two
 * user option buttons to retrieve either more GetRecent photos or
 * choose to inspect the stream of the owner for the photo that is
 * currently being presented.  The image has a ImageTouchListner that
 * listens to Gestures.  The image listens specifically for the Fling
 * gesture which is used to progress the photo by spinning the photo
 * queue and presenting the next photo.
 *
 * There is a list of radio buttons that allow the user to choose different
 * resolution photos to be retrieved.  thumbnail, small, and medium sizes.
 * The ImageView used to present the Photo bitmap is constrained by size.
 * That is the photos are scaled up to fit. thumbnails can tend to be grainy
 * small is a bit harder to tell and medium looks pretty good. Since larger
 * bitmaps require more bandwidth and memory resources the user can decide
 * to use the thumbnail to zip through the Flickr stream but up the size
 * when looking at a user stream of interest.
 *
 * The Threading to keep the UI thread from ANR baiting is broken up into two parts.
 * There is the RestResponseAsyncTask which is an inner class to this Fragment which
 * handles the initial retrieval of the XML Rest strings containing the list
 * of photo details desired for download. The PhotoFillerHandler is the work horse
 * and is more appropriate to do all the retrieval of the individual photos. The
 * HandlerThread was chosen specifically because it has a looper or "message pump"
 * This class has an instance of this PhotoFillerHandler.
 *
 * The OnCreate initializes the PhotoFillerHandler, sets up the PhotoRetrieved callback
 * that the handler uses to give us back the completed Photo instance. It also starts the
 * initial cycle of GetRecent Rest calls and the retrieval and population of the
 * photo queues.
 *
 * The OnViewCreated simply calls the primary activities show fragment function to make the
 * initialized fragment visible.
 *
 * The OnCreate initializes all the view elements as well as OnClickListener functionality
 * It populates the Default Stock Photos used to present until photos are retrieved.
 
 # Photo class
 * Photo class holds the information about a photo that is needed in various parts
 * of Drop of Honey.  The goal is to have the Photo class have the information needed
 * when asked to be able to assemble sub queries such as retrieve the owners photo stream
 * or display a bitmap associated with the photo at the time of queue entry.
 
 # All Photos class
 * Photo Holder class
 * Respository for Default stock photos
 * photoDeque hold the returned photos from the Flickr GetRecent Rest call
 * photoOwnerDeque holds the stream of interest photos (owner photos).
 * There are Spin funtions to sping the queues forward or backward.
 * Utilities for clearing the queues as well as diminishing the size for
 * partial refresh
 
 # Flickr Rest XML class
 * This is the the XML parsing phase of the Rest retrieval.  This class uses
 * the XMLPullParser mechanism to parse an xml string that is retrieved from
 * Flickr server.  the XMLPullParser uses the following spec: "http://www.xmlpull.org/"
 * It also has some utility functions to assemble the needed Rest URL's
 * FlickrRestXML depends on the EasyRest Class for it's Http connection and byte retrieval.
 
 # Rest Easy class
 * RestEasy class implements the HttpURLConnection api as opposed to the
 * apache http classes.  Ultimately RestEasy is the class that takes the URL
 * and returns the data in a form usable by the FlickrRestXML class.
 * URL in,  XML String out,  Bitmap out.

 # Photo Filler Handler class
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
 
 # Rest Response Async Task class
 * This is an inner class to the Image Browser It's usage is explained in Image Browser 
 * information as well as the Photo Filler Handler class info.  
 * In short it sets up the retrieval of the initial Stream Rest call that will return 
 * a set of partially initialized Photo instances. 
 


