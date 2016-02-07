package edu.cpsc4820.bhglove.simplenewsreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 *
 * Resources:
 *
 * Web View
 * http://developer.android.com/intl/zh-tw/reference/android/webkit/WebView.html
 */
public class ArticleActivity extends AppCompatActivity {

    private WebView mWebView;

    private ProgressBar webBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        webBar = (ProgressBar) findViewById(R.id.webProgressBar);
        Intent intent = getIntent();

        String link = intent.getStringExtra("Link");

        mWebView = (WebView) findViewById(R.id.webView);
        webBar.setVisibility(ProgressBar.GONE);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);

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
    }
}
