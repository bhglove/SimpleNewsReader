package edu.cpsc4820.bhglove.simplenewsreader.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import edu.cpsc4820.bhglove.simplenewsreader.R;

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
    private WebView mWebView;
    private TextView mTitle;
    private ProgressBar webBar;
    private ImageButton mFavButton;
    Boolean toggleFav = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mFavButton = (ImageButton) findViewById(R.id.favArticleButton);
        //Retrieves the headline and link clicked on NewsFeed activity
        Intent intent = getIntent();
        String title = intent.getStringExtra("Title");
        String link = intent.getStringExtra("Link");

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
}

