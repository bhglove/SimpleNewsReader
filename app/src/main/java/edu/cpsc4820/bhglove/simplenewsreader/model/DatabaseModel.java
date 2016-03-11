package edu.cpsc4820.bhglove.simplenewsreader.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * DatabaseModel intended purpose is to act as model class for the classes DatabaseController and Feed.
 * This class creates, modifies, and deletes tables needed to represent and populate the view
 * classes. The method names are directly ported and named the same for easy retrieval by the
 * DatabaseController class.
 *
 * Created by Benjamin Glover on 2/17/2016.
 */
public class DatabaseModel extends SQLiteOpenHelper{
    /* Database Information */
    private static final String DATABASE_NAME = "SIMPLE_RSS_READER";
    private static final int DATABASE_VERSION = 2;

    /*RSS Feed Table Information*/
    private static final String TABLE_RSS = "RSS";
    private static final String KEY_RSS_ID = "RSS_ID";
    private static final String KEY_RSS_TITLE = "TITLE";
    private static final String KEY_RSS_LINK = "LINK";
    private static final String KEY_RSS_AVAILABLE = "AVAILABLE";

    /*Article Content Table Information*/
    private static final String TABLE_RSS_CONTENT = "RSS_CONTENT";
    private static final String KEY_RSS_CONTENT_ID = "RSS_CONTENT_ID";

    /*One RSS Feed to Multiple Content Table Information*/
    private static final String TABLE_CONTENT = "CONTENT";
    private static final String KEY_CONTENT_ID = "CONTENT_ID";
    private static final String KEY_CONTENT_HEADLINE = "HEADLINE";
    private static final String KEY_CONTENT_PERMALINK = "PERMALINK";
    private static final String KEY_CONTENT_IMG_LINK = "IMAGE";
    private static final String KEY_CONTENT_DESCRIPTION = "DESCRIPTION";
    /*Date Table Information*/
    private static final String TABLE_DATE = "DATE";
    private static final String KEY_DATE_ID = "DATE_ID";
    private static final String KEY_DATE_STRING = "DATE_STRING";

    /*Content Date Table Information*/
    private static final String TABLE_CONTENT_DATE = "CONTENT_DATE";
    private static final String KEY_CONTENT_DATE_ID = "CONTENT_DATE_ID";


    public DatabaseModel(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /*Creates the needed tables: Rss, Content, Date, RSS_Content, Content_Date*/
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_RSS = "CREATE TABLE IF NOT EXISTS " + TABLE_RSS + "(" + KEY_RSS_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, " + KEY_RSS_TITLE + " TEXT, "
                + KEY_RSS_LINK + " TEXT, " + KEY_RSS_AVAILABLE + " INTEGER DEFAULT 1)";
        db.execSQL(CREATE_TABLE_RSS);

        String CREATE_TABLE_CONTENT = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTENT + "(" + KEY_CONTENT_ID +
                 " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, " + KEY_CONTENT_HEADLINE + " TEXT, " +
                KEY_CONTENT_DESCRIPTION + " TEXT, " + KEY_CONTENT_PERMALINK + " TEXT, " +
                KEY_CONTENT_IMG_LINK + " TEXT)";
        db.execSQL(CREATE_TABLE_CONTENT);

        String CREATE_TABLE_RSS_CONTENT = "CREATE TABLE IF NOT EXISTS " + TABLE_RSS_CONTENT + "("
                + KEY_RSS_CONTENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, " + KEY_RSS_ID +
                " INTEGER, " + KEY_CONTENT_ID + " INTEGER )";
        db.execSQL(CREATE_TABLE_RSS_CONTENT);

        String CREATE_TABLE_DATE = "CREATE TABLE IF NOT EXISTS " + TABLE_DATE + "(" + KEY_DATE_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, " + KEY_DATE_STRING + " TEXT )";
        db.execSQL(CREATE_TABLE_DATE);

        String CREATE_TABLE_CONTENT_DATE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTENT_DATE
                + "(" + KEY_CONTENT_DATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, "
                + KEY_DATE_ID + " INTEGER, " +  KEY_CONTENT_ID + " INTEGER )";
        db.execSQL(CREATE_TABLE_CONTENT_DATE);
     }

    /**
     * Creates a new content entry for the tables Content, Date and creates an association between
     * the article and the rss feed.
     * @param feedLink - RSS Feed link
     * @param title - Headline
     * @param description - Description
     * @param link - Permalink
     * @param imageUrl - Image Url
     * @param date - Date Posted
     */
    public void createNewContent(String feedLink, String title, String description, String link, String imageUrl, String date){
        if(checkContentDuplicates(link) == 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            createNewDate(date);

            ContentValues values = new ContentValues();
            values.put(KEY_CONTENT_HEADLINE, title);
            values.put(KEY_CONTENT_DESCRIPTION, description);
            values.put(KEY_CONTENT_PERMALINK, link);
            values.put(KEY_CONTENT_IMG_LINK, imageUrl);
            db.insert(TABLE_CONTENT, null, values);

            createNewRssContent(getRssId(feedLink), getContentId(link));
            createNewContentDate(getDateId(date), getContentId(link));
        }
    }

    /**
     * Creates a new entry for the table RSS.
     *
     * @param title - Title of the RSS Feed
     * @param link - Xml Link of the RSS Feed
     */
    public void createNewFeed(String title, String link){
        if(checkRSSDuplicates(title, link) == 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_RSS_TITLE, title);
            values.put(KEY_RSS_LINK, link);
            values.put(KEY_RSS_AVAILABLE, 1);
            db.insert(TABLE_RSS, null, values);
        }
    }

    /**
     * Creates a new association between a RSS Feed and a specific article.
     * @param rssId - RSS ID
     * @param contentId - Content ID
     */
    private void createNewRssContent(int rssId, int contentId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RSS_ID, rssId);
        values.put(KEY_CONTENT_ID, contentId);
        db.insert(TABLE_RSS_CONTENT, null, values);
    }

    /**
     * Creates a new entry for the date of an article.
     * @param date - Date Posted
     */
    private void createNewDate(String date){
        if(checkDateDuplicates(date) == 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_DATE_STRING, date);
            db.insert(TABLE_DATE, null, values);
        }
    }

    /**
     * Creates an association between an article and the date it was posted.
     * @param dateId - Date Posted ID
     * @param contentId - Content ID
     */
    private void createNewContentDate(int dateId, int contentId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE_ID, dateId);
        values.put(KEY_CONTENT_ID, contentId);
        db.insert(TABLE_CONTENT_DATE, null, values);
    }

    /**
     * Checks the database for duplicate RSS Links. Is used before a new rss entry is made.
     * @param title - RSS Title
     * @param link - RSS Link
     * @return - Number of Duplicates
     */
    private int checkRSSDuplicates(String title, String link){
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT = "SELECT " + KEY_RSS_ID + " FROM " + TABLE_RSS + " WHERE "
                + KEY_RSS_TITLE + "='" + title + "' OR " + KEY_RSS_LINK + "='" + link + "'";

        Cursor cursor;
        cursor = db.rawQuery(SELECT, null);
        int count =  cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Checks the database for duplicate articles. Is used before a new article entry is made.
     * @param link - Permalink of an Article
     * @return - Number of Duplicates
     */
    private int checkContentDuplicates(String link){
        SQLiteDatabase db = getReadableDatabase();
        String SELECT = "SELECT * FROM " + TABLE_CONTENT + " WHERE "
                + KEY_CONTENT_PERMALINK + "=\"" + link + "\"";

        Cursor cursor = db.rawQuery(SELECT, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Checks the database for duplicate stored dates. Is used before a new article entry is made.
     * @param date - Date Posted
     * @return - Number of Duplicates
     */
    private int checkDateDuplicates(String date){
        SQLiteDatabase db = getReadableDatabase();
        String SELECT = "SELECT * FROM " + TABLE_DATE + " WHERE "
                + KEY_DATE_STRING + "=\"" + date + "\"";

        Cursor cursor = db.rawQuery(SELECT, null);
        int count = cursor.getCount();
        cursor.close();
        //db.close();
        return count;
    }

    /**
     * Returns the associated id of a given rss link.
     * @param link - RSS Feed Link
     * @return - The ID Number of the RSS Feed
     */
    public int getRssId(String link){
        int retVal;
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT =  "SELECT " + KEY_RSS_ID + " FROM " + TABLE_RSS + " WHERE "
                + KEY_RSS_LINK + "='" + link + "'";

        Cursor cursor = db.rawQuery(SELECT, null);
        cursor.moveToFirst();
        if(cursor.getCount() == 1)
            retVal = cursor.getInt(0);
        else
            retVal = -1;
        cursor.close();
        //db.close();
        return retVal;
    }

    /**
     * Returns the associated id of a given content link.
     * @param link - Content Permalink
     * @return - ID of an Article
     */
    public int getContentId(String link){
        int retVal;
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT =  "SELECT " + KEY_CONTENT_ID + " FROM " + TABLE_CONTENT + " WHERE "
                + KEY_CONTENT_PERMALINK + "=\"" + link + "\"";

        Cursor cursor = db.rawQuery(SELECT, null);
        cursor.moveToFirst();
        if(cursor.getCount() == 1)
            retVal = cursor.getInt(0);
        else
            retVal = -1;
        cursor.close();
        return retVal;
    }

    /**
     * Returns the associated headline of a given content link.
     * @param link - Content Permalink
     * @return - Headline of an Article
     */
    public String getKeyContentHeadline(String link){
        String retVal;
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT =  "SELECT " + KEY_CONTENT_HEADLINE + " FROM " + TABLE_CONTENT + " WHERE "
                + KEY_CONTENT_PERMALINK + "=\"" + link + "\"";

        Cursor cursor = db.rawQuery(SELECT, null);
        cursor.moveToFirst();
        if(cursor.getCount() == 1)
            retVal = cursor.getString(0);
        else
            retVal = " ";
        cursor.close();
        return retVal;
    }

    /**
     * Returns the associated description of a given content link.
     * @param link - Content Permalink
     * @return - Description of an Article
     */
    public String getKeyContentDescription(String link){
        String retVal;
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT =  "SELECT " + KEY_CONTENT_DESCRIPTION + " FROM " + TABLE_CONTENT + " WHERE "
                + KEY_CONTENT_PERMALINK + "=\"" + link + "\"";

        Cursor cursor = db.rawQuery(SELECT, null);
        cursor.moveToFirst();
        if(cursor.getCount() == 1)
            retVal = cursor.getString(0);
        else
            retVal = " ";
        cursor.close();
        return retVal;
    }

    /**
     * Returns the associated image url of a given content link.
     * @param link - Content Permalink
     * @return - Image url of an Article
     */
    public String getKeyContentImgLink(String link){
        String retVal;
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT =  "SELECT " + KEY_CONTENT_IMG_LINK + " FROM " + TABLE_CONTENT + " WHERE "
                + KEY_CONTENT_PERMALINK + "=\"" + link + "\"";

        Cursor cursor = db.rawQuery(SELECT, null);
        cursor.moveToFirst();
        if(cursor.getCount() == 1)
            retVal = cursor.getString(0);
        else
            retVal = " ";
        cursor.close();
        return retVal;
    }

    /**
     * Returns the associated image url of a given content link.
     * @param link - Content Permalink
     * @return - Image url of an Article
     */
    public int getKeyContentDateId(String link){
        int retVal;
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT =  "SELECT " + KEY_CONTENT_DATE_ID + " FROM " + TABLE_CONTENT + " WHERE "
                + KEY_CONTENT_PERMALINK + "=\"" + link + "\"";

        Cursor cursor = db.rawQuery(SELECT, null);
        cursor.moveToFirst();
        if(cursor.getCount() == 1)
            retVal = cursor.getInt(0);
        else
            retVal = -1;
        cursor.close();
        return retVal;
    }

    /**
     * Returns the id to the associated string of an article's posted date.
     * @param date Date Posted
     * @return ID associated with the Date
     */
    public int getDateId(String date){
        int retVal;
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT =  "SELECT " + KEY_DATE_ID + " FROM " + TABLE_DATE + " WHERE "
                + KEY_DATE_STRING + "='" + date + "'";

        Cursor cursor = db.rawQuery(SELECT, null);
        cursor.moveToFirst();
        if(cursor.getCount() == 1)
            retVal = cursor.getInt(0);
        else
            retVal = -1;
        cursor.close();
        return retVal;
    }

    /**
     * Updates the RSS Table to reflect changes to a given RSS Feed.
     * @param oldTitle Previous Title of the RSS Feed
     * @param title New RSS Feed Title
     * @param link New RSS Link
     */
    public void editFeed(String oldTitle, String title, String link){
        SQLiteDatabase db = this.getWritableDatabase();
        String WHERE = KEY_RSS_TITLE + "='" + oldTitle + "'";
        ContentValues values = new ContentValues();
        values.put(KEY_RSS_TITLE, title);
        values.put(KEY_RSS_LINK, link);
        db.update(TABLE_RSS, values, WHERE, null);
    }

    /**
     * Returns the associated link of a given RSS Feed based on it's title.
     * @param title RSS Feed Title
     * @return Returns the RSS Link
     */
    public String findRssLink(String title){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectLink = "Select " + KEY_RSS_LINK + " FROM " + TABLE_RSS +
                " WHERE " + KEY_RSS_TITLE + "='" + title + "'";
        Cursor cursor = db.rawQuery(selectLink, null);
        String link = "";
        if(cursor.moveToFirst()){
            link = cursor.getString(0);
        }
        cursor.close();
        //db.close();
        return link;
    }

    /**
     * Sets the specific RSS Feed as selected to download.
     * @param value Title of the RSS Feed
     */
    public void setSelected(String value){
        SQLiteDatabase db = this.getWritableDatabase();
        String WHERE = KEY_RSS_TITLE + "='" + value + "'";
        ContentValues values = new ContentValues();
        values.put(KEY_RSS_AVAILABLE, 0);
        db.update(TABLE_RSS, values, WHERE, null);
        //db.close();
    }

    /**
     * Unselected the specific RSS Feed from the queue to download.
     * @param value Title of the RSS Feed
     */
    public void setAvailable(String value){
        SQLiteDatabase db = this.getWritableDatabase();
        String WHERE = KEY_RSS_TITLE + "='" + value + "'";
        ContentValues values = new ContentValues();
        values.put(KEY_RSS_AVAILABLE, 1);
        db.update(TABLE_RSS, values, WHERE, null);
        //db.close();
    }

    /**
     * Returns the Headline of a specific article based on the Content's ID
     * @param contentId ID of the article
     * @return Title of the article
     */
    public String getContentRssTitle(int contentId){
        SQLiteDatabase db = this.getWritableDatabase();

        String SELECT = "SELECT " + TABLE_RSS + "." + KEY_RSS_TITLE + " FROM " +  TABLE_RSS +
                " INNER JOIN " + TABLE_RSS_CONTENT + " ON " + TABLE_RSS_CONTENT + "." + KEY_RSS_ID +
                "=" + TABLE_RSS + "." + KEY_RSS_ID +
                " INNER JOIN " + TABLE_CONTENT + " ON "
                + TABLE_RSS_CONTENT + "." + KEY_CONTENT_ID + "=" + TABLE_CONTENT + "." +
                KEY_CONTENT_ID + " WHERE " + TABLE_CONTENT + "." +
                KEY_CONTENT_ID + "='" + contentId + "'";
        String title = " ";

        Cursor cursor = db.rawQuery(SELECT, null);
        cursor.moveToFirst();
        if(cursor.getCount() == 1){
            title  = cursor.getString(0);
        }
        return title;
    }

    /**
     * Returns the date posted based on the article's content id.
     * @param contentID Content ID
     * @return Date String
     */
    public String getContentDate(int contentID){
        SQLiteDatabase db = this.getWritableDatabase();

        String SELECT = "SELECT " + TABLE_DATE + "." + KEY_DATE_STRING + " FROM " +  TABLE_DATE +
                " INNER JOIN " + TABLE_CONTENT_DATE + " ON " + TABLE_CONTENT_DATE + "." + KEY_DATE_ID +
                "=" + TABLE_DATE + "." + KEY_DATE_ID +
                " INNER JOIN " + TABLE_CONTENT + " ON "
                + TABLE_CONTENT_DATE + "." + KEY_CONTENT_ID + "=" + TABLE_CONTENT + "." +
                KEY_CONTENT_ID + " WHERE " + TABLE_CONTENT + "." +
                KEY_CONTENT_ID + "='" + contentID + "'";

        String title = " ";

        Cursor cursor = db.rawQuery(SELECT, null);
        cursor.moveToFirst();
        if(cursor.getCount() == 1) {
            title = cursor.getString(0);
        }
        return title;
    }

    /**
     * Returns a list of all rss feeds available to queue for download.
     * @return Arraylist of Available RSS Feeds
     */
    public ArrayList getAvailable(){
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

        return list;
    }

    /**
     * Returns a list of all rss feeds in queue for download.
     * @return Arraylist of Selected RSS Feeds
     */
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

        return list;
    }

    /**
     * Returns all downloaded headlines stored in the database.
     * @return ArrayList of downloaded headlines.
     */
    public ArrayList getHeadlines() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_CONTENT_HEADLINE + " FROM " + TABLE_CONTENT;
        Cursor cursor = db.rawQuery(WHERE, null);
        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();

        return list;
    }

    /**
     * Returns all downloaded links stored in the database.
     * @return ArrayList of downloaded links.
     */
    public ArrayList getLinks() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_CONTENT_PERMALINK + " FROM " + TABLE_CONTENT;
        Cursor cursor = db.rawQuery(WHERE, null);


        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }


    /**
     * Returns all downloaded descriptions stored in the database.
     * @return ArrayList of download descriptions.
     */
    public ArrayList getDescriptions() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_CONTENT_DESCRIPTION + " FROM " + TABLE_CONTENT;
        Cursor cursor = db.rawQuery(WHERE, null);

        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    /**
     * Returns all downloaded image urls stored in the database.
     * @return ArrayList of all downloaded image urls.
     */
    public ArrayList getImages() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_CONTENT_IMG_LINK + " FROM " + TABLE_CONTENT;
        Cursor cursor = db.rawQuery(WHERE, null);

        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    /**
     * Returns all Selected RSS Feed links for download
     * @return ArrayList of enqueued links for download
     */
    public String[] getAllSelected() {
        ArrayList<String> list = getSelected();

        String[] selected = new String[list.size()];
        for(int i = 0; i < list.size(); i++){
            selected[i] = findRssLink(list.get(i));
        }
        return selected;
    }

    /**
     * Upgrades the database when a change is made.
     * @param db - Database
     * @param oldVersion Previous Version number
     * @param newVersion Current Version Number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DELETE0 = "DROP TABLE IF EXISTS " + TABLE_RSS;
        String DELETE1 = "DROP TABLE IF EXISTS " + TABLE_RSS_CONTENT;
        String DELETE2 = "DROP TABLE IF EXISTS " + TABLE_CONTENT;
        String DELETE3 = "DROP TABLE IF EXISTS " + TABLE_DATE;

        db.execSQL(DELETE0);
        db.execSQL(DELETE1);
        db.execSQL(DELETE2);
        db.execSQL(DELETE3);
        onCreate(db);
    }

    /**
     * Manually removes all content from the the tables: Content, Date, Content_Date to remove
     * any instance of duplicates and allows for new content to be downloaded.
     */
    public void refreshContent(){
        SQLiteDatabase db = this.getWritableDatabase();
        String DELETE1 = "DROP TABLE IF EXISTS " + TABLE_RSS_CONTENT;
        String DELETE2 = "DROP TABLE IF EXISTS " + TABLE_CONTENT;
        String DELETE3 = "DROP TABLE IF EXISTS " + TABLE_DATE;
        String DELETE4 = "DROP TABLE IF EXISTS " + TABLE_CONTENT_DATE;

        db.execSQL(DELETE1);
        db.execSQL(DELETE2);
        db.execSQL(DELETE3);
        db.execSQL(DELETE4);
        onCreate(db);
    }
}
