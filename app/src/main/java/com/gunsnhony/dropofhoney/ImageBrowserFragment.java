package com.gunsnhony.dropofhoney;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gunsnhony.dropofhoney.support.AllPhotos;
import com.gunsnhony.dropofhoney.support.DOH_Constants;
import com.gunsnhony.dropofhoney.support.FlickrRestXML;
import com.gunsnhony.dropofhoney.support.Photo;
import com.gunsnhony.dropofhoney.support.PhotoFillerHandler;
import com.gunsnhony.dropofhoney.support.RestReadyListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;


/**
 * Created by Hugh on 7/12/2017.
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
 *
 */

public class ImageBrowserFragment extends Fragment{
    private Context context;
    View imageBrowserView = null;
    
    private TextView titleView;
    private ImageView recentImage;
    private Button FlkrGetRecentBtn;
    private Button FlkrOwnerBtn;
    private RadioButton thumbnailRadBtn;
    private RadioButton smallRadBtn;
    private RadioButton mediumRadBtn;

    public FlickrRestXML frx = new FlickrRestXML();
    public PhotoFillerHandler pfHandler;

    private static final String TAG = "ImageBrowser";
    private String requestImageSize = DOH_Constants.FlikrPhotoSearchSmallExtra;
    private String ownerLink = "";
    public static int ownerQueueSize = 120;
    public static int flickrQueueSize = 120;
    public static int restRequestSize = 100;
    
    private final int Max_Swipe_Distance = 900;
    private final int Min_Swipe_Velocity = 20;

    public class RestResponseTask extends AsyncTask<String, Void, ArrayList<Photo>> {

        private RestReadyListener rrLisener;

        public RestResponseTask(RestReadyListener rrl)
        {
            this.rrLisener = rrl;
        }

        protected ArrayList<Photo> doInBackground(String ... restCalls) {
            Log.d(TAG, "RestResponseTask start doInBackground");
            ArrayList<Photo> photos = new ArrayList<>();
            FlickrRestXML frx = new FlickrRestXML();
            Activity activity = getActivity();

            if( activity == null || restCalls.length < 4 )
                return null;

            int tries = 0, max_tries = 3;
            while(photos.size() == 0 && tries < max_tries) {
                try {
                    tries ++;
                    int count = Integer.parseInt(restCalls[3]);
                    if (restCalls[0] == frx.FlickerGetRecentType) {
                        photos = frx.getRecentFlickrPhotos(restCalls[1], restCalls[2], count);
                    }
                    if (restCalls[0] == frx.FlickerOwnerSearchType) {
                        photos = frx.getOwnerFlickrPhotos(restCalls[1], restCalls[2], count);
                    }
                    if (restCalls[0] == frx.FlickerSearchType) {
                        // TODO implementtion needed in FlickrRestXML class
                        //photos = frx.getSearchFlickrPhotos(restCalls[1],restCalls[2],count);
                    }
                } catch (XmlPullParserException xe) {
                    Log.e("ResponseTask", xe.getMessage(), xe);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return photos;
        }

        @Override
        protected void onPostExecute(ArrayList<Photo> photos)
        {
            rrLisener.onRestReady(photos);
            Log.d(TAG, "RestResponseTask RestReady photos returned to rrListener");
        }
    }
    
    private boolean populatePhotoStream(String restCallType, String urlString, String size, String count ){
        
        if(AllPhotos.isOwner)
            AllPhotos.getPhotoOwnerDeque().clear();
        if(AllPhotos.getPhotoDeque().size() > flickrQueueSize *2)
            AllPhotos.minimizeQueue();// AllPhotos.removeAll() and AllPhotos.removeHalf() are options here;
        
        RestResponseTask rrt = new RestResponseTask(new RestReadyListener() {
            @Override
            public void onRestReady(ArrayList<Photo> photos) {
                Log.d(TAG, "Processing RestReady photos to Photo Filler Handler");

                for(int i = 0; i < photos.size(); i++)
                {
                    pfHandler.pumpMessage(photos.get(i));
                }
            }
        });
        rrt.execute(restCallType, urlString, size, count);
        return true;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(TAG, "onAttachFragment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        context = getContext();
        pfHandler = new PhotoFillerHandler(new Handler());
        pfHandler.setListener(new PhotoFillerHandler.photoListener() {
            @Override
            public void PhotoRetrieved(Photo photo) {
                if(photo.streamOrigin == frx.FlickerGetRecentType)
                    AllPhotos.getPhotoDeque().push(photo);
                if(photo.streamOrigin == frx.FlickerOwnerSearchType)
                    AllPhotos.getPhotoOwnerDeque().push(photo);
                Log.d(TAG, "Photo Retrieved from Photo Filler Handler : Push to Photo Deque");
            }
        });

        pfHandler.start();
        pfHandler.getLooper();
        populatePhotoStream(frx.FlickerGetRecentType, frx.assembleFlickerGetRecentURL(requestImageSize), requestImageSize, Integer.toString(restRequestSize));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if(imageBrowserView == null)
        {
            imageBrowserView = LayoutInflater.from(getActivity()).inflate(R.layout.image_viewer, null);
        }
        recentImage = (ImageView) imageBrowserView.findViewById(R.id.RecentImageView);
        titleView = (TextView) imageBrowserView.findViewById(R.id.imageTitle);
        thumbnailRadBtn = (RadioButton) imageBrowserView.findViewById(R.id.thumnailRB);
        smallRadBtn = (RadioButton) imageBrowserView.findViewById(R.id.smallRB);
        mediumRadBtn = (RadioButton) imageBrowserView.findViewById(R.id.mediumRB);
        FlkrGetRecentBtn = (Button) imageBrowserView.findViewById(R.id.recentStreamBtn);
        FlkrOwnerBtn = (Button) imageBrowserView.findViewById(R.id.ownerSearchButton);

        initializeDefaultImage();
        ownerLink = AllPhotos.DefaultPhoto.retrieveURL;
        thumbnailRadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestImageSize = DOH_Constants.FlikrPhotoSearchThumbExtra;
            }
        });
        smallRadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestImageSize = DOH_Constants.FlikrPhotoSearchSmallExtra;
            }
        });
        mediumRadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestImageSize = DOH_Constants.FlikrPhotoSearchMediumExtra;
            }
        });
        FlkrGetRecentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllPhotos.isOwner=false;
                populatePhotoStream(frx.FlickerGetRecentType, frx.assembleFlickerGetRecentURL(requestImageSize), requestImageSize, Integer.toString(flickrQueueSize));
            }
        });
        FlkrOwnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllPhotos.isOwner=true;
                populatePhotoStream(frx.FlickerOwnerSearchType, ownerLink, requestImageSize, Integer.toString(ownerQueueSize));
            }
        });
        recentImage.setOnTouchListener( ImageTouchListner() );
        return imageBrowserView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        primary.showImageBrowserFragment();
    }

    private void initializeDefaultImage()
    {
        Photo photo;
        if(!AllPhotos.getPhotoDeque().isEmpty())
        {
            photo = AllPhotos.getPhotoDeque().getFirst();
            recentImage.setImageBitmap(photo.bitmap);
            titleView.setText(photo.title);
        }else{
            recentImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dragontree));
            titleView.setText("Dragon Tree");
            photo = new Photo();
            photo.bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.teapot);
            photo.size = "url_n";//requestImageSize;
            photo.owner = "56603367@N03";
            photo.retrieveURL =  frx.assembleFlickerOwnerSearchURL(photo.owner, photo.size);
            photo.title = "Dragon Tree";
            AllPhotos.DefaultPhoto = photo;
            AllPhotos.getPhotoDeque().push(photo);
            AllPhotos.getPhotoOwnerDeque().push(photo);
        }
    }

    public View.OnTouchListener ImageTouchListner() {
        return new View.OnTouchListener() {
            private GestureDetector gesture =
                    new GestureDetector(getActivity(),
                            new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                                    final float xDistance = Math.abs(e1.getX() - e2.getX());
                                    final float yDistance = Math.abs(e1.getY() - e2.getY());
                                    velocityX = Math.abs(velocityX);
                                    boolean result = false;

                                    if (xDistance < Max_Swipe_Distance && yDistance < Max_Swipe_Distance &&
                                            velocityX > Min_Swipe_Velocity ) {
                                        if (e1.getX() > e2.getX()) {
                                            Log.d(TAG, "Left <- Right");
                                            NextImage();
                                        } else {
                                            Log.d(TAG, "Left -> Right");
                                            PrevImage();
                                        }
                                        result = true;
                                        return result;
                                    }
                                    return false;
                                }

                                @Override
                                public boolean onDown(MotionEvent e) {
                                    return true;
                                }

                                @Override
                                public void onShowPress(MotionEvent e) {}

                                @Override
                                public boolean onSingleTapUp(MotionEvent e) { return false; }

                                @Override
                                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                                    // return false so event will be passed onto the onFling Override.
                                    return false;
                                }

                                @Override
                                public void onLongPress(MotionEvent e) {}
                            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        };
    }

    public void NextImage()
    {
        SpinQueue(true);
    }

    public void PrevImage()
    {
        SpinQueue(false);
    }

    public void SpinQueue(boolean forward)
    {
        Log.d(TAG, "SpinQueue");
        if(AllPhotos.getPhotoDeque().size() > 1) {
            
            if(AllPhotos.isOwner)
                AllPhotos.spinPhotoOwnerDeque(forward);
            else
                AllPhotos.spinPhotoDeque(forward);
          
            Photo photo;
            if(AllPhotos.isOwner)
                photo = AllPhotos.getPhotoOwnerDeque().getFirst();
            else
                photo = AllPhotos.getPhotoDeque().getFirst();
            if (photo.bitmap != null) {
                recentImage.setImageBitmap(photo.bitmap);
                String title = photo.title.length() > 70 ? photo.title.substring(0,70) : photo.title;
                titleView.setText(title);
                ownerLink = frx.assembleFlickerOwnerSearchURL(photo.owner, photo.size);
            } else {
                // we shouldn't get here but if an null bitmap gets through we don't want to stall
                if (!AllPhotos.getPhotoDeque().isEmpty()) {
                    Log.d("ImageBrowser", "SpinQueue hit with null bitmap Photo url : " + photo.retrieveURL);
                    SpinQueue(forward);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        pfHandler.quit();
        AllPhotos.removeAll();
        Log.i("ImageBrowser", "photo filler handler retired");
        Log.i("ImageBrowser", "AllPhotos are cleared and garbage collected");
        super.onDestroy();
    }
}
