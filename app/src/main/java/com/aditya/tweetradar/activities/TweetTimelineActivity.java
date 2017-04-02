package com.aditya.tweetradar.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.TweetRadarApplication;
import com.aditya.tweetradar.adapters.FragmentViewPagerAdapter;
import com.aditya.tweetradar.fragments.TweetComposeDialogFragment;
import com.aditya.tweetradar.models.User;
import com.aditya.tweetradar.persistence.TweetRadarSharedPreferences;
import com.aditya.tweetradar.receivers.NetworkStateReceiver;
import org.parceler.Parcels;

/**
 * Created by amodi on 3/23/17.
 */

public class TweetTimelineActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {
    private static final String TAG = TweetTimelineActivity.class.getSimpleName();

    public static final String USER_EXTRA = "user_extra";

    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_timeline);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        loggedInUser = Parcels.unwrap(intent.getParcelableExtra(USER_EXTRA));
        toolbar.setTitle(R.string.app_name);
        fragmentViewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), this, loggedInUser);
        viewPager.setAdapter(fragmentViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

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

//    private void fetchLoggedInUser() {
//        TweetRadarApplication.getTwitterClient().getUserInformation(new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                loggedInUser = User.fromJSON(response);
//                TweetRadarSharedPreferences.saveLoggedInUserId(TweetTimelineActivity.this, loggedInUser.id);
//                loggedInUser.save();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.e(TAG, "Failed to get user information" + throwable);
//            }
//        });
//    }



    @Override
    protected void onDestroy() {
        this.unregisterReceiver(networkStateReceiver);
        super.onDestroy();
    }


    private void showNoInternetSnackbar() {
        if (noInternetSnackbar == null) {
            //noInternetSnackbar = Snackbar.make(coordinatorLayout, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE);
            noInternetSnackbar.show();
        }
    }

    private void hideNoInternetSnackbar() {
        if (noInternetSnackbar != null) {
            noInternetSnackbar.dismiss();
        }
    }





}
