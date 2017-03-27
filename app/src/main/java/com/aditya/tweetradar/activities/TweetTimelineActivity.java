package com.aditya.tweetradar.activities;

import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Menu;
import android.view.MenuItem;
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
import com.aditya.tweetradar.persistence.TweetRadarSharedPreferences;
import com.aditya.tweetradar.receivers.NetworkStateReceiver;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by amodi on 3/23/17.
 */

public class TweetTimelineActivity extends AppCompatActivity
    implements TweetComposeDialogFragment.TweetComposeDialogFragmentListener,
    NetworkStateReceiver.NetworkStateReceiverListener {
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
    NetworkStateReceiver networkStateReceiver;

    @Override
    public void onTweet(Tweet tweet) {
        tweetAdapter.addTweet(0, tweet);
        tweetAdapter.notifyDataSetChanged();
    }

    @Override
    public void networkAvailable() {
        if (noInternetSnackbar != null) {
            noInternetSnackbar.dismiss();
        }
    }

    @Override
    public void networkUnavailable() {
        showNoInternetSnackbar();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_timeline);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
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
                if (totalItemsCount <= 5) {
                    return;
                }
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
                TweetComposeDialogFragment.newInstance(loggedInUser, null, null)
                                          .show(getSupportFragmentManager(), "compose");
            }
        });

        // handling implicit intent
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                // Make sure to check whether returned data will be null.
                String titleOfPage = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);
                String prefilledTweetBody = urlOfPage + "\n" + titleOfPage;
                TweetComposeDialogFragment.newInstance(TweetRadarSharedPreferences.getLoggedInUserId(this),
                                                       null,
                                                       prefilledTweetBody)
                                          .show(getSupportFragmentManager(), "compose");
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tweet_timeline_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miLogout) {
            TweetRadarApplication.getTwitterClient().clearAccessToken();
            Intent intent = new Intent(TweetTimelineActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return false;
    }

    private void fetchTweets(Long maxId) {
        if (!TwitterClient.isNetworkAvailable(this)) {
            showNoInternetSnackbar();
            if (tweetAdapter.getItemCount() <= 2) {
                loadTweetsFromDatabase();
            }
            return;
        }
        TweetRadarApplication.getTwitterClient().getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                hideNoInternetSnackbar();
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
                tweetAdapter.setLoggedInUser(loggedInUser);
                TweetRadarSharedPreferences.saveLoggedInUserId(TweetTimelineActivity.this, loggedInUser.id);
                loggedInUser.save();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Failed to get user information" + throwable);
            }
        });
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(networkStateReceiver);
        super.onDestroy();
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

    private void hideNoInternetSnackbar() {
        if (noInternetSnackbar != null) {
            noInternetSnackbar.dismiss();
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
