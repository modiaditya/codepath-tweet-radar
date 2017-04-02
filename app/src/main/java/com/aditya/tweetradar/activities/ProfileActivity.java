package com.aditya.tweetradar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.adapters.ProfileViewPagerAdapter;
import com.aditya.tweetradar.adapters.TweetAdapter;
import com.aditya.tweetradar.fragments.ProfileHeaderFragment;
import com.aditya.tweetradar.models.User;
import org.parceler.Parcels;

import static com.aditya.tweetradar.activities.TweetTimelineActivity.USER_EXTRA;
import static com.aditya.tweetradar.activities.UserListActivity.IS_FOLLOWING_EXTRA;

/**
 * Created by amodi on 4/1/17.
 */

public class ProfileActivity extends AppCompatActivity implements TweetAdapter.OnUserClickListener,
ProfileHeaderFragment.OnFollowingInfoClickedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;

    User user;
    ProfileViewPagerAdapter profileViewPagerAdapter;

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(USER_EXTRA, Parcels.wrap(user));
        startActivity(intent);
    }

    @Override
    public void onFollowersClicked(User user) {
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra(IS_FOLLOWING_EXTRA, false);
        intent.putExtra(USER_EXTRA, Parcels.wrap(user));
        startActivity(intent);
    }

    @Override
    public void onFollowingClicked(User user) {
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra(IS_FOLLOWING_EXTRA, true);
        intent.putExtra(USER_EXTRA, Parcels.wrap(user));
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        user = Parcels.unwrap(intent.getParcelableExtra(USER_EXTRA));
        getSupportActionBar().setTitle(user.name);
//        final Drawable upArrow = getResources().getDrawable(android.R.drawable);
//        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        //getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileViewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), this, user);
        viewPager.setAdapter(profileViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        ProfileHeaderFragment profileHeaderFragment = ProfileHeaderFragment.newInstance(user);
        //UserTweetTimelineFragment userTweetTimelineFragment = UserTweetTimelineFragment.newInstance(user);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
          .replace(R.id.profileHeaderContainer, profileHeaderFragment)
          //.replace(R.id.profileTweetContainer, userTweetTimelineFragment)
          .commit();

    }
}
