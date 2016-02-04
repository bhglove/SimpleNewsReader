package edu.cpsc4820.bhglove.clemsonnews;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    //TODO Make the class able to accept rss feeds dynamically

public class ParseRSS extends AsyncTask<String , String, Boolean>{
    private List<String> mHeadlines;
    private List<String> mLinks;

    public ParseRSS(){
        mHeadlines = (List) new ArrayList<String>();
        mLinks = (List) new ArrayList<String>();

    }

    @Override
    protected Boolean doInBackground(String... params) {
       return getRSSList();
    }

    public List<String> getmHeadlines(){
        return mHeadlines;
    }

    private boolean getRSSList(){
        boolean retVal = false;
        try {
            URL url = new URL("http://www.clemson.edu/media-relations/rss.php?cat_id=2");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10 * 1000);
            conn.setConnectTimeout(10 * 1000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            InputStream inputStream = conn.getInputStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            // We will get the XML from an input stream

            xpp.setInput(inputStream, "UTF_8");

            boolean insideItem = false;

            // Returns the type of current event: START_TAG, END_TAG, etc..
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if (insideItem)
                            mHeadlines.add(xpp.nextText()); //extract the headline
                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        if (insideItem)
                            mLinks.add(xpp.nextText()); //extract the link of article
                    }
                }else if(eventType==XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                    insideItem=false;
                }

                eventType = xpp.next(); //move to next element
                retVal = true;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  retVal;
    }
}
