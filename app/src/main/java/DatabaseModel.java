import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
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

    private static final String TABLE_ARTICLES = "ARTICLES";
    private static final String KEY_ARTICLES_ID = "_ID";
    private static final String KEY_ARTICLES_RSS = "RSS_ID";
    private static final String KEY_ARTICLES_CONTENT = "CONTENT_ID";

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

        String CREATE_TABLE_RSS = "CREATE" + TABLE_RSS + "(" + KEY_RSS_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_RSS_TITLE + " TEXT, "
                + KEY_RSS_LINK + " Text, " + KEY_RSS_AVAILABLE + " BOOLEAN )";
        db.execSQL(CREATE_TABLE_RSS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createNewFeed(String title, String link){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RSS_TITLE, title);
        values.put(KEY_RSS_LINK, link);
        values.put(KEY_RSS_AVAILABLE, Boolean.TRUE);

        db.insert(TABLE_RSS, null, values);
        db.close();
    }

    public void editFeed(String oldTitle, String title, String link){
        SQLiteDatabase db = this.getWritableDatabase();
        String WHERE = KEY_RSS_TITLE + "=" + oldTitle;
        ContentValues values = new ContentValues();
        values.put(KEY_RSS_TITLE, title);
        db.update(TABLE_RSS, values, WHERE, null);
        db.close();
    }

    public String findLink(String title){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectLink = "Select " + KEY_RSS_LINK + " FROM " + TABLE_RSS +
                "WHERE " + KEY_RSS_TITLE + "=" + title;
        Cursor cursor = db.rawQuery(selectLink, null);
        String link = "";
        if(cursor.moveToFirst()){
            link = cursor.getString(0).toString();
        }
        db.close();
        return link;
    }

    public void setSelected(String value){
        SQLiteDatabase db = this.getWritableDatabase();
        String WHERE = KEY_RSS_AVAILABLE + "=" + Boolean.TRUE;
        ContentValues values = new ContentValues();
        values.put(KEY_RSS_AVAILABLE, Boolean.FALSE);
        db.update(TABLE_RSS, values, WHERE, null);
        db.close();
    }

    public void setAvailable(String value){
        SQLiteDatabase db = this.getWritableDatabase();
        String WHERE = KEY_RSS_AVAILABLE + "=" + Boolean.FALSE;
        ContentValues values = new ContentValues();
        values.put(KEY_RSS_AVAILABLE, Boolean.TRUE);
        db.update(TABLE_RSS, values, WHERE,null);
        db.close();
    }

    public ArrayList getAvailable(){
        ArrayList<String> list = new ArrayList<>();


        return list;
    }

    public ArrayList getSelected() {
        ArrayList<String> list = new ArrayList<>();


        return list;
    }

    public ArrayList getHeadlines() {
        ArrayList<String> list = new ArrayList<>();


        return list;
    }

    public ArrayList getLinks() {
        ArrayList<String> list = new ArrayList<>();


        return list;
    }

    public ArrayList getDescriptions() { return null; }

    public String[] getAllSelected() { return null; }




}
