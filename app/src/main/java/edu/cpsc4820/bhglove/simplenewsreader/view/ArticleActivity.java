package edu.cpsc4820.bhglove.simplenewsreader.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cpsc4820.bhglove.simplenewsreader.R;
import edu.cpsc4820.bhglove.simplenewsreader.controller.AccessDatabase;
import edu.cpsc4820.bhglove.simplenewsreader.controller.DatabaseController;

/**
 * This activity class displays ArticleImage web page retrieved from the NewsFeed
 * class as ArticleImage large WebView.
 * Created by Benjamin Glover on 2/17/2016.
 * Resources:
 *
 * Web View
 * http://developer.android.com/intl/zh-tw/reference/android/webkit/WebView.html
 */
public class ArticleActivity extends AppCompatActivity {
    private DatabaseController mData;
    private WebView mWebView;
    private TextView mTitle;
    private ProgressBar webBar;
    private ImageButton mFavButton;
    private String link;
    Boolean toggleFav = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String title = intent.getStringExtra("Title");
        link = intent.getStringExtra("Link");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IsFavortieTask task = new IsFavortieTask();
                task.execute();
            }
        });
        thread.start();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        mData = DatabaseController.getInstance(getApplicationContext());



        mFavButton = (ImageButton) findViewById(R.id.favArticleButton);
        setFavImage();
        Log.d("Fav", "UI Fav is " + toggleFav);

        //Retrieves the headline and link clicked on NewsFeed activity


        mTitle = (TextView) findViewById(R.id.article_title);
        if(title == null){
            mTitle.setText(R.string.app_name);
        }
        else{
            mTitle.setText(title);
        }

        webBar = (ProgressBar) findViewById(R.id.webProgressBar);

        mWebView = (WebView) findViewById(R.id.webView);
        webBar.setVisibility(ProgressBar.GONE);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);

        /** Sets the progress bar to the appropriate loading time.*/
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100 && webBar.getVisibility() == ProgressBar.GONE) {
                    webBar.setVisibility(ProgressBar.VISIBLE);
                }
                webBar.setProgress(newProgress);
                if (newProgress == 100) {
                    webBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient());
        if(link == null) link = "http://www.clemson.edu/";
        mWebView.loadUrl(link);

        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavImage();
            }
        });
    }

    @Override
    public void onBackPressed(){
        mWebView.onPause();

        MarkAsFavorite fav = new MarkAsFavorite();
        fav.execute();
        Intent intent = new Intent(ArticleActivity.this, NewsFeed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }

    private void toggleFavImage(){
        if(toggleFav){
            mFavButton.setImageResource(R.drawable.ic_star_border_white_18dp);
            toggleFav = false;
        }
        else{
            mFavButton.setImageResource(R.drawable.ic_star_red_400_18dp);
            toggleFav = true;
        }
    }

    private void setFavImage(){
        if(toggleFav){
            mFavButton.setImageResource(R.drawable.ic_star_red_400_18dp);
        }
        else{
            mFavButton.setImageResource(R.drawable.ic_star_border_white_18dp);
        }
    }
   private class MarkAsFavorite extends AsyncTask<Void, Void, Integer>{

       @Override
       protected Integer doInBackground(Void ...params) {
           try{
               AccessDatabase access = AccessDatabase.getInstance(getApplicationContext());
               SharedPreferences pref = getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE);
               String rssTitle = mData.getContentRssTitle(link);
               String headline = mData.getContentHeadline(link);
               String description = mData.getContentDescription(link);
               String image = mData.getContentImageUrl(link);
               String date = mData.getContentDate(link);

               JSONObject object = new JSONObject();
               object.accumulate("rss_title", rssTitle);
               object.accumulate("headline", headline);
               object.accumulate("description", description);
               object.accumulate("permalink", link);
               if(image == null){
                   image = "null";
               }
               object.accumulate("imageUrl", image);
               object.accumulate("date", date);
               if(toggleFav){
                   object.accumulate("fav", 1);
               }
               else{
                   object.accumulate("fav", 0);
               }
               int user_id = pref.getInt(MainActivity.KEY_USERID, 0);
               String variable = "object="+object.toString()+"&user_id="+user_id;
               access.executeForInt(variable, access.ADD_CONTENT);
               Log.d("object", object.toString());
           } catch (JSONException e) {
               e.printStackTrace();
           }
           return 0;
       }
   }
    private class IsFavortieTask extends AsyncTask<Void, Void, Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
            AccessDatabase access = AccessDatabase.getInstance(getApplicationContext());
            SharedPreferences pref = getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE);
            int userid = pref.getInt(MainActivity.KEY_USERID, 0);
            String variables = "user_id="+userid+"&link="+link;
            Log.d("Fav", "variables " + variables);
            int result = access.executeForInt(variables, access.IS_FAV);
            if(result == 1){
              toggleFav = true;
            }
            else{
                toggleFav = false;
            }
            Log.d("Fav", "Fav is " + toggleFav);
            Log.d("Fav", "Link is " + link);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setFavImage();
                }
            });
            return 0;
        }
    }
}

