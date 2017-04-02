package com.aditya.tweetradar.fragments;

import com.aditya.tweetradar.TweetRadarApplication;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by amodi on 3/31/17.
 */

public class MentionTimelineFragment extends TweetListFragment {

    public static MentionTimelineFragment newInstance() {
        MentionTimelineFragment mentionTimelineFragment = new MentionTimelineFragment();
        return mentionTimelineFragment;
    }

    @Override
    public void fetchTweets(Long maxId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        TweetRadarApplication.getTwitterClient().getMentionsTimeline(maxId, jsonHttpResponseHandler);
    }
}
