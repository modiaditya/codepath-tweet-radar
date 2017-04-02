package com.aditya.tweetradar.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.fragments.FavoriteTimelineFragment;
import com.aditya.tweetradar.fragments.UserTweetTimelineFragment;
import com.aditya.tweetradar.models.User;

/**
 * Created by amodi on 4/2/17.
 */

public class ProfileViewPagerAdapter  extends FragmentPagerAdapter {

    FragmentManager fm;
    Context context;
    User user;

    public ProfileViewPagerAdapter(FragmentManager fm, Context context, User user) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.user = user;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                UserTweetTimelineFragment userTweetTimelineFragment = UserTweetTimelineFragment.newInstance(user);
                fragment = userTweetTimelineFragment;
                break;
            case 1:
                FavoriteTimelineFragment favoriteTimelineFragment = FavoriteTimelineFragment.newInstance(user);
                fragment = favoriteTimelineFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.tweets);
        } else {
            return context.getString(R.string.favroites);
        }
    }
}
