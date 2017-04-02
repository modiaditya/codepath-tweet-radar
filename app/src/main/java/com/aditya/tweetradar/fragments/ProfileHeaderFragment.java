package com.aditya.tweetradar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.helpers.Formatters;
import com.aditya.tweetradar.models.User;
import com.bumptech.glide.Glide;
import org.parceler.Parcels;

import static com.aditya.tweetradar.activities.TweetTimelineActivity.USER_EXTRA;

/**
 * Created by amodi on 4/1/17.
 */

public class ProfileHeaderFragment extends Fragment {

    @BindView(R.id.tvName) TextView name;
    @BindView(R.id.tvScreenName) TextView screenName;
    @BindView(R.id.ivProfile) ImageView profileImage;
    @BindView(R.id.ivCover) ImageView profileBackgroundImage;
    @BindView(R.id.ivVerified) ImageView verifiedImage;
    @BindView(R.id.tvDescription) TextView description;
    @BindView(R.id.tvFollowersCount) TextView followerCount;
    @BindView(R.id.tvFollowingCount) TextView followingCount;


    User user;

    public static ProfileHeaderFragment newInstance(User user) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER_EXTRA, Parcels.wrap(user));
        ProfileHeaderFragment profileHeaderFragment = new ProfileHeaderFragment();
        profileHeaderFragment.setArguments(bundle);
        return profileHeaderFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = Parcels.unwrap(getArguments().getParcelable(USER_EXTRA));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_details, container, false);
        ButterKnife.bind(this, v);

        name.setText(user.name);
        screenName.setText(user.screenName);
        Glide.with(getContext()).load(user.profileImageUrl).into(profileImage);
        Glide.with(getContext()).load(user.profileImageBackgroundUrl).placeholder(R.color.colorPrimary).into(profileBackgroundImage);
        if (user.isVerified) {
            verifiedImage.setVisibility(View.VISIBLE);
        } else {
            verifiedImage.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(user.description)) {
            description.setText(user.description);
            description.setVisibility(View.VISIBLE);
        } else {
            description.setVisibility(View.GONE);
        }
        followerCount.setText(Formatters.formatFollowingInfo(user.followersCount));
        followingCount.setText(Formatters.formatFollowingInfo(user.followingCount));

        return v;
    }
}
