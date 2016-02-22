package edu.cpsc4820.bhglove.simplenewsreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
 *
 */
public class DataModel {

    private static DataModel mData = null;
    private ArrayList<String> headlines; //The title of the articles
    private ArrayList<String> links;     //The weblink of the article
    private ArrayList<String> description; //A description of the article (Contains HTML data)
    private ArrayList<String> images;
    private ArrayList mListSelected;   //This is the selected feeds the user wants to display on the news feed
    private ArrayList mListAvailable;
    private Feeds feedList;
    private DatabaseModel db = null;
    private Context context;

    /** Initialize variables and set the three preset RSS feeds. */
   private DataModel(Context context){
       headlines = new ArrayList<String>();
       links = new ArrayList<String>();
       description = new ArrayList<String>();
       images = new ArrayList<String>();
       mListAvailable = new ArrayList<String>();
       mListSelected = new ArrayList<String>();
       this.context = context;

       if(db == null){
           db = new DatabaseModel(context);
       }
       feedList = new Feeds();
       for(int i = 0; i < feedList.getAllTitles().length; i++)
              db.createNewFeed(feedList.getTitleAt(i), feedList.getLinkAt(i));

   }

    /** Returns a single instance of the static DataModel
     *
      * @return void
     */
    public static DataModel getInstance(Context context){
        if(mData == null) mData = new DataModel(context);

        return mData;
    }

    /**
     *  Mediator function that adds a new RSS Feed from class Subscription to the list in class Feeds
     *  @param title
     *  @param link
     */
    public void createNewFeed(String title, String link){
        feedList.addFeed(title, link);
        db.createNewFeed(title, link);
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
        db.editFeed(oldTitle, title, link);
    }

    /**
     * Mediator function that searches the list of RSS Feeds in class Feeds
     * @param title
     * @return
     */
    public String findLink(String title){
        return db.findLink(title);
    }

    /**
     * Mediator function updates the added category to the selected category listview
     * @param value
     */
    public void setSelected(String value){
        Log.d("Add", "Add to Selected " + value);
        mListSelected.add(value);
        mListAvailable.remove(value);
        db.setSelected(value);
    }

    /**
     * Mediator function updates the added category to the available category listview
     * @param value
     */
    public void setAvailable(String value){
        Log.d("Add", "Add to Available " + value);
        mListAvailable.add(value);
        mListSelected.remove(value);
        db.setAvailable(value);
    }

    /**
     * Mediator function that allows Subscription to access all available RSS Feeds
     * @return ArrayList
     */
    public ArrayList getAvailable() {
        mListAvailable = db.getAvailable();
        return mListAvailable;
    }

    /**
     * Mediator function that allows Subscription to access all user selected RSS Feeds
     * @return ArrayList
     */
    public ArrayList getSelected() {
        mListSelected = db.getSelected();
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
    public ArrayList<String> getDescriptions() {
        return description;
    }

    public ArrayList<String> getImages(){
        return images;
    }

    /**
     * Mediator function that returns all of the feed links to selected feeds.
     * @return String[]
     */
    public String[] getAllSelected(){
       return db.getAllSelected();
    }

    public void setAllSelected(String[] selected) {
        for (String s : selected) {
            mListSelected.add(s);
        }
    }

    private void getData() {
        ParseRSS mParseRss = new ParseRSS();

        try {
            mParseRss.execute(mData.getAllSelected());
            mParseRss.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        headlines = mParseRss.getmHeadlines();
        links = mParseRss.getmLinks();
        description = mParseRss.getmDescription();
        images = mParseRss.getmImage();
    }

    /**
     * Returns the adapter needed for the NewsFeed ListView. Sets a description and article title.
     * @param context
     * @return ArrayAdapter
     */
    public ArrayAdapter createNewsFeedAdapter(final Context context) {
        ArrayAdapter mArrayAdapter;

        getData();

        /**
         * This array adapter uses a custom layout view to display an articles headline, description,
         * and image thumbnail. The thumbnail should be roughly 100dp by 100dp.
         * The original colors of the textviews were changed to blue for headlines and grey for
         * article descriptions.
         *
         *
         *
         * Split the articles into pages using the SQL Statement TODO SPLIT INTO PAGES
         */
        mArrayAdapter = new ArrayAdapter<String>(context, R.layout.article, headlines) {

            @Override
            public View getView(final int position, View convertView,
                                ViewGroup parent) {
                if(convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.article, parent, false);
                Handler handler = new Handler();
                final ImageView imageView = (ImageView) convertView.findViewById(R.id.article_imgview);


                //TODO Aysnyc tasks to download one image for this View.
                TextView textView1 = (TextView) convertView.findViewById(R.id.headline);
                TextView textView2 = (TextView) convertView.findViewById(R.id.description);
                final TextView textView3 = (TextView) convertView.findViewById(R.id.image_url);
                /*YOUR CHOICE OF COLOR*/
                textView1.setTextColor(Color.BLUE);
                textView1.setText(mData.getHeadlines().get(position));
                textView2.setTextColor(Color.GRAY);
                textView2.setText(Html.fromHtml(mData.getDescriptions().get(position).replaceAll("(<(/)img>)|(<img.+?>)", "")).toString().trim());
                textView3.setTextColor(Color.GRAY);
                try {
                    String image = mData.getImages().get(position + 1);
                    if (image == null)
                        image = "www.example.com";

                    final String imageUrl = image;
                    textView3.setText(imageUrl);
                }catch (IndexOutOfBoundsException e){
                    Log.d("Bounds", mData.getHeadlines().size() + " " + mData.getImages().size());
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DownloadArticleImage dl = new DownloadArticleImage(imageView);
                        String url = textView3.getText().toString();
                        if(!url.equals("www.example.com"))
                            dl.execute(url);
                        else
                            imageView.setImageResource(R.drawable.rss);
                    }
                }, 100);

                 return convertView;
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
        private ArrayList<String> mImage;

        public ParseRSS() {
            mHeadlines = (ArrayList) new ArrayList<String>();
            mLinks = (ArrayList) new ArrayList<String>();
            mDescription = (ArrayList) new ArrayList<String>();
            mImage = new ArrayList<String>();
        }

        public ArrayList<String> getmHeadlines() {
            return mHeadlines;
        }

        public ArrayList<String> getmLinks() {
            return mLinks;
        }
        public ArrayList<String> getmDescription() {
            return mDescription;
        }
        public ArrayList<String> getmImage() {
            return mImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("Thread", "Pre Execute");
        }

        @Override
        protected void onPostExecute(String params) {
            super.onPostExecute(params);

        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("Thread", "Background");
            getRSSList(params);

            return "Task Completed.";
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
                Log.d("Feed", "Parsing: " + feed[i]);
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
                        String headline = null;
                        String description = null;
                        String link = null;
                        String image = null;

                        if (eventType == XmlPullParser.START_TAG) {
                            String name = xpp.getName();
                            if (xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = true;
                            } else if (xpp.getName().equalsIgnoreCase("title")) {
                                if (insideItem) {
                                    headline = xpp.nextText();
                                    mHeadlines.add(headline); //extract the headline
                                }
                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                if (insideItem) {
                                    link = xpp.nextText();
                                    mLinks.add(link); //extract the link of article
                                }
                            } else if (xpp.getName().equalsIgnoreCase("description")) {
                                if (insideItem) {
                                    description = xpp.nextText();
                                    mDescription.add(description); //extract the category
                                }
                                // Inspiration from this and studying the xml data allowed for parsing https://xjaphx.wordpress.com/2011/10/16/android-xml-adventure-parsing-xml-data-with-xmlpullparser/
                            } else if(xpp.getName().contains("media:content")) {
                                String imageUrl = xpp.getAttributeValue(null, "url");
                                if(imageUrl != null)
                                    Log.d("Feed", imageUrl);
                                image = imageUrl;
                                mImage.add(image);
                            } else if(xpp.getName().equalsIgnoreCase("image")){
                                String imageUrl = xpp.nextText();
                                image = imageUrl;
                                mImage.add(image);
                            }

                            /* Date
                              else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                if (insideItem) {
                                    Log.d("Date", feed[i] + ": " + xpp.nextText().substring(0, 17));

                                }
                            }
                            */
                        } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                        }
                        if(mHeadlines.size() > mImage.size()){
                            mImage.add(image);
                            Log.d("Bounds", i + " added for bounds");
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
    }

}
