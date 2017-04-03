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

import static com.aditya.tweetradar.fragments.TweetComposeDialogFragment.USER_EXTRA;

/**
 * Created by amodi on 4/2/17.
 */

public class FavoriteTimelineFragment extends TweetListFragment {

    public static FavoriteTimelineFragment newInstance(User user) {

        Bundle args = new Bundle();
        args.putParcelable(USER_EXTRA, Parcels.wrap(user));
        FavoriteTimelineFragment fragment = new FavoriteTimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void fetchTweets(Long maxId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        TweetRadarApplication.getTwitterClient().getFavoriteList(user.id, jsonHttpResponseHandler);
    }
}
