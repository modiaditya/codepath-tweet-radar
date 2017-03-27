package com.aditya.tweetradar.models;

import com.aditya.tweetradar.persistence.TweetRadarDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amodi on 3/23/17.
 */

@Table(database = TweetRadarDatabase.class)
@Parcel(analyze = {Tweet.class})
public class Tweet extends BaseModel {

    @Column
    @PrimaryKey
    public Long id;

    @Column
    public String createdAt;

    @Column
    public int favoriteCount;

    @Column
    public boolean isFavorited;

    @Column
    public int retweetCount;

    @Column
    public boolean isRetweeted;

    @Column
    public String text;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    public Media media;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    public User user;

    public static Tweet fromJSON(JSONObject object) {
        Tweet tweet = new Tweet();
        try {
            tweet.text = object.getString("text");
            tweet.id = object.getLong("id");
            tweet.createdAt = object.getString("created_at");

            //JSONObject entities = object.optJSONObject("entities");
            //tweet.entity = entities == null ? null : Entity.fromJSON(entities);

            tweet.retweetCount = object.getInt("retweet_count");
            tweet.isRetweeted = object.getBoolean("retweeted");
            tweet.favoriteCount = object.getInt("favorite_count");
            tweet.isFavorited = object.getBoolean("favorited");
            tweet.user = User.fromJSON(object.getJSONObject("user"));

            JSONObject mediaObj = object.getJSONObject("entities");
            if(mediaObj.has("media")) {
                JSONArray mediaArray = mediaObj.getJSONArray("media");
                tweet.media = Media.fromJson(mediaArray.getJSONObject(0));
            }
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
