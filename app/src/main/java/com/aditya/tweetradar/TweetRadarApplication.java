package com.aditya.tweetradar;

import android.app.Application;
import android.content.Context;
import com.aditya.tweetradar.client.TwitterClient;

/**
 * Created by amodi on 3/24/17.
 */

public class TweetRadarApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        TweetRadarApplication.context = this;
    }

    public static TwitterClient getTwitterClient() {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TweetRadarApplication.context);
    }
}
