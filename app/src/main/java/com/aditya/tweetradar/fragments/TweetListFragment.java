package com.aditya.tweetradar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.aditya.tweetradar.activities.TweetTimelineActivity;
import com.aditya.tweetradar.adapters.TweetAdapter;
import com.aditya.tweetradar.listeners.EndlessRecyclerViewScrollListener;
import com.aditya.tweetradar.models.Tweet;
import com.aditya.tweetradar.models.Tweet_Table;
import com.aditya.tweetradar.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

/**
 * Created by amodi on 3/28/17.
 */

public abstract class TweetListFragment extends Fragment
    implements TweetComposeDialogFragment.TweetComposeDialogFragmentListener{
    private static String TAG = TweetListFragment.class.getSimpleName();

    @BindView(R.id.rv_timeline) RecyclerView recyclerView;
    @BindView(R.id.srlTimeline) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fabTweetCompose) @Nullable FloatingActionButton composeTweet;

    TweetAdapter tweetAdapter;
    RecyclerView.LayoutManager layoutManager;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    User user;
    TweetAdapter.OnUserClickListener onUserClickListener;

    @Override
    public void onTweet(Tweet tweet) {
        tweetAdapter.addTweet(0, tweet);
        tweetAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null && getArguments().getParcelable(TweetTimelineActivity.USER_EXTRA) != null) {
            user = Parcels.unwrap(getArguments().getParcelable(TweetTimelineActivity.USER_EXTRA));
        }



        //toolbar.setLogo(R.drawable.ic_twitter_social);

        this.layoutManager = new LinearLayoutManager(getContext());
        this.tweetAdapter = new TweetAdapter(getContext());
        this.onUserClickListener = (TweetAdapter.OnUserClickListener) getActivity();
        this.tweetAdapter.setOnUserClickListener(this.onUserClickListener);
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
        composeTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetComposeDialogFragment fragment = TweetComposeDialogFragment.newInstance(user,
                                                                                             null,
                                                                                             null);
                fragment.setTweetComposeDialogFragmentListener(TweetListFragment.this);
                fragment.show(getActivity().getSupportFragmentManager(), "compose");
            }
        });

//        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        // Replace the contents of the container with the new fragment
//        ft.replace(R.id.fragment_container, HomeTimelineFragment.newInstance());
//        // or ft.add(R.id.your_placeholder, new FooFragment());
//        // Complete the changes added above
//        ft.commit();
        fetchTweets();
        //recyclerViewScrollListener();
        return view;
    }

    private void fetchTweets(Long maxId) {
//        if (!TwitterClient.isNetworkAvailable(getContext())) {
//            //showNoInternetSnackbar();
//            if (tweetAdapter.getItemCount() <= 2) {
//                loadTweetsFromDatabase();
//            }
//            return;
//        }
        fetchTweets(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //hideNoInternetSnackbar();
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

    public abstract void fetchTweets(Long maxId, JsonHttpResponseHandler jsonHttpResponseHandler);


    protected void fetchTweets() {
        fetchTweets(null);
    }


    private void loadTweetsFromDatabase() {
        tweetAdapter.addTweets(SQLite.select().from(Tweet.class).orderBy(Tweet_Table.id, false).queryList());
        tweetAdapter.notifyDataSetChanged();
    }


    //    private void recyclerViewScrollListener() {
//        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy < 0) {
//                    composeTweet.show();
//                } else if (dy > 0){
//                    composeTweet.hide();
//                }
//            }
//        });
//    }
}
