package com.aditya.tweetradar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aditya.tweetradar.TweetRadarApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by amodi on 4/2/17.
 */

public class SearchTimelineFragment extends TweetListFragment {
    private static final String TAG = SearchTimelineFragment.class.getSimpleName();
    private static final String SEARCH_QUERY_EXTRA = "search_query_extra";
    private String searchQuery;

    public interface OnSearchListener {
        void onSearch(String query);
    }

    public static SearchTimelineFragment newInstance(String searchQuery) {

        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY_EXTRA, searchQuery);
        SearchTimelineFragment fragment = new SearchTimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchQuery = getArguments().getString(SEARCH_QUERY_EXTRA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    public void fetchTweets(Long maxId, final JsonHttpResponseHandler jsonHttpResponseHandler) {
        TweetRadarApplication.getTwitterClient().search(maxId, searchQuery, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    jsonHttpResponseHandler.onSuccess(statusCode, headers, response.getJSONArray("statuses"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Failed to fetch search query results", throwable);
            }
        });
    }
}
