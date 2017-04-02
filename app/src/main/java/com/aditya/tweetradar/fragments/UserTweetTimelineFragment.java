package com.aditya.tweetradar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aditya.tweetradar.TweetRadarApplication;
import com.aditya.tweetradar.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.parceler.Parcels;

import static com.aditya.tweetradar.activities.TweetTimelineActivity.USER_EXTRA;

/**
 * Created by amodi on 4/1/17.
 */

public class UserTweetTimelineFragment extends TweetListFragment {

    public static UserTweetTimelineFragment newInstance(User user) {

        Bundle args = new Bundle();
        UserTweetTimelineFragment fragment = new UserTweetTimelineFragment();
        args.putParcelable(USER_EXTRA, Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        composeTweet.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void fetchTweets(Long maxId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        TweetRadarApplication.getTwitterClient().getUserTimeline(maxId, user.id, jsonHttpResponseHandler);
    }
}
