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
 * DatabaseModel intended purpose is to act as model class for the classes DataModel and Feed.
 * DatabaseModel uses a private DataModel class to parse RSS Feeds and interact
 * directly with an internal database. This class creates, modifies, and deletes tables needed to
 * represent and populate the view classes. The method names are directly ported and named the same
 * for easy retrieval by the DataModel class.
 * Created by Benjamin Glover on 2/17/2016.
 */
public class DatabaseModel extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "RSS_FEEDS";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_RSS = "RSS";
    private static final String KEY_RSS_ID = "RSS_ID";
    private static final String KEY_RSS_TITLE = "TITLE";
    private static final String KEY_RSS_LINK = "LINK";
    private static final String KEY_RSS_AVAILABLE = "AVAILABLE";

    private static final String TABLE_RSS_CONTENT = "RSS_CONTENT";
    private static final String KEY_RSS_CONTENT_ID = "RSS_CONTENT_ID";
    //KEY_RSS_ID
    //KEY_CONTENT_ID

    private static final String TABLE_CONTENT = "CONTENT";
    private static final String KEY_CONTENT_ID = "CONTENT_ID";
    private static final String KEY_CONTENT_HEADLINE = "HEADLINE";
    private static final String KEY_CONTENT_PERMALINK = "PERMALINK";
    private static final String KEY_CONTENT_IMG_LINK = "IMAGE";
    private static final String KEY_CONTENT_DESCRIPTION = "DESCRIPTION";

    public DatabaseModel(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private int checkRSSDuplicates(String title, String link){
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT = "SELECT " + KEY_RSS_ID + " FROM " + TABLE_RSS + " WHERE "
                + KEY_RSS_TITLE + "='" + title + "' OR " + KEY_RSS_LINK + "='" + link + "'";

        Cursor cursor;
        cursor = db.rawQuery(SELECT, null);
        int count =  cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

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
        db.close();
        return retVal;
    }

    public void refreshContent(){
        SQLiteDatabase db = this.getWritableDatabase();
        String DELETE = "DROP TABLE IF EXISTS " + TABLE_RSS_CONTENT;
        String DELETE2 = "DROP TABLE IF EXISTS " + TABLE_CONTENT;
        db.execSQL(DELETE);
        db.execSQL(DELETE2);

        String CREATE_TABLE_CONTENT = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTENT + "(" + KEY_CONTENT_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, " + KEY_CONTENT_HEADLINE + " TEXT, " +
                KEY_CONTENT_DESCRIPTION + " TEXT, " + KEY_CONTENT_PERMALINK + " TEXT, " +
                KEY_CONTENT_IMG_LINK + " TEXT)";
        db.execSQL(CREATE_TABLE_CONTENT);

        String CREATE_TABLE_RSS_CONTENT = "CREATE TABLE IF NOT EXISTS " + TABLE_RSS_CONTENT + "("
                + KEY_RSS_CONTENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, " + KEY_RSS_ID +
                " INTEGER, " + KEY_CONTENT_ID + " INTEGER )";
        db.execSQL(CREATE_TABLE_RSS_CONTENT);

        db.close();
    }

    public void createNewFeed(String title, String link){
        if(checkRSSDuplicates(title, link) == 0) {
            long value;
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_RSS_TITLE, title);
            Log.d("Create", KEY_RSS_TITLE + " = " + title);
            values.put(KEY_RSS_LINK, link);
            Log.d("Create", KEY_RSS_LINK + " = " + link);
            values.put(KEY_RSS_AVAILABLE, 1);
            Log.d("Create", KEY_RSS_AVAILABLE + "='1'");

            Log.d("Duplicates", title + " " + checkRSSDuplicates(title, link));
            value = db.insertWithOnConflict(TABLE_RSS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
            Log.d("Values", title + " " + value);
        }

        Log.d("Value", title + " is id: " + getRssId(title));
    }

    private int checkContentDuplicate(String title, String link){
        SQLiteDatabase db = getReadableDatabase();
        String SELECT = "SELECT * FROM " + TABLE_CONTENT + " WHERE " + KEY_CONTENT_HEADLINE + "=\""
                + title + "\" OR "
                + KEY_CONTENT_PERMALINK + "=\"" + link + "\"";
        Cursor cursor = db.rawQuery(SELECT, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public int getContentId(String title, String link){
        int retVal;
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT =  "SELECT " + KEY_RSS_ID + " FROM " + TABLE_CONTENT + " WHERE "
                + KEY_CONTENT_HEADLINE + "=\"" + title + "\" AND " + KEY_CONTENT_PERMALINK + "=\""
        + link + "\"";

        Cursor cursor = db.rawQuery(SELECT, null);
        cursor.moveToFirst();
        if(cursor.getCount() == 1)
            retVal = cursor.getInt(0);
        else
            retVal = -1;
        cursor.close();
        return retVal;
    }

    public void createNewContent(String feedLink, String title, String description, String link, String imageUrl){
        if(checkContentDuplicate(title,  link) == 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_CONTENT_HEADLINE, title);
            values.put(KEY_CONTENT_DESCRIPTION, description);
            values.put(KEY_CONTENT_PERMALINK, link);
            values.put(KEY_CONTENT_IMG_LINK, imageUrl);
            int id = (int) db.insert(TABLE_CONTENT, null, values);
            db.close();

            createNewRssContent(getRssId(feedLink), id);
        }
    }

    public void createNewRssContent(int rssId, int contentId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RSS_ID, rssId);
        values.put(KEY_CONTENT_ID, contentId);
        db.insert(TABLE_RSS_CONTENT, null, values);
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


    public ArrayList getHeadlines() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_CONTENT_HEADLINE + " FROM " + TABLE_CONTENT +
                " Limit 0, 15";
        Cursor cursor = db.rawQuery(WHERE, null);


        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList getLinks() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_CONTENT_PERMALINK + " FROM " + TABLE_CONTENT+
                " Limit 0, 15";
        Cursor cursor = db.rawQuery(WHERE, null);


        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }


    public ArrayList getDescriptions() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_CONTENT_DESCRIPTION + " FROM " + TABLE_CONTENT +
                " Limit 0, 15";
        Cursor cursor = db.rawQuery(WHERE, null);


        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public ArrayList getImages() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE  = "SELECT " + KEY_CONTENT_IMG_LINK + " FROM " + TABLE_CONTENT +
                " Limit 0, 15";;
        Cursor cursor = db.rawQuery(WHERE, null);


        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(0));
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
}
