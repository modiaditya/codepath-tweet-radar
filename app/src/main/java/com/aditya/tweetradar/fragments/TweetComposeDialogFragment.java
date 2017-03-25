package com.aditya.tweetradar.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.TweetRadarApplication;
import com.aditya.tweetradar.models.Tweet;
import com.aditya.tweetradar.models.User;
import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;
import org.parceler.Parcels;

/**
 * Created by amodi on 3/25/17.
 */

public class TweetComposeDialogFragment extends DialogFragment {
    public static final String TAG = TweetComposeDialogFragment.class.getSimpleName();
    public static final String USER_EXTRA = "user_extra";

    private static final int MAX_TWEET_CHARS = 140;

    @BindView(R.id.imageProfileImage) ImageView profileImage;
    @BindView(R.id.tvName) TextView name;
    @BindView(R.id.tvScreenName) TextView screenName;
    @BindView(R.id.tvTweetBody) EditText tweetBody;
    @BindView(R.id.tvCharacterCount) TextView characterCount;
    @BindView(R.id.buttonTweet) Button tweet;
    @BindView(R.id.close) ImageView close;

    private User user;
    TweetComposeDialogFragmentListener tweetComposeDialogFragmentListener;

    public interface TweetComposeDialogFragmentListener {
        void onTweet(Tweet tweet);
    }
    public static TweetComposeDialogFragment newInstance(User user) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER_EXTRA, Parcels.wrap(user));
        TweetComposeDialogFragment tweetComposeDialogFragment = new TweetComposeDialogFragment();
        tweetComposeDialogFragment.setArguments(bundle);
        return tweetComposeDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = Parcels.unwrap(getArguments().getParcelable(USER_EXTRA));
        }
        tweetComposeDialogFragmentListener = (TweetComposeDialogFragmentListener) getActivity();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_compose_tweet, container);
        ButterKnife.bind(this, v);

        name.setText(user.name);
        screenName.setText(user.screenName);
        Glide.with(getContext()).load(user.profileImageUrl).into(profileImage);
        setTweetBodyListener();
        setOnTweetButtonClickListener();
        tweet.setEnabled(false);
        tweet.setAlpha(0.5f);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return v;
    }

    private void setOnTweetButtonClickListener() {
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetRadarApplication.getTwitterClient().postTweet(tweetBody.getText().toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        Tweet tweetResponse = Tweet.fromJSON(response);
                        tweetComposeDialogFragmentListener.onTweet(tweetResponse);
                        dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, "Failed to post tweet", throwable);
                        Toast.makeText(getActivity(), "Failed to post tweet", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void setTweetBodyListener() {
        tweetBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // use onTextChanged instead
                setCharsRemaining(MAX_TWEET_CHARS - editable.toString().length());
            }
        });
    }

    private void setCharsRemaining(int count) {
        characterCount.setText(String.valueOf(count));
        if (count >= 0) {
            characterCount.setTextColor(Color.BLACK);
            tweet.setEnabled(true);
            tweet.setAlpha(1f);
        } else {
            characterCount.setTextColor(Color.RED);
            tweet.setEnabled(false);
            tweet.setAlpha(.5f);
        }
    }
}
