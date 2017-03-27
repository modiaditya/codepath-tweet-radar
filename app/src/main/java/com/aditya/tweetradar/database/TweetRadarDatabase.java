package com.aditya.tweetradar.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by amodi on 3/25/17.
 */

@Database(name = TweetRadarDatabase.NAME, version = TweetRadarDatabase.VERSION)
public class TweetRadarDatabase {

    public static final String NAME = "TweetRadarDatabase";
    public static final int VERSION = 2;
}
