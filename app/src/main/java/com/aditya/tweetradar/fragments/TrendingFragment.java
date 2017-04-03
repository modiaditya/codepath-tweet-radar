package com.aditya.tweetradar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.TweetRadarApplication;
import com.aditya.tweetradar.adapters.TrendingAdapter;
import com.aditya.tweetradar.models.Trending;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amodi on 4/2/17.
 */

public class TrendingFragment extends Fragment {

    private final static String TAG = TrendingFragment.class.getSimpleName();

    @BindView(R.id.rv_trending) RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;
    TrendingAdapter trendingAdapter;
    SearchTimelineFragment.OnSearchListener onSearchListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending_list, container, false);
        ButterKnife.bind(this, view);

        onSearchListener = (SearchTimelineFragment.OnSearchListener) getActivity();
        layoutManager = new LinearLayoutManager(getContext());
        trendingAdapter = new TrendingAdapter(getContext(), new ArrayList<Trending>());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                                                                getResources().getConfiguration().orientation);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(trendingAdapter);
        trendingAdapter.setOnSearchListener(onSearchListener);
        fetchTrendingInfo();
        return view;
    }

    private void fetchTrendingInfo() {
        TweetRadarApplication.getTwitterClient().getTrending(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    List<Trending> trendingList = Trending.fromJSONArray(response.getJSONObject(0).getJSONArray("trends"));
                    trendingAdapter.addTrendingItems(trendingList);
                    trendingAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Failed to get Trending data", throwable);
            }
        });
    }
}
