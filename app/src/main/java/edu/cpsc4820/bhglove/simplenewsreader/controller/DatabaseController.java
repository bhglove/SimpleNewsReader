package edu.cpsc4820.bhglove.simplenewsreader.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

import edu.cpsc4820.bhglove.simplenewsreader.model.DatabaseModel;
import edu.cpsc4820.bhglove.simplenewsreader.model.Feeds;
import edu.cpsc4820.bhglove.simplenewsreader.R;

/**
 * The DatabaseController class is a public class responsible for being the central hub of all data,
 * with use of a singleton. This class also acts as the controller to NewsFeed and Feeds, by storing
 * all list and adapters in this single class. DatabaseController first populates stored RSS feeds from the
 * class Feed and then parses the information from that feed into three separate ArrayLists from the
 * private class ParseRSS.
 * Created by Benjamin Glover on 2/4/2016.
 * V2.0
 * The DatabaseController class was modified to incorporate an internal database
 *
 *
 *
 * Resources:
 *
 * Remove the image tag from html strings
 * http://stackoverflow.com/questions/11178533/how-to-skip-image-tag-in-html-data-in-android?answertab=active#tab-top
 * */
public class DatabaseController {

    private static DatabaseController mData = null;
    private ArrayList<String> headlines; //The title of the articles
    private ArrayList<String> links;     //The weblink of the article
    private ArrayList<String> description; //A description of the article (Contains HTML data)
    private ArrayList<String> images;
    private ArrayList<String> dates;



    private ArrayList mListSelected;   //This is the selected feeds the user wants to display on the news feed
    private ArrayList mListAvailable;
    private Feeds feedList;
    private DatabaseModel db = null;
    private Context context;
    private LruCache<String, Bitmap> mMemoryCache;

    private int progress;

    /** Initialize variables and set the three preset RSS feeds. */
   private DatabaseController(Context context){
       headlines = new ArrayList<String>();
       links = new ArrayList<String>();
       description = new ArrayList<String>();
       images = new ArrayList<String>();
       dates = new ArrayList<String>();

       mListAvailable = new ArrayList<String>();
       mListSelected = new ArrayList<String>();
       this.context = context;
       progress = 0;
       if(db == null){
           db = new DatabaseModel(context);
       }
       feedList = new Feeds();
       for(int i = 0; i < feedList.getAllTitles().length; i++)
              db.createNewFeed(feedList.getTitleAt(i), feedList.getLinkAt(i));

       final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
       final int cacheSize = maxMemory / 8;

       mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
           @Override
            protected int sizeOf(String key, Bitmap bitmap){
               return bitmap.getByteCount() / 1024;
           }
       };
   }

    /** Returns a single instance of the static DatabaseController
     *
     * @return void
     */
    public static DatabaseController getInstance(Context context){
        if(mData == null) mData = new DatabaseController(context);

        return mData;
    }

    /**
     * Returns the bitmap associated to that key.
     * @param key
     * @return
     */
    public Bitmap getBitmapFromCache(String key){
        return mMemoryCache.get(key);
    }

    /**
     * Adds a Bitmap based on a key and also checks for duplicates.
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if(getBitmapFromCache(key) == null && bitmap != null){
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * Public method to set a bitmap to an imageview. This is the method that updates in the
     * listview.
     * @param url
     * @param imageView
     */
    public void loadBitmap(String url, ImageView imageView){
        final String imageUrl = url;
        final ImageView image = imageView;
        final Bitmap bitmap = getBitmapFromCache(url);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }
        else {
            if(url.equals("www.exapmle.com")){
                imageView.setImageResource(R.drawable.rss);
            }
            else{
                DownloadArticleImage dl = new DownloadArticleImage(image);
                dl.setContext(context);
                dl.execute(imageUrl);
            }
        }
    }

    /**
     *  Mediator function that adds a new RSS Feed from class Subscription to the list in class Feeds
     *  @param title
     *  @param link
     */
    public void createNewFeed(String title, String link){
        //feedList.addFeed(title, link);
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
        //feedList.editFeed(oldTitle, title, link);
        db.editFeed(oldTitle, title, link);
    }

    /**
     * Mediator function that searches the list of RSS Feeds in class Feeds
     * @param title
     * @return
     */
    public String findRssLink(String title){
        return db.findRssLink(title);
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
     * Receives the associated headline of an article.
     * @param link
     * @return
     */
   public String getContentHeadline(String link){
       String retVal;
       retVal = db.getKeyContentHeadline(link);
       return retVal;
   }

    /**
     * Receives the associated description of an article.
     * @param link
     * @return
     */
    public String getContentDescription(String link){
        String retVal;
        retVal = db.getKeyContentDescription(link);
        return retVal;
    }

    /**
     * Receives the associated image url of an article.
     * @param link
     * @return
     */
    public String getContentImageUrl(String link){
        String retVal;
        retVal = db.getKeyContentImgLink(link);
        return retVal;
    }

    /**
     * Receives the associated date id of an article.
     * @param link
     * @return
     */
    public int getContentDateInt(String link){
        int retVal;
        retVal = db.getKeyContentDateId(link);
        return retVal;
    }

    /**
     * Mediator function that returns the Titles for all articles
     * @return ArrayList
     */
    public ArrayList<String> getHeadlines() {
        headlines = db.getHeadlines();
        return headlines;
    }

    /**
     * Mediator function that returns all Links for all articles
     * @return ArrayList
     */
    public ArrayList<String> getLinks() {
        links = db.getLinks();
        return links;
    }

    /**
     * Mediator function that returns all Descriptions for all articles
     * @return ArryList
     */
    public ArrayList<String> getDescriptions() {
        description = db.getDescriptions();
        return description;
    }

    /**
     * Returns the image urls from the database.
     * @return ArrayList Images stored in database.
     */
    public ArrayList<String> getImages(){
        images = db.getImages();
        return images;
    }

    /**
     * Mediator function that returns the title of an article from the database.
     * @param link Permalink of the article
     * @return String Content Title
     */
    public String getContentTitle(String link){
        return db.getContentRssTitle(db.getContentId(link));
    }

    /**
     * Returns the Date associated with a particular article.
     * @param link
     * @return
     */
    public String getContentDate(String link){
        return db.getContentDate(db.getContentId(link));
    }

    public ArrayList<String> getDates(){
        return dates;
    }

    /**
     * Mediator function that returns all of the feed links to selected feeds.
     * @return String[]
     */
    public String[] getAllSelected(){
       return db.getAllSelected();
    }


    /**
     * By using the link of a particular rss feed, the method collects the information of the article:
     * Headlines, Descriptions, Links, ImageUrl, and Date. And creates a new entry in the database.
     * @param feedLink - RSS Link
     */
    private void parseDataFromXmlUrl(String feedLink) {
        ParseRSS mParseRss = new ParseRSS();

        try {
            mParseRss.execute(feedLink);
            mParseRss.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        headlines = mParseRss.getmHeadlines();
        description = mParseRss.getmDescription();
        links = mParseRss.getmLinks();
        images = mParseRss.getmImage();
        dates = mParseRss.getmDate();

        for(int i = 0; i < headlines.size(); i++){
            db.createNewContent(feedLink, headlines.get(i), description.get(i), links.get(i),
                    images.get(i), dates.get(i));
        }
    }

    /**
     * Returns the progress made by the thread downloading and parsing rss feeds.
     * @return int
     */
    public int getProgress(){
        return progress;
    }

    public void refreshDataContent(){
        int i = 0;
        progress = 0;
        db.refreshContent();
        if(mData.getAllSelected().length == 0){
            progress = 100;
        }
        else {
            for (String feed : mData.getAllSelected()) {
                i++;
                parseDataFromXmlUrl(feed);
                progress = (100 / mData.getAllSelected().length) * i;
                Log.d("Progress", "Progress at: " + progress);
            }
        }
    }



    /**
     * Returns the adapter needed for the NewsFeed ListView. Sets a description and article title.
     *
     * This array adapter uses a custom layout view to display an articles headline, description,
     * and image thumbnail. The thumbnail should be roughly 100dp by 100dp.
     * The original colors of the textviews were changed to blue for headlines and grey for
     * article descriptions.
     * @param context
     * @return ArrayAdapter
     *
     * New Resources 3/6/2016
     * Space between List View items
     * http://stackoverflow.com/questions/4984313/spacing-between-listview-items-android
     *
     *
     */
    public ArrayAdapter createNewsFeedAdapter(final Context context) {
        ArrayAdapter mArrayAdapter;

        mArrayAdapter = new ArrayAdapter<String>(context, R.layout.article, mData.getHeadlines()) {

            @Override
            public View getView(final int position, View convertView,
                                ViewGroup parent) {
                if(convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.article, parent, false);
                //Handler handler = new Handler();
                final ImageView imageView = (ImageView) convertView.findViewById(R.id.article_imgview);

                TextView headlineTxtView = (TextView) convertView.findViewById(R.id.headline);
                TextView descriptionTxtView = (TextView) convertView.findViewById(R.id.description);
                TextView pubDateTxtView = (TextView) convertView.findViewById(R.id.pubDate);
                TextView rssTitleTxtView = (TextView) convertView.findViewById(R.id.rssTitle);

                /*YOUR CHOICE OF COLOR*/
                headlineTxtView.setTextColor(Color.BLUE);
                headlineTxtView.setText(mData.getHeadlines().get(position));


                String description = Html.fromHtml(mData.getDescriptions().get(position).replaceAll("(<(/)img>)|(<img.+?>)", "")).toString().trim();
                descriptionTxtView.setText(description);
                descriptionTxtView.setTextColor(Color.GRAY);

                pubDateTxtView.setText(getContentDate(mData.getLinks().get(position)));
                pubDateTxtView.setTextColor(Color.GRAY);

                rssTitleTxtView.setTextColor(Color.GRAY);
                rssTitleTxtView.setText(getContentTitle(mData.getLinks().get(position)));
                try {
                    String image = mData.getImages().get(position);
                    if (image == null) {
                        image = "www.example.com";
                    }
                    final String imageUrl = image;

                    if(image.contains("www.example.com")) {
                        imageView.setImageResource(R.drawable.rss);
                        Log.d("Image", "Set stock image ");
                    }
                    else{
                        loadBitmap(imageUrl, imageView);
                    }
                }catch (IndexOutOfBoundsException e){
                    Log.d("Bounds", mData.getHeadlines().size() + " " + mData.getImages().size());
                }
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
        private ArrayList<String> mDate;

        public ParseRSS() {
            mHeadlines = (ArrayList) new ArrayList<String>();
            mLinks = (ArrayList) new ArrayList<String>();
            mDescription = (ArrayList) new ArrayList<String>();
            mImage = new ArrayList<String>();
            mDate = new ArrayList<String>();
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
        public ArrayList<String> getmDate() { return mDate; }

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
                        if (eventType == XmlPullParser.START_TAG) {

                            if (xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = true;
                            } else if (xpp.getName().equalsIgnoreCase("title")) {
                                if (insideItem) {
                                    mHeadlines.add(xpp.nextText()); //extract the headline
                                    Log.d("Feed", "Headline size:" + mHeadlines.size()
                                            + " Image size" + mImage.size() + " Image Count:"
                                            + mImage.size());
                                }
                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                if (insideItem) {
                                    mLinks.add(xpp.nextText()); //extract the link of article
                                }
                            } else if (xpp.getName().equalsIgnoreCase("description")) {
                                if (insideItem) {
                                    mDescription.add(xpp.nextText()); //extract the category
                                }
                                // Inspiration from this and studying the xml data allowed for parsing https://xjaphx.wordpress.com/2011/10/16/android-xml-adventure-parsing-xml-data-with-xmlpullparser/
                            } else if(xpp.getName().contains("media:content") && (mImage.size() < mHeadlines.size())) {
                                String imageUrl = xpp.getAttributeValue(null, "url");
                                if(imageUrl != null)
                                    Log.d("Feed", imageUrl);
                                Log.d("Feed", "Image #" + mImage.size() + " " + imageUrl);
                                if(imageUrl.contains(".jp") || imageUrl.contains(".png") || imageUrl.contains("image")) {
                                    mImage.add(imageUrl);
                                }
                            }else if(xpp.getName().contains("media:thumbnail") && (mImage.size() < mHeadlines.size())) {
                                String imageUrl = xpp.getAttributeValue(null, "url");
                                if(imageUrl != null)
                                    Log.d("Feed", imageUrl);
                                Log.d("Feed", "Image #" + " " + imageUrl + mImage.size());

                                if(imageUrl.contains(".jp") || imageUrl.contains(".png") || imageUrl.contains("image")) {
                                    mImage.add(imageUrl);
                                }
                            } else if(xpp.getName().equalsIgnoreCase("thumbnail") && (mImage.size() < mHeadlines.size())){
                                String imageUrl = xpp.nextText();
                                if(imageUrl.contains(".jp") || imageUrl.contains(".png") || imageUrl.contains("image")) {
                                    mImage.add(imageUrl);
                                }
                                Log.d("Feed", "Image #" + " " + imageUrl + mImage.size());
                            } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                    mDate.add(xpp.nextText().substring(0, 17));
                                    //Log.d("Date", feed[i] + ": " + xpp.nextText().substring(0, 17));
                            }
                        } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                            if(mHeadlines.size() > mImage.size()){
                                Log.d("Feed", "headlines: " + mHeadlines.size() + " vs images: " +  mImage.size());
                                mImage.add(null);
                            }
                            if(mHeadlines.size() > mDate.size()){
                                mDate.add(" ");
                            }
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
    }

}
