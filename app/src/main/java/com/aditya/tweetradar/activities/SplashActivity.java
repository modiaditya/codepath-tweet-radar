package com.aditya.tweetradar.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.TweetRadarApplication;
import com.aditya.tweetradar.models.User;
import com.aditya.tweetradar.models.User_Table;
import com.aditya.tweetradar.persistence.TweetRadarSharedPreferences;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;
import org.parceler.Parcels;

/**
 * Created by amodi on 3/26/17.
 */

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    // splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // fetch logged in user
        Long memberId = TweetRadarSharedPreferences.getLoggedInUserId(this);
        User user = null;
        if (memberId != -1) {
            user = SQLite.select().from(User.class).where(User_Table.id.eq(memberId)).querySingle();
        }
        if (user != null) {
            startTweetTimelineActivity(user);
        } else {
            TweetRadarApplication.getTwitterClient().getUserInformation(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    User user = User.fromJSON(response);
                    TweetRadarSharedPreferences.saveLoggedInUserId(SplashActivity.this, user.id);
                    user.save();
                    startTweetTimelineActivity(user);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "Failed to get user information" + throwable);
                }
            });
        }
    }

    private void startTweetTimelineActivity(final User user) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, TweetTimelineActivity.class );
                i.putExtra(TweetTimelineActivity.USER_EXTRA, Parcels.wrap(user));
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
