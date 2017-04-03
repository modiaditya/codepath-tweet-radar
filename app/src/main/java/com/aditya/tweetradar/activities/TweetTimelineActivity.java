package com.aditya.tweetradar.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.TweetRadarApplication;
import com.aditya.tweetradar.adapters.FragmentViewPagerAdapter;
import com.aditya.tweetradar.adapters.TweetAdapter;
import com.aditya.tweetradar.fragments.SearchTimelineFragment;
import com.aditya.tweetradar.fragments.TweetComposeDialogFragment;
import com.aditya.tweetradar.fragments.TweetListFragment;
import com.aditya.tweetradar.models.Tweet;
import com.aditya.tweetradar.models.User;
import com.aditya.tweetradar.persistence.TweetRadarSharedPreferences;
import com.aditya.tweetradar.receivers.NetworkStateReceiver;
import org.parceler.Parcels;

import static com.aditya.tweetradar.activities.SearchActivity.SEARCH_QUERY_EXTRA;

/**
 * Created by amodi on 3/23/17.
 */

public class TweetTimelineActivity extends AppCompatActivity
    implements TweetComposeDialogFragment.TweetComposeDialogFragmentListener,
    NetworkStateReceiver.NetworkStateReceiverListener, TweetAdapter.OnUserClickListener, SearchTimelineFragment.OnSearchListener
     {
    private static final String TAG = TweetTimelineActivity.class.getSimpleName();

    public static final String USER_EXTRA = "user_extra";

    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.fabTweetCompose) FloatingActionButton composeTweet;

    NetworkStateReceiver networkStateReceiver;
    Snackbar noInternetSnackbar;
    User loggedInUser;
    FragmentViewPagerAdapter fragmentViewPagerAdapter;

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
    public void onUserClick(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(USER_EXTRA, Parcels.wrap(user));
        startActivity(intent);
    }

    @Override
    public void onTweet(Tweet tweet) {
        TweetListFragment fragment = (TweetListFragment) (fragmentViewPagerAdapter.registeredFragments.get(0));
        fragment.onTweet(tweet);
    }

    @Override
    public void onSearch(String query) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SEARCH_QUERY_EXTRA, query);
        startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_timeline);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        loggedInUser = Parcels.unwrap(intent.getParcelableExtra(USER_EXTRA));
        setupToolbarProperties();

        fragmentViewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), this, loggedInUser);
        viewPager.setAdapter(fragmentViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        networkStateReceiver = new NetworkStateReceiver();
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        networkStateReceiver.addListener(new NetworkStateReceiver.NetworkStateReceiverListener() {
            @Override
            public void networkAvailable() {
                hideNoInternetSnackbar();
            }

            @Override
            public void networkUnavailable() {
                showNoInternetSnackbar();
            }
        });

        // handling implicit intent
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

        setupFAB();

    }

    private void setupToolbarProperties() {
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.tweet_timeline_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
    }

    private void setupFAB() {
        composeTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetComposeDialogFragment fragment = TweetComposeDialogFragment.newInstance(loggedInUser,
                                                                                             null,
                                                                                             null);
                fragment.setTweetComposeDialogFragmentListener(TweetTimelineActivity.this);
                fragment.show(getSupportFragmentManager(), "compose");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
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
            return true;
        }
        if (id == R.id.miSearch) {
            Intent intent = new Intent(TweetTimelineActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(networkStateReceiver);
        super.onDestroy();
    }


    public void showNoInternetSnackbar() {
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





}
