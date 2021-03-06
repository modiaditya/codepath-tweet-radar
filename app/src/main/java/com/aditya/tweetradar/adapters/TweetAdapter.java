package com.aditya.tweetradar.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
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
import com.aditya.tweetradar.fragments.SearchTimelineFragment;
import com.aditya.tweetradar.fragments.TweetComposeDialogFragment;
import com.aditya.tweetradar.helpers.PatternEditableBuilder;
import com.aditya.tweetradar.models.Tweet;
import com.aditya.tweetradar.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import java.util.regex.Pattern;

/**
 * Created by amodi on 3/24/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private static int LOADING_ITEM_TYPE = 0;
    private static int TWEET_ITEM_TYPE = 1;

    ArrayList<Tweet> tweets;
    Context context;
    User loggedInUser;
    OnUserClickListener onUserClickListener;
    TweetComposeDialogFragment.TweetComposeDialogFragmentListener tweetComposeDialogFragmentListener;
    SearchTimelineFragment.OnSearchListener onSearchListener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public TweetAdapter(Context context) {
        this.context = context;
        tweets = new ArrayList<>();
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TWEET_ITEM_TYPE) {
            View v = LayoutInflater.from(context).inflate(R.layout.tweet_item, parent, false);
            return new TweetViewHolder(v);
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.loading_item, parent, false);
            return new LoadingViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == LOADING_ITEM_TYPE) {
            return;
        }

        TweetViewHolder tweetViewHolder = (TweetViewHolder) holder;

        final Tweet tweet = tweets.get(position);
        tweetViewHolder.name.setText(tweet.user.name);
        tweetViewHolder.screenName.setText(tweet.user.screenName);
        tweetViewHolder.text.setText(tweet.text);
        tweetViewHolder.retweetCount.setText(String.valueOf(tweet.retweetCount));
        tweetViewHolder.favoriteCount.setText(String.valueOf(tweet.favoriteCount));
        Glide.with(context).load(tweet.user.profileImageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(tweetViewHolder.profileImage);
        tweetViewHolder.createdTime.setText(getRelativeTime(tweet.createdAt));
        if (tweet.isRetweeted) {
            tweetViewHolder.retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_retweet_activated));
            tweetViewHolder.retweetCount.setTextColor(ContextCompat.getColor(context, R.color.retweet));
        } else {
            tweetViewHolder.retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_retweet));
            tweetViewHolder.retweetCount.setTextColor(ContextCompat.getColor(context, R.color.deactivate));
        }

        if (tweet.isFavorited) {
            tweetViewHolder.favoriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_activated));
            tweetViewHolder.favoriteCount.setTextColor(ContextCompat.getColor(context, R.color.favorite));
        } else {
            tweetViewHolder.favoriteImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite));
            tweetViewHolder.favoriteCount.setTextColor(ContextCompat.getColor(context, R.color.deactivate));
        }

        if(tweet.media != null) {
            Glide.with(context)
                 .load(tweet.media.mediaUrlHttps)
                 .diskCacheStrategy(DiskCacheStrategy.ALL)
                 .into(((TweetViewHolder) holder).tweetImage);
            tweetViewHolder.tweetImage.setVisibility(View.VISIBLE);
        } else {
            tweetViewHolder.tweetImage.setVisibility(View.GONE);
        }

        if (tweet.user.isVerified) {
            tweetViewHolder.verifiedImage.setVisibility(View.VISIBLE);
        } else {
            tweetViewHolder.verifiedImage.setVisibility(View.GONE);
        }

        tweetViewHolder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUserClickListener.onUserClick(tweet.user);
            }
        });

        new PatternEditableBuilder()
            .addPattern(Pattern.compile("\\@(\\w+)"), ContextCompat.getColor(context, R.color.colorPrimary),
                            new PatternEditableBuilder.SpannableClickedListener() {
                                @Override
                                public void onSpanClicked(String text) {
                                    onSearchListener.onSearch(text);
                                }
                            }).into(tweetViewHolder.text);

        new PatternEditableBuilder()
            .addPattern(Pattern.compile("\\#(\\w+)"), ContextCompat.getColor(context, R.color.colorPrimary),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                onSearchListener.onSearch(text);
                            }
                        }).into(tweetViewHolder.text);

    }

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    public void setTweetComposeDialogFragmentListener(TweetComposeDialogFragment.TweetComposeDialogFragmentListener tweetComposeDialogFragmentListener) {
        this.tweetComposeDialogFragmentListener = tweetComposeDialogFragmentListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= tweets.size()) {
            return LOADING_ITEM_TYPE;
        } else {
            return TWEET_ITEM_TYPE;
        }
    }

    public void setOnSearchListener(SearchTimelineFragment.OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    @Override
    public int getItemCount() {
        return (tweets.size() + 1);
    }

    public void addTweet(Tweet tweet) {
        tweet.save();
        tweets.add(tweet);
    }

    public void addTweet(int position, Tweet tweet) {
        tweet.save();
        tweets.add(position, tweet);
    }

    public void addTweets(List<Tweet> tweets) {
        saveTweets(tweets);
        this.tweets.addAll(tweets);
    }

    public void setTweet(int position, Tweet tweet) {
        tweet.save();
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

    private void saveTweets(List<Tweet> tweets) {
        for (Tweet tweet : tweets) {
            tweet.save();
        }
    }


    abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    class TweetViewHolder extends ViewHolder {
        @BindView(R.id.tvName) TextView name;
        @BindView(R.id.tvScreenName) TextView screenName;
        @BindView(R.id.imageProfile) ImageView profileImage;
        @BindView(R.id.tvCreatedTime) TextView createdTime;
        @BindView(R.id.tvText) TextView text;
        @BindView(R.id.tvRetweetCount) TextView retweetCount;
        @BindView(R.id.tvFavoriteCount) TextView favoriteCount;
        @BindView(R.id.imageReply) ImageView replyImage;
        @BindView(R.id.imageFavorite) ImageView favoriteImage;
        @BindView(R.id.imageRetweet) ImageView retweetImage;
        @BindView(R.id.imageTweet) ImageView tweetImage;
        @BindView(R.id.imageVerified) ImageView verifiedImage;

        public TweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupFavorite();
            setupRetweet();
            setupReplyTweet();
        }

        private void setupFavorite() {
            favoriteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    Tweet tweet = tweets.get(position);

                    TweetRadarApplication.getTwitterClient().postFavorite(!tweet.isFavorited, tweet.id, new JsonHttpResponseHandler() {
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

        private void setupReplyTweet() {
            replyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    final Tweet tweet = tweets.get(position);

                    TweetComposeDialogFragment fragment = TweetComposeDialogFragment.newInstance(loggedInUser,
                                                                                                 tweet.id,
                                                                                                 tweet.user.screenName);
                    fragment.setTweetComposeDialogFragmentListener(tweetComposeDialogFragmentListener);
                    fragment.show(((FragmentActivity)context).getSupportFragmentManager(), "reply_tweet");


                }
            });
        }
    }

    class LoadingViewHolder extends ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
