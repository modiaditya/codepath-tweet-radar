package com.aditya.tweetradar.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.TweetRadarApplication;
import com.aditya.tweetradar.models.Tweet;
import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;

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
        if (tweet.isRetweeted) {
            holder.retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_retweet_activated));
            holder.retweetCount.setTextColor(ContextCompat.getColor(context, R.color.retweet));
        } else {
            holder.retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_retweet));
            holder.retweetCount.setTextColor(ContextCompat.getColor(context, R.color.deactivate));
        }

        if (tweet.isFavorited) {
            holder.favoriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_activated));
            holder.favoriteCount.setTextColor(ContextCompat.getColor(context, R.color.favorite));
        } else {
            holder.favoriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite));
            holder.favoriteCount.setTextColor(ContextCompat.getColor(context, R.color.deactivate));
        }

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void addTweet(Tweet tweet) {
        tweets.add(tweet);
    }

    public void addTweet(int position, Tweet tweet) {
        tweets.add(position, tweet);
    }

    public void addTweets(List<Tweet> tweets) {
        this.tweets.addAll(tweets);
    }

    public void setTweet(int position, Tweet tweet) {
        this.tweets.set(position, tweet);
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
        @BindView(R.id.imageFavorite) ImageView favoriteImage;
        @BindView(R.id.imageRetweet) ImageView retweetImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupFavorite();
            setupRetweet();
        }

        private void setupFavorite() {
            favoriteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    Tweet tweet = tweets.get(position);

                    TweetRadarApplication.getTwitterClient().postFavorite(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Tweet tweetResponse = Tweet.fromJSON(response);
                            setTweet(position, tweetResponse);
                            notifyItemChanged(position);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                              JSONObject errorResponse) {
                            Toast.makeText(context, "Failed to favroite post", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        private void setupRetweet() {
            retweetImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    final Tweet tweet = tweets.get(position);

                    TweetRadarApplication.getTwitterClient().postRetweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            tweet.isRetweeted = true;
                            tweet.retweetCount = (tweet.retweetCount +1);
                            notifyItemChanged(position);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                              JSONObject errorResponse) {
                            Toast.makeText(context, "Failed to retweet post", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }


    }
}
