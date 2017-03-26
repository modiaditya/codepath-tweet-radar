package com.aditya.tweetradar.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.TweetRadarApplication;

/**
 * Created by amodi on 3/26/17.
 */

public class SplashActivity extends AppCompatActivity {
    // splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // this method will be executed once the timer is over
                // start the app main activity
                boolean isUserAuthenticated = TweetRadarApplication.getTwitterClient().isAuthenticated();
                Intent i = new Intent(SplashActivity.this, isUserAuthenticated ?
                                                           TweetTimelineActivity.class : LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
