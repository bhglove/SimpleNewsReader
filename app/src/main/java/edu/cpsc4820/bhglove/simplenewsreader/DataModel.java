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
 * Deprecated as of 2/7/2016
 *
 * The DataModel class is a public class responsible for being the central hub of all data list,
 * with use of a singleton. This class also acts as the mediator to NewsFeed and Feeds, by storing
 * all list and adapters in this single class. DataModel first populates stored RSS feeds from the
 * class Feed and then parses the information from that feed into three separate ArrayLists from the
 * private class ParseRSS.
 *
 * Created by Benjamin Glover on 2/4/2016.
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
    private ArrayList<String> headlines; //The title of the articles
    private ArrayList<String> links;     //The weblink of the article
    private ArrayList<String> description; //A description of the article (Contains HTML data)
    private ArrayList mListSelected;   //This is the selected feeds the user wants to display on the news feed
    private ArrayList mListAvailable;
    private Feeds feedList;

    /** Initialize variables and set the three preset RSS feeds. */
   private DataModel(){
       headlines = new ArrayList<String>();
       links = new ArrayList<String>();
       description = new ArrayList<String>();
       mListAvailable = new ArrayList<String>();
       mListSelected = new ArrayList<String>();

       feedList = new Feeds();
       for(String s : feedList.getAllTitles()){
           mListAvailable.add(s);
       }

   }

    /** Returns a single instance of the static DataModel
     *
      * @return void
     */
    public static DataModel getInstance(){
        if(mData == null) mData = new DataModel();

        return mData;
    }

    /**
     *  Mediator function that adds a new RSS Feed from class Subscription to the list in class Feeds
     *  @param title
     *  @param link
     */
    public void createNewFeed(String title, String link){
        feedList.addFeed(title, link);
    }

    /**
     * Mediator function that allows the user to edit a created RSS Feed.
     * @param oldTitle
     * @param title
     * @param link
     */
    public void editFeed(String oldTitle, String title, String link){
        if(mListSelected.contains(oldTitle)){
            mListSelected.remove(oldTitle);
            mListSelected.add(title);
        }
        feedList.editFeed(oldTitle, title, link);
    }

    /**
     * Mediator function that searches the list of RSS Feeds in class Feeds
     * @param title
     * @return
     */
    public String findLink(String title){
        return feedList.findLink(title);
    }

    /**
     * Mediator function updates the added category to the selected category listview
     * @param value
     */
    public void addToSelectedFeed(String value){
        Log.d("Add", "Add to Selected" + value);
        mListSelected.add(value);
        mListAvailable.remove(value);
    }

    /**
     * Mediator function updates the added category to the available category listview
     * @param value
     */
    public void addToAvailableFeed(String value){
        Log.d("Add", "Add to Available" + value);
        mListAvailable.add(value);
        mListSelected.remove(value);
    }

    /**
     * Mediator function that allows Subscription to access all available RSS Feeds
     * @return ArrayList
     */
    public ArrayList getmListAvailable() {
        return mListAvailable;
    }

    /**
     * Mediator function that allows Subscription to access all user selected RSS Feeds
     * @return ArrayList
     */
    public ArrayList getmListSelected() {
        return mListSelected;
    }

    /**
     * Mediator function that returns the Titles for all articles
     * @return ArrayList
     */
    public ArrayList<String> getHeadlines() {
        return headlines;
    }

    /**
     * Mediator function that returns all Links for all articles
     * @return ArrayList
     */
    public ArrayList<String> getLinks() {
        return links;
    }

    /**
     * Mediator function that returns all Descriptions for all articles
     * @return ArryList
     */
    public ArrayList<String> getDescription() {
        return description;
    }

    /**
     * Mediator function that returns all of the feed links to selected feeds.
     * @return String[]
     */
    public String[] getAllSelectedFeed(){
        String [] feed = new String[mListSelected.size()];
        for(int i = 0; i < mListSelected.size(); i++)
            feed[i] = feedList.findLink(mListSelected.get(i).toString());
            //Deprecated
            //feed[i] = PopularFeeds.valueOf(mListSelected.get(i).toString()).toFeed();

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

    /**
     * Returns the adapter needed for the NewsFeed ListView. Sets a description and article title.
     * @param context
     * @return ArrayAdapter
     */
    public ArrayAdapter createNewsFeedAdapter(Context context) {
        ArrayAdapter mArrayAdapter;

        getData();

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
     * Private class used to Parse RSS Feeds
     * Created by Benjamin Glover on 2/3/2016.
     * Resources:
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

        /**
         * This function parses a list of RSS Feeds and set three ArrayList to appropriate values
         * @param feed
         * @return boolean
         */
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
