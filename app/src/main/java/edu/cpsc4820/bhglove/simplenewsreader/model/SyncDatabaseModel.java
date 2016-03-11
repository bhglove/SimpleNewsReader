package edu.cpsc4820.bhglove.simplenewsreader.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Benjamin on 3/10/2016.
 */
public class SyncDatabaseModel extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EXTERNAL_SIMPLE_RSS_READER";
    private static final int DATABASE_VERSION = 1;






    public SyncDatabaseModel(Context context) {
        super(context, "Name", null, 0);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
