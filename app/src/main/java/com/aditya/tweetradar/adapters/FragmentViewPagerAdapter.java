package com.aditya.tweetradar.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.fragments.HomeTimelineFragment;
import com.aditya.tweetradar.fragments.MentionTimelineFragment;
import com.aditya.tweetradar.models.User;
import org.parceler.Parcels;

import static com.aditya.tweetradar.activities.TweetTimelineActivity.USER_EXTRA;

/**
 * Created by amodi on 3/31/17.
 */

public class FragmentViewPagerAdapter extends FragmentPagerAdapter {

    Context context;
    User user;

    public FragmentViewPagerAdapter(FragmentManager fm, Context context, User user) {
        super(fm);
        this.context = context;
        this.user = user;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER_EXTRA, Parcels.wrap(user));
        switch (position) {
            case 0:
                HomeTimelineFragment homeTimelineFragment = HomeTimelineFragment.newInstance();
                fragment = homeTimelineFragment;
                break;
            case 1:
                MentionTimelineFragment mentionTimelineFragment = MentionTimelineFragment.newInstance();
                fragment = mentionTimelineFragment;
                break;
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.home);
        } else {
            return context.getString(R.string.mention);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
