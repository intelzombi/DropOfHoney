package com.gunsnhony.dropofhoney.support;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;  // vs android.net.Uri vs java.net.URI :=>  Google vs Java:  Ip4 vs Ip4 & Ip6: simple exceptions vs verbose exceptions
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Hugh on 7/13/2017.
 */

public class RestEasy {

    public static Bitmap readByteToBitmap(InputStream in ) throws IOException
    {
        byte[] bytes = new byte[1024];
        int bytesRead = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
            do {
                bytesRead = in.read(bytes);
                baos.write(bytes, 0, bytesRead);
            }while(bytesRead != -1);
        byte[] bmBA = baos.toByteArray();
        return BitmapFactory.decodeByteArray(bmBA,0,bmBA.length);
    }

    public static String urlToString(String urlString)
    {
        InputStream in = null;
        String feedIn = "";
        HttpsURLConnection urlConnection = null;
        try {
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpsURLConnection) url.openConnection();
                // get the response
                in = new BufferedInputStream(urlConnection.getInputStream());
                if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                {
                    return null;
                }
                byte[] bytes = new byte[1024];
                int bytesRead = 0;
                int stride = 1024;

                do {
                    bytesRead = in.read(bytes, 0, stride);
                    feedIn += new String(bytes);
                }while(bytesRead != -1);

            } catch (IOException e) {
                if (urlConnection != null)
                    urlConnection.getErrorStream();
                e.printStackTrace();
            } finally {
                if(in != null)
                    in.close();
                if(urlConnection != null)
                    urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e("RestEasy", e.getMessage());
        }
        return feedIn;
    }

    public static Bitmap urlToBitmap(String urlString)
    {
        InputStream in = null;
        Bitmap bm = null;
        HttpsURLConnection urlConnection = null;
        try {
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpsURLConnection) url.openConnection();
                // get the response
                in = new BufferedInputStream(urlConnection.getInputStream());
                if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                {
                    return null;
                }
                bm = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException mue) {
                Log.e("RestEasy", "Malformed URL: " + urlString + "\n" + mue.getLocalizedMessage(), mue);
            }catch (IOException e) {
                if (urlConnection != null)
                    Log.e("RestEasy", "" + urlConnection.getErrorStream(), e);
                    e.printStackTrace();
            } catch (OutOfMemoryError oome) {
                Log.e("RestEasy", "Out of Memory creating a bitmap with url: " + urlString + "\n" + oome.getLocalizedMessage() , oome);
                oome.printStackTrace();
            }
            finally {
                if(in!=null)
                    in.close();
                if(urlConnection != null)
                    urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e("RestEasy", e.getMessage());
        }
        return bm;
    }

    public static Bitmap GetPhotoRestStream( String urlString) {
        return urlToBitmap(urlString);
    }
}
