package com.gunsnhony.dropofhoney;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.gunsnhony.dropofhoney.support.AllPhotos;
import com.gunsnhony.dropofhoney.support.DOH_Constants;
import com.gunsnhony.dropofhoney.support.FlickrRestXML;
import com.gunsnhony.dropofhoney.support.Photo;
import com.gunsnhony.dropofhoney.support.RestReadyListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;


/**
 * Created by Hugh on 7/12/2017.
 */

public class ImageBrowserFragment extends Fragment{
    private Context context;
    View imageBrowserView = null;
    private TextView titleView;
    private ImageView recentImage;
    private Button FlkrGetRecentBtn;
    private Button FlkrOwnerBtn;
    Bitmap defaultBit;
    FlickrRestXML frx = new FlickrRestXML();
    PhotoFillerHandler pfHandler;

    String Tag = "ImageBrowser";
    String ownerLink = "";

    private int currentImage = 0;
    int ImageCount;
    boolean isNext = true;

    private final int Max_Swipe_Distance = 900;
    private final int Min_Swipe_Velocity = 80;

    public class RestResponseTask extends AsyncTask<String, Void, ArrayList<Photo>> {

        private RestReadyListener rrLisener;

        public RestResponseTask(RestReadyListener rrl)
        {
            this.rrLisener = rrl;
        }

        @Override
        protected ArrayList<Photo> doInBackground(String ... restCalls) {
            Log.d(Tag, "RestResponseTask start doInBackground");
            ArrayList<Photo> photos = new ArrayList<>();
            FlickrRestXML frx = new FlickrRestXML();
            Activity activity = getActivity();
            if( activity == null || restCalls.length < 4 )
                return null;
            try{
                int count = Integer.parseInt(restCalls[3]);
                if(restCalls[0] == frx.FlickerGetRecentType) {
                    photos = frx.getRecentFlickrPhotos(restCalls[1],restCalls[2],count);
                }
                if(restCalls[0] == frx.FlickerOwnerSearchType) {
                    photos = frx.getOwnerFlickrPhotos(restCalls[1],restCalls[2],count);
                }
                if(restCalls[0] == frx.FlickerSearchType) {
                    // TODO implementtion needed in FlickrRestXML class
                    //photos = frx.getSearchFlickrPhotos(restCalls[1],restCalls[2],count);
                }
            }catch(XmlPullParserException xe){
                Log.e("ResponseTask", xe.getMessage(), xe);
            }catch(IOException e)
            {
                e.printStackTrace();
            }
            return photos;
        }

        @Override
        protected void onPostExecute(ArrayList<Photo> photos)
        {
            rrLisener.onRestReady(photos);
            Log.d(Tag, "RestResponseTask RestReady photos returned to rrListener");
            //AllPhotos.setAllPhotos(photos);
        }
    }

    private boolean populatePhotoStream(String restCallType, String urlString, String size, String count ){
        AllPhotos.removeAll();
        RestResponseTask rrt = new RestResponseTask(new RestReadyListener() {
            @Override
            public void onRestReady(ArrayList<Photo> photos) {
                Log.d(Tag, "Processing RestReady photos to Photo Filler Handler");
                for(int i = 0; i < photos.size(); i++)
                {
                    pfHandler.pumpMessage(photos.get(i));
                    // wrong place AllPhotos.getPhotoDeque().push(photos.get(i));
                }
            }
        });
        rrt.execute(restCallType, urlString, size, count);
        // TODO Do I need this really?
        try {
            rrt.get();
        }catch(CancellationException ce) {
            ce.printStackTrace();
        }catch(ExecutionException ee) {
            ee.printStackTrace();
        }catch(InterruptedException ie) {
            ie.printStackTrace();
        }
        return true;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(Tag, "onAttachFragment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(Tag, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Tag, "onCreate");
        context = getContext();
        pfHandler = new PhotoFillerHandler(new Handler());
        pfHandler.setListener(new PhotoFillerHandler.photoListener() {
            @Override
            public void PhotoRetrieved(Photo photo) {
                AllPhotos.getPhotoDeque().push(photo);
               // wrong place pfHandler.pumpMessage(photo);
            }
        });

        pfHandler.start();
        pfHandler.getLooper();
        populatePhotoStream(frx.FlickerGetRecentType, DOH_Constants.GetRecentURL, DOH_Constants.FlikrPhotoSearchSmallExtra, "20");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(Tag, "onCreateView");
        if(imageBrowserView == null)
        {
            imageBrowserView = LayoutInflater.from(getActivity()).inflate(R.layout.image_viewer, null);
        }
        FlkrGetRecentBtn = (Button) imageBrowserView.findViewById(R.id.recentStreamBtn);
        recentImage = (ImageView) imageBrowserView.findViewById(R.id.RecentImageView);
        titleView = (TextView) imageBrowserView.findViewById(R.id.imageTitle);
        FlkrOwnerBtn = (Button) imageBrowserView.findViewById(R.id.ownerSearchButton);

        Photo photo;
        if(AllPhotos.getAllPhotos().size() > 0 && AllPhotos.getPhoto(0) != null && AllPhotos.getPhoto(0).bitmap != null) {
            photo = AllPhotos.getPhoto(0);
            recentImage.setImageBitmap(photo.bitmap);
            titleView.setText(photo.title);
        }else{
            recentImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tree));
            titleView.setText("Abstact Tree");
            photo = new Photo();
            photo.bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fractal);
            photo.size = "url_o";
            photo.owner = "56603367@N03";
            photo.retrieveURL =  frx.assembleFlickerOwnerSearchURL(photo.owner, photo.size );
            photo.title = "Simple Fractal";
            photo.retrieveURL = "https://farm3.staticflickr.com/2831/11467918723_dea8ddee9b_s.jpg";
            AllPhotos.DefaultPhoto = photo;
            AllPhotos.getAllPhotos().add(photo);
        }
        FlkrGetRecentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populatePhotoStream(frx.FlickerGetRecentType, DOH_Constants.GetRecentURL, DOH_Constants.FlikrPhotoSearchSmallExtra, "20");
            }
        });
        FlkrOwnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populatePhotoStream(frx.FlickerOwnerSearchType, ownerLink, DOH_Constants.FlikrPhotoSearchSmallExtra, "20");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        pfHandler.quit();
        Log.i("ImageBrowser", "photo filler handler retired");
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
                                            Log.d(Tag, "Left <- Right");
                                            NextImage();
                                        } else {
                                            Log.d(Tag, "Left -> Right");
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
                                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }

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
        isNext = true;
        currentImage ++;
        //UpdateImage();
        SpinQueue(true);
    }

    public void PrevImage()
    {
        isNext = false;
        currentImage --;
        //UpdateImage();
        SpinQueue(false);
    }

    public void SpinQueue(boolean forward)
    {
        Log.d(Tag, "SpinQueue");
        if(!AllPhotos.getPhotoDeque().isEmpty()) {
            if (forward) {
                Photo photo = AllPhotos.getPhotoDeque().getFirst();
                AllPhotos.getPhotoDeque().removeFirst();
                AllPhotos.getPhotoDeque().addLast(photo);
            } else {
                Photo photo = AllPhotos.getPhotoDeque().getLast();
                AllPhotos.getPhotoDeque().removeLast();
                AllPhotos.getPhotoDeque().addFirst(photo);
            }
            Photo photo = AllPhotos.getPhotoDeque().getFirst();
            if (photo.bitmap != null) {
                recentImage.setImageBitmap(photo.bitmap);
                titleView.setText(photo.title);
                ownerLink = frx.assembleFlickerOwnerSearchURL(photo.owner, photo.size);
            } else {
                if (!AllPhotos.getPhotoDeque().isEmpty())
                    SpinQueue(forward);
            }
        }
    }
    public void UpdateImage()
    {
        ImageCount = AllPhotos.getAllPhotos().size();
        if( currentImage < 0)
            if( ImageCount == 0)
                currentImage = 0;
            else
                currentImage = ImageCount -1;
        if( currentImage >= ImageCount)
            currentImage = 0;
        final Bitmap bm = AllPhotos.getPhoto(currentImage).bitmap;
        if( bm != null) {
            recentImage.setImageBitmap(bm);
            Photo photo = AllPhotos.getPhoto(currentImage);
            titleView.setText(photo.title);
            ownerLink = frx.assembleFlickerOwnerSearchURL(photo.owner, photo.size);
        }
        else {
            if( currentImage != 0) {
                if (isNext)
                    currentImage++;
                else
                    currentImage--;
                UpdateImage();
            }
        }
    }
}
