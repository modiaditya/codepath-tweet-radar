package com.aditya.tweetradar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.fragments.UserListFragment;
import com.aditya.tweetradar.models.User;
import org.parceler.Parcels;

import static com.aditya.tweetradar.activities.TweetTimelineActivity.USER_EXTRA;

/**
 * Created by amodi on 4/2/17.
 */

public class UserListActivity extends AppCompatActivity {
    public static final String IS_FOLLOWING_EXTRA = "is_following";

    @BindView(R.id.toolbar) Toolbar toolbar;
    boolean isFollowing;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        isFollowing = intent.getBooleanExtra(IS_FOLLOWING_EXTRA, false);
        user = Parcels.unwrap(intent.getParcelableExtra(USER_EXTRA));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(isFollowing ? R.string.following : R.string.followers);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.userListContainer, UserListFragment.newInstance(user)).commit();

    }
}
