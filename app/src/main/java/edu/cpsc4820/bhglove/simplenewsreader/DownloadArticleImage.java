package edu.cpsc4820.bhglove.simplenewsreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by harryg on 2/21/2016.
 */
public class DownloadArticleImage extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageView;
    private Context context = null;
    private DataModel dataModel = null;



    public DownloadArticleImage(ImageView imageView){
        this.imageView = new WeakReference<ImageView>(imageView);
    }

    public void setContext(Context context){
        this.context = context;
    }
    @Override
    protected void onPreExecute(){
        if(context == null){
            cancel(true);
        }

        dataModel = DataModel.getInstance(context);
    }

    @Override
    protected void onPostExecute(Bitmap params){
        ImageView image = this.imageView.get();
        image.setImageBitmap(params);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap result = null;
        InputStream inputStream = null;
        BufferedInputStream bufferedInputStream = null;

        try{
            URLConnection conn = new URL(params[0]).openConnection();
            conn.connect();
            inputStream = conn.getInputStream();
            bufferedInputStream = new BufferedInputStream(inputStream, 1024);

            result = BitmapFactory.decodeStream(bufferedInputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bufferedInputStream != null){
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null){
                try{
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        dataModel.addBitmapToMemoryCache(String.valueOf(params[0]), result);
        return result;
    }
}
