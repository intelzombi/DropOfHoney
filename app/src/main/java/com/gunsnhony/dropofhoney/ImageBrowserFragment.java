package com.gunsnhony.dropofhoney;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;


/**
 * Created by Hugh on 7/12/2017.
 */

public class ImageBrowserFragment extends Fragment {
    private Context context;
    View imageBrowserView = null;
    private TextView titleView;
    private ImageView recentImage;
    private Button FlkrGetRecentBtn;
    private Button FlkrOwnerBtn;
    Bitmap defaultBit;
    FlickrRestXML frx = new FlickrRestXML();
    PhotoFillerHandler<Photo> photoFillHandler;

    String Tag = "GESTURE";
    String ownerLink = "";

    private int currentImage = 0;
    int ImageCount;
    boolean isNext = true;

    private final int Max_Swipe_Distance = 900;
    private final int Min_Swipe_Velocity = 80;

    public class RestResponseTask extends AsyncTask<String, Void, ArrayList<Photo>> {

        @Override
        protected ArrayList<Photo> doInBackground(String ... restCalls) {
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
        protected void onPostExecute(ArrayList<Photo> photos) {
            AllPhotos.setAllPhotos(photos);
        }
    }

    private boolean populatePhotoStream(String restCallType, String urlString, String size, String count ){
        AllPhotos.removeAll();
        RestResponseTask rrt = new RestResponseTask();
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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        populatePhotoStream(frx.FlickerGetRecentType, DOH_Constants.GetRecentURL, DOH_Constants.FlikrPhotoSearchSmallExtra, "20");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        UpdateImage();
    }

    public void PrevImage()
    {
        isNext = false;
        currentImage --;
        UpdateImage();
    }

    public void UpdateImage()
    {
        ImageCount = AllPhotos.getAllPhotos().size();
        if( currentImage < 0)
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
