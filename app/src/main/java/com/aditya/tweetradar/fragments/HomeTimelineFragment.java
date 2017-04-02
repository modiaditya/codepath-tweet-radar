package com.aditya.tweetradar.fragments;

import com.aditya.tweetradar.TweetRadarApplication;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by amodi on 3/31/17.
 */

public class HomeTimelineFragment extends TweetListFragment {

    public static HomeTimelineFragment newInstance() {
        return new HomeTimelineFragment();
    }

    @Override
    public void fetchTweets(Long maxId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        TweetRadarApplication.getTwitterClient().getHomeTimeline(maxId, jsonHttpResponseHandler);
    }
}
