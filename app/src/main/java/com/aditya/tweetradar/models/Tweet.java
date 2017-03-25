package com.aditya.tweetradar.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amodi on 3/23/17.
 */

public class Tweet {

    public Long idLong;
    public String id;
    public String createdAt;
    public int favoriteCount;
    public boolean isFavorited;
    public int retweetCount;
    public boolean isRetweeted;
    public String text;
    public User user;

    public Long getIdLong() {
        return idLong;
    }

    public String getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public boolean isRetweeted() {
        return isRetweeted;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }

    public static Tweet fromJSON(JSONObject object) {
        Tweet tweet = new Tweet();
        try {
            tweet.text = object.getString("text");
            tweet.id = object.getString("id_str");
            tweet.idLong = object.getLong("id");
            tweet.createdAt = object.getString("created_at");

            //JSONObject entities = object.optJSONObject("entities");
            //tweet.entity = entities == null ? null : Entity.fromJSON(entities);

            tweet.retweetCount = object.getInt("retweet_count");
            tweet.isRetweeted = object.getBoolean("retweeted");
            tweet.favoriteCount = object.getInt("favorite_count");
            tweet.isFavorited = object.getBoolean("favorited");
            tweet.user = User.fromJSON(object.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray array) {
        List<Tweet> tweets = new ArrayList<>();

        for (int index = 0; index < array.length(); ++index) {
            try {
                Tweet tweet = Tweet.fromJSON(array.getJSONObject(index));
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }
}
