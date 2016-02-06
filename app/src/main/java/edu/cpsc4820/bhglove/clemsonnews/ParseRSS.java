package edu.cpsc4820.bhglove.clemsonnews;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Benjamin Glover on 2/3/2016.
 *
 *
 * Resources:
 *
 * Using Async task to fetch data in the background
 * http://stackoverflow.com/questions/20017448/android-app-force-close-when-sending-data-to-local-webserver-from-android
 *
 * Pulling and parsing RSS Feed data into Lists
 * http://jmsliu.com/1508/rss-reader-android-app-tutorial-3-parse-xml-in-android.html
 * Better input stream for url connections
 * http://stackoverflow.com/questions/20017448/android-app-force-close-when-sending-data-to-local-webserver-from-android
 *
 *
 */

//TODO Use Async Properly

