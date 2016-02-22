package edu.cpsc4820.bhglove.simplenewsreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * DatabaseModel intended purpose is to consolidate the classes DataModel and Feed into one single
 * controller class. DatabaseModel uses a private model class to parse RSS Feeds and interact
 * directly with an internal database. This class creates, modifies, and deletes tables needed to
 * represent and populate the view classes. The method names are directly ported and named the same
 * for easy replacement of the other deprecated classes.
 * Created by Benjamin Glover on 2/17/2016.
 */
public class DatabaseModel extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "RSS_FEEDS";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_RSS = "RSS";
    private static final String KEY_RSS_ID = "_ID";
    private static final String KEY_RSS_TITLE = "TITLE";
    private static final String KEY_RSS_LINK = "LINK";
    private static final String KEY_RSS_AVAILABLE = "AVAILABLE";

    private static final String TABLE_RSS_CONTENT = "RSS_CONTENT";
    private static final String KEY_RSS_CONTENT_ID = "_ID";
    //KEY_RSS_ID
    //KEY_CONTENT_ID

    private static final String TABLE_CONTENT = "CONTENT";
    private static final String KEY_CONTENT_ID = "_ID";
    private static final String KEY_CONTENT_HEADLINE = "HEADLINE";
    private static final String KEY_CONTENT_PERMALINK = "PERMALINK";
    private static final String KEY_CONTENT_IMG_LINK = "IMAGE";
    private static final String KEY_CONTENT_DESCRIPTION = "DESCRIPTION";

    public DatabaseModel(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_RSS = "CREATE TABLE " + TABLE_RSS + "(" + KEY_RSS_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT , " + KEY_RSS_TITLE + " TEXT, "
                + KEY_RSS_LINK + " TEXT, " + KEY_RSS_AVAILABLE + " INTEGER DEFAULT 1)";
        db.execSQL(CREATE_TABLE_RSS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void createNewFeed(String title, String link){
        SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_RSS_TITLE, title);
            Log.d("Create", KEY_RSS_TITLE + " = " + title);
            values.put(KEY_RSS_LINK, link);
            Log.d("Create", KEY_RSS_LINK + " = " + link);
            values.put(KEY_RSS_AVAILABLE, 1);
            Log.d("Create", KEY_RSS_AVAILABLE + "='1'");

            db.insert(TABLE_RSS, null, values);

            db.close();

    }

    public void editFeed(String oldTitle, String title, String link){
        SQLiteDatabase db = this.getWritableDatabase();
        String WHERE = KEY_RSS_TITLE + "='" + oldTitle + "'";
        ContentValues values = new ContentValues();
        values.put(KEY_RSS_TITLE, title);
        values.put(KEY_RSS_LINK, link);
        db.update(TABLE_RSS, values, WHERE, null);
        db.close();
    }

    public String findLink(String title){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectLink = "Select " + KEY_RSS_LINK + " FROM " + TABLE_RSS +
                " WHERE " + KEY_RSS_TITLE + "='" + title + "'";
        Cursor cursor = db.rawQuery(selectLink, null);
        String link = "";
        if(cursor.moveToFirst()){
            link = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return link;
    }

    public void setSelected(String value){
        SQLiteDatabase db = this.getWritableDatabase();
        String WHERE = KEY_RSS_TITLE + "='" + value + "'";
        ContentValues values = new ContentValues();
        values.put(KEY_RSS_AVAILABLE, 0);
        db.update(TABLE_RSS, values, WHERE, null);
        db.close();
    }

    public void setAvailable(String value){
        SQLiteDatabase db = this.getWritableDatabase();
        String WHERE = KEY_RSS_TITLE + "='" + value + "'";
        ContentValues values = new ContentValues();
        values.put(KEY_RSS_AVAILABLE, 1);
        db.update(TABLE_RSS, values, WHERE, null);
        db.close();
    }

    public ArrayList getAvailable(){
        Log.d("GET", "Getting Available");
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_RSS_TITLE + " FROM " + TABLE_RSS
               + " WHERE " + KEY_RSS_AVAILABLE + "=" + "'1'";
        Cursor cursor = db.rawQuery(WHERE, null);

        if(cursor.moveToFirst()){
            do{
                if(!list.contains(cursor.getString(0)))
                  list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList getSelected() {
        Log.d("GET", "Getting Selected");

        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String WHERE  = "SELECT " + KEY_RSS_TITLE + " FROM " + TABLE_RSS
                + " WHERE " + KEY_RSS_AVAILABLE + "=" + "'0'";
        Cursor cursor = db.rawQuery(WHERE, null);

        if(cursor.moveToFirst()){
            do{
                if(!list.contains(cursor.getString(0)))
                  list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    private void addHeadlines() { }
    public ArrayList getHeadlines() {
        int index = 0;
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_CONTENT_HEADLINE + " FROM " + TABLE_CONTENT;
        Cursor cursor = db.rawQuery(WHERE, null);


        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(index));
                index++;
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
    private void addContentLinks() { }
    public ArrayList getLinks() {
        int index = 0;
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_CONTENT_PERMALINK + " FROM " + TABLE_CONTENT;
        Cursor cursor = db.rawQuery(WHERE, null);


        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(index));
                index++;
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    private void addContentDescriptions() { }
    public ArrayList getDescriptions() { int index = 0;
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_CONTENT_DESCRIPTION + " FROM " + TABLE_CONTENT;
        Cursor cursor = db.rawQuery(WHERE, null);


        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(index));
                index++;
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public String[] getAllSelected() {
        ArrayList<String> list = getSelected();

        String[] selected = new String[list.size()];
        for(int i = 0; i < list.size(); i++){
            selected[i] = findLink(list.get(i));
        }
        return selected;
    }
    private void getContentData(){

    }
    public ArrayAdapter createNewsFeedAdapter(Context context) {
        ArrayAdapter mArrayAdapter;

        getContentData();
        final ArrayList<String> headlines = getHeadlines();
        final ArrayList<String> descriptions = getDescriptions();

        mArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_2, android.R.id.text1, headlines) {

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
                TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
                /*YOUR CHOICE OF COLOR*/
                textView1.setTextColor(Color.BLUE);
                textView1.setText(headlines.get(position));
                textView2.setTextColor(Color.GRAY);
                textView2.setText(Html.fromHtml(descriptions.get(position).replaceAll("(<(/)img>)|(<img.+?>)", "")).toString().trim());

                return view;
            }
        };
        return mArrayAdapter;
    }
    /**
     * Private class used to Parse RSS Feeds
     * This class provides the model to parse a list of rss feed xml links and was modified from
     * previously used code to include the use of SQLite.
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
            if(mHeadlines == null)
              mHeadlines = (ArrayList) new ArrayList<String>();
            if(mLinks == null)
              mLinks = (ArrayList) new ArrayList<String>();
            if(mDescription == null)
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

        public ArrayList<String> getmHeadlines() { return mHeadlines; }
        public ArrayList<String> getmLinks() {
            return mLinks;
        }
        public ArrayList<String> getmDescription() {
            return mDescription;
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
    }


}
