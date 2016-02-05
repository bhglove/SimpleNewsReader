package edu.cpsc4820.bhglove.clemsonnews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 *
 * Resources:
 *
 * Web View
 * http://developer.android.com/intl/zh-tw/reference/android/webkit/WebView.html
 */
public class ArticleActivity extends AppCompatActivity {
    private TextView mTitleView;
    private WebView mWebView;
    private TextView mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Intent intent = getIntent();

        String headline = intent.getStringExtra("Headline");
        String link = intent.getStringExtra("Link");
        String description = intent.getStringExtra("Description");

        mTitleView = (TextView) findViewById(R.id.articleTitleView);
        mWebView = (WebView) findViewById(R.id.webView);
        mDescription = (TextView) findViewById(R.id.articleDescriptionView);

        if(headline == null) headline = "oh no";

        mTitleView.setText(headline);
        mDescription.setText(Html.fromHtml(description));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);

        mWebView.setWebViewClient(new WebViewClient());
        if(link == null) link = "http://www.clemson.edu/";
        mWebView.loadUrl(link);

    }
}
