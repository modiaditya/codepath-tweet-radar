package com.aditya.tweetradar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.models.Tweet;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by amodi on 3/24/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    ArrayList<Tweet> tweets;
    Context context;

    public TweetAdapter(Context context) {
        this.context = context;
        tweets = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tweet_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.name.setText(tweet.user.name);
        holder.screenName.setText(tweet.user.screenName);
        holder.text.setText(tweet.text);
        holder.retweetCount.setText(String.valueOf(tweet.retweetCount));
        holder.favoriteCount.setText(String.valueOf(tweet.favoriteCount));
        Glide.with(context).load(tweet.user.profileImageUrl).into(holder.profileImage);
        holder.createdTime.setText(getRelativeTime(tweet.createdAt));
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void addTweet(Tweet tweet) {
        tweets.add(tweet);
    }

    public void addTweets(List<Tweet> tweets) {
        this.tweets.addAll(tweets);
    }

    public Tweet getLastLoadedTweet() {
        return tweets.get(tweets.size()-1);
    }

    public void clear() {
        tweets.clear();
    }

    private static String getRelativeTime(String date) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long dateMillis = sf.parse(date).getTime();
            long currentTime = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
            long difference = currentTime - dateMillis;
            int seconds = (int)(difference / 1000);
            int mins = (seconds / 60);
            int hours = (mins / 60);

            if (hours >= 1) {
                return hours+"h";
            }
            if (mins >= 1) {
                return mins+"m";
            }
            if (seconds >= 1) {
                return seconds+"s";
            } else {
                return  "1s";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName) TextView name;
        @BindView(R.id.tvScreenName) TextView screenName;
        @BindView(R.id.imageProfile) ImageView profileImage;
        @BindView(R.id.tvCreatedTime) TextView createdTime;
        @BindView(R.id.tvText) TextView text;
        //@BindView(R.id.tvReplyCount) TextView replyCount;
        @BindView(R.id.tvRetweetCount) TextView retweetCount;
        @BindView(R.id.tvFavoriteCount) TextView favoriteCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
