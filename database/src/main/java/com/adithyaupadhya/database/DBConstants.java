package com.adithyaupadhya.database;

import android.net.Uri;

/**
 * Created by adithya.upadhya on 20-02-2016.
 */
public interface DBConstants {
    //  DATABASE NAME
    String DB_NAME = "MovieTVCeleb.db";

    // AUTHORITY
    String AUTHORITY = "com.adithyaupadhya.database.contentprovider.DBContentProvider";

    //  PREFERENCE, GOOGLE SIGN IN ID, FACEBOOK SIGN IN ID:
    String USER_ID = "user_id";

    //  DATETIME COLUMN
    String DATETIME = "insert_date";
    String RELEASE_DATE = "release_date";

    //  MOVIE FAVORITES DATABASE
    String MOVIE_TABLE = "movie";
    String MOVIE_ID = "movie_id";
    String MOVIE_DETAILS = "movie_details";
    String CREATE_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " + MOVIE_TABLE + "(" +
            USER_ID + " VARCHAR(50), " +
            MOVIE_ID + " VARCHAR(20), " +
            MOVIE_DETAILS + " TEXT, " +
            RELEASE_DATE + " VARCHAR(15), " +
            DATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY(" + USER_ID + ", " + MOVIE_ID + "))";

    //  TV SERIES FAVORITES DATABASE
    String TVSERIES_TABLE = "tvseries";
    String TVSERIES_ID = "tvseries_id";
    String TVSERIES_DETAILS = "tvseries_details";
    String CREATE_TVSERIES_TABLE = "CREATE TABLE IF NOT EXISTS " + TVSERIES_TABLE + "(" +
            USER_ID + " VARCHAR(50), " +
            TVSERIES_ID + " VARCHAR(20), " +
            TVSERIES_DETAILS + " TEXT, " +
            DATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY(" + USER_ID + ", " + TVSERIES_ID + "))";

    //  CELEBRITY FAVORITES DATABASE
    String CELEBRITY_TABLE = "celebrities";
    String CELEBRITY_ID = "celebrity_id";
    String CELEBRITY_DETAILS = "celebrity_details";
    String CREATE_CELEBRITY_TABLE = "CREATE TABLE IF NOT EXISTS " + CELEBRITY_TABLE + "(" +
            USER_ID + " VARCHAR(50), " +
            CELEBRITY_ID + " VARCHAR(20), " +
            CELEBRITY_DETAILS + " TEXT, " +
            DATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY(" + USER_ID + ", " + CELEBRITY_ID + "))";

    //  TABLE URI'S
    Uri MOVIE_TABLE_URI = Uri.parse("content://" + AUTHORITY + "/" + MOVIE_TABLE);
    Uri TVSERIES_TABLE_URI = Uri.parse("content://" + AUTHORITY + "/" + TVSERIES_TABLE);
    Uri CELEBRITY_TABLE_URI = Uri.parse("content://" + AUTHORITY + "/" + CELEBRITY_TABLE);

    //  PREFERENCES CONSTANTS
    String USER_EMAIL = "user_email";
    String USER_PROFILE_PIC = "user_pic";
    String USER_NAME = "user_name";
    String GAME_HIGH_SCORE = "_high_score";
    String APP_FIRST_TIME_LAUNCHED = "first_launch_flag";
}
