package com.adithyaupadhya.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adithya.upadhya on 20-02-2016.
 */
public class AppDatabaseHelper extends SQLiteOpenHelper {
    private final static int DB_VERSION = 1;

    public AppDatabaseHelper(Context context) {
        super(context, DBConstants.DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstants.CREATE_MOVIE_TABLE);
        db.execSQL(DBConstants.CREATE_TVSERIES_TABLE);
        db.execSQL(DBConstants.CREATE_CELEBRITY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.MOVIE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TVSERIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.CELEBRITY_TABLE);
        onCreate(db);
    }
}
