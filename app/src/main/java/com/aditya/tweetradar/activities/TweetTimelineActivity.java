package com.aditya.tweetradar.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.TweetRadarApplication;
import com.aditya.tweetradar.adapters.TweetAdapter;
import com.aditya.tweetradar.client.TwitterClient;
import com.aditya.tweetradar.fragments.TweetComposeDialogFragment;
import com.aditya.tweetradar.listeners.EndlessRecyclerViewScrollListener;
import com.aditya.tweetradar.models.Tweet;
import com.aditya.tweetradar.models.Tweet_Table;
import com.aditya.tweetradar.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by amodi on 3/23/17.
 */

public class TweetTimelineActivity extends AppCompatActivity implements TweetComposeDialogFragment.TweetComposeDialogFragmentListener {
    private static String TAG = TweetTimelineActivity.class.getSimpleName();

    @BindView(R.id.rv_timeline) RecyclerView recyclerView;
    @BindView(R.id.srlTimeline) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fabTweetCompose) FloatingActionButton composeTweet;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;

    TweetAdapter tweetAdapter;
    RecyclerView.LayoutManager layoutManager;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    User loggedInUser;
    Snackbar noInternetSnackbar;

    @Override
    public void onTweet(Tweet tweet) {
        tweetAdapter.addTweet(0, tweet);
        tweetAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_timeline);
        ButterKnife.bind(this);
        //toolbar.setLogo(R.drawable.ic_twitter_social);
        toolbar.setTitle(R.string.app_name);
        this.layoutManager = new LinearLayoutManager(this);
        this.tweetAdapter = new TweetAdapter(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                                                                getResources().getConfiguration().orientation);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tweetAdapter);
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchTweets(tweetAdapter.getLastLoadedTweet().id);
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetAdapter.clear();
                tweetAdapter.notifyDataSetChanged();
                fetchTweets();
            }
        });
        fetchTweets();
        fetchLoggedInUser();
        recyclerViewScrollListener();
        composeTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetComposeDialogFragment.newInstance(loggedInUser).show(getSupportFragmentManager(), "compose");
            }
        });
    }

    private void fetchTweets(Long maxId) {
        if (!TwitterClient.isNetworkAvailable(this)) {
            showNoInternetSnackbar();
            if (tweetAdapter.getItemCount() == 0) {
                loadTweetsFromDatabase();
            }
            return;
        }
        TweetRadarApplication.getTwitterClient().getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                swipeRefreshLayout.setRefreshing(false);
                List<Tweet> tweets = Tweet.fromJSONArray(response);
                tweetAdapter.addTweets(tweets);
                tweetAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Failed to get data" + throwable);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void fetchLoggedInUser() {
        TweetRadarApplication.getTwitterClient().getUserInformation(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                loggedInUser = User.fromJSON(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Failed to get user information" + throwable);
            }
        });
    }

    private void fetchTweets() {
        fetchTweets(null);
    }

    private void showNoInternetSnackbar() {
        if (noInternetSnackbar == null) {
            noInternetSnackbar = Snackbar.make(coordinatorLayout, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE);
            noInternetSnackbar.show();
        }
    }

    private void loadTweetsFromDatabase() {
        tweetAdapter.addTweets(SQLite.select().from(Tweet.class).orderBy(Tweet_Table.id, false).queryList());
        tweetAdapter.notifyDataSetChanged();
    }

    private void recyclerViewScrollListener() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    composeTweet.show();
                } else if (dy > 0){
                    composeTweet.hide();
                }
            }
        });
    }



}
