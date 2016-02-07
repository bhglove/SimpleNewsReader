package edu.cpsc4820.bhglove.simplenewsreader;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Benjios on 2/4/2016.
 *
 * Resources:
 *
 * Remove the image tag from html strings
 * http://stackoverflow.com/questions/11178533/how-to-skip-image-tag-in-html-data-in-android?answertab=active#tab-top
 *
 *
 */
public class DataModel {

    private static DataModel mData = null;
    private ArrayList<String> headlines;
    private ArrayList<String> links;
    private ArrayList<String> description;
    private ArrayList mListSelected;   //This is the selected feeds the user wants to display on the news feed
    private ArrayList mListAvailable;

   private DataModel(){
       headlines = new ArrayList<String>();
       links = new ArrayList<String>();
       description = new ArrayList<String>();
       mListAvailable = new ArrayList<String>();
       mListSelected = new ArrayList<String>();

       for (PopularFeeds cat : PopularFeeds.values())
           mListAvailable.add(cat.toString());
   }

    public static DataModel getInstance(){
        if(mData == null) mData = new DataModel();

        return mData;
    }

    public void addToSelectedFeed(String value){
        Log.d("Add", "Add to Selected" + value);
        mListSelected.add(value);
        mListAvailable.remove(value);
    }

    public void addToAvailableFeed(String value){
        Log.d("Add", "Add to Available" + value);
        mListAvailable.add(value);
        mListSelected.remove(value);
    }

    public ArrayList getmListAvailable() {
        return mListAvailable;
    }

    public ArrayList getmListSelected() {
        return mListSelected;
    }

    public ArrayList<String> getHeadlines() {
        return headlines;
    }

    public ArrayList<String> getLinks() {
        return links;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public String[] getAllSelectedFeed(){
        String [] feed = new String[mListSelected.size()];
        for(int i = 0; i < mListSelected.size(); i++)
            feed[i] = PopularFeeds.valueOf(mListSelected.get(i).toString()).toFeed();
        return feed;
    }

    public void setAllSelected(String[] selected) {
        for (String s : selected) {
            mListSelected.add(s);
        }
    }

    private void getData() {
        ParseRSS mParseRss = new ParseRSS();

        try {
            mParseRss.execute(mData.getAllSelectedFeed());
            mParseRss.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        headlines = mParseRss.getmHeadlines();
        links = mParseRss.getmLinks();
        description = mParseRss.getmDescription();
    }

    public ArrayAdapter createNewsFeedAdapter(Context context) {
        ArrayAdapter mArrayAdapter;

        getData();
        //TODO Display Readable names
        //TODO Change Simple List to #2 pass in DataModel
        mArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_2, android.R.id.text1, headlines) {

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
                TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
                /*YOUR CHOICE OF COLOR*/
                textView1.setTextColor(Color.BLUE);
                textView1.setText(mData.getHeadlines().get(position));
                textView2.setTextColor(Color.GRAY);
                textView2.setText(Html.fromHtml(mData.getDescription().get(position).replaceAll("(<(/)img>)|(<img.+?>)", "")).toString().trim());

                return view;
            }
        };
        return mArrayAdapter;
    }

    /**
     * Created by Benjamin Glover on 2/3/2016.
     * <p/>
     * <p/>
     * Resources:
     * <p/>
     * Using Async task to fetch data in the background
     * http://stackoverflow.com/questions/20017448/android-app-force-close-when-sending-data-to-local-webserver-from-android
     * <p/>
     * Pulling and parsing RSS Feed data into Lists
     * http://jmsliu.com/1508/rss-reader-android-app-tutorial-3-parse-xml-in-android.html
     * Better input stream for url connections
     * http://stackoverflow.com/questions/20017448/android-app-force-close-when-sending-data-to-local-webserver-from-android
     */

    private class ParseRSS extends AsyncTask<String, Integer, String> {
        private ArrayList<String> mHeadlines;
        private ArrayList<String> mLinks;
        private ArrayList<String> mDescription;


        public ParseRSS() {
            mHeadlines = (ArrayList) new ArrayList<String>();
            mLinks = (ArrayList) new ArrayList<String>();
            mDescription = (ArrayList) new ArrayList<String>();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("Thread", "Background");
            getRSSList(params);

            return "Task Completed.";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("Thread", "Pre Execute");
        }

        public ArrayList<String> getmHeadlines() {
            return mHeadlines;
        }

        public ArrayList<String> getmLinks() {
            return mLinks;
        }

        private boolean getRSSList(String[] feed) {
            boolean retVal = false;
            Log.d("Feed", "There are " + feed.length + " selected feeds");
            for (int i = 0; i < feed.length; i++) {
                try {
                    URL url = new URL(feed[i]);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(100 * 1000);
                    conn.setConnectTimeout(100 * 1000);
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
                            } else if (xpp.getName().equalsIgnoreCase("description")) {
                                if (insideItem)
                                    mDescription.add(xpp.nextText()); //extract the category
                            }
                        } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                        }

                        eventType = xpp.next(); //move to next element
                        retVal = true;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    retVal = false;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    retVal = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    retVal = false;
                }
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String params) {
            super.onPostExecute(params);
            Log.i("Thread", "Post Execute");
        }

        public ArrayList<String> getmDescription() {
            return mDescription;
        }
    }

}
