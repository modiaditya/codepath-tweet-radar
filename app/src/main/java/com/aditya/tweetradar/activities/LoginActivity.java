package com.aditya.tweetradar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.client.TwitterClient;
import com.codepath.oauth.OAuthLoginActivity;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private Button button;

    @Override
    public void onLoginSuccess() {
        Intent i = new Intent(this, SplashActivity.class);
        startActivity(i);
    }

    @Override
    public void onLoginFailure(Exception e) {
        Log.e(TAG, "Failed to login", e);
        Toast.makeText(this, "Failed to login", Toast.LENGTH_LONG).show();
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
