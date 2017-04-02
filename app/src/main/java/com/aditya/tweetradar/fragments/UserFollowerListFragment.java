package com.aditya.tweetradar.fragments;

import android.os.Bundle;
import com.aditya.tweetradar.TweetRadarApplication;
import com.aditya.tweetradar.models.User;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.parceler.Parcels;

import static com.aditya.tweetradar.activities.TweetTimelineActivity.USER_EXTRA;

/**
 * Created by amodi on 4/2/17.
 */

public class UserFollowerListFragment extends BaseUserListFragment {

    public static UserFollowerListFragment newInstance(User user) {

        Bundle args = new Bundle();
        args.putParcelable(USER_EXTRA, Parcels.wrap(user));
        UserFollowerListFragment fragment = new UserFollowerListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    void fetchFollowList(AsyncHttpResponseHandler responseHandler) {
        TweetRadarApplication.getTwitterClient().getFollowersList(user.id, nextCursor, responseHandler);
    }
}
