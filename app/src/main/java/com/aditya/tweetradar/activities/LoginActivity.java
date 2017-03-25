package com.aditya.tweetradar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.client.TwitterClient;
import com.codepath.oauth.OAuthLoginActivity;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private Button button;

    @Override
    public void onLoginSuccess() {
        Log.e(TAG, "Yayyy!");
        Intent i = new Intent(this, TweetTimelineActivity.class);
        startActivity(i);
//        getClient().getHomeTimeline(1, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                Log.e(TAG, "Yaasdadadadyyy!");
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Log.e(TAG, "Nay!");
//            }
//
//        });
    }

    @Override
    public void onLoginFailure(Exception e) {
        Log.e(TAG, "Oh noo", e);
    }

    // Method to be called to begin the authentication process
    // assuming user is not authenticated.
    // Typically used as an event listener for a button for the user to press.
    public void loginToRest() {
        getClient().connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button = (Button) findViewById(R.id.btnLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginToRest();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getClient().isAuthenticated();
    }
}
