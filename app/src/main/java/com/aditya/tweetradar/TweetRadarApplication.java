package com.aditya.tweetradar;

import android.app.Application;
import android.content.Context;
import com.aditya.tweetradar.client.TwitterClient;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by amodi on 3/24/17.
 */

public class TweetRadarApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        TweetRadarApplication.context = this;
        FlowManager.init(new FlowConfig.Builder(this).build());
    }

    public static TwitterClient getTwitterClient() {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TweetRadarApplication.context);
    }
}
