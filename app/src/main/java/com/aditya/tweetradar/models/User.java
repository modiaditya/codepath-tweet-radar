package com.aditya.tweetradar.models;

import com.aditya.tweetradar.persistence.TweetRadarDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by amodi on 3/23/17.
 */

@Table(database = TweetRadarDatabase.class)
@Parcel(analyze={User.class})
public class User extends BaseModel {

    @Column
    @PrimaryKey
    public Long id;

    @Column
    public String name;

    @Column
    public String profileImageUrl;

    @Column
    public String profileImageBackgroundUrl;

    @Column
    public String screenName;

    @Column
    public boolean isVerified;

    @Column
    public int followersCount;

    @Column
    public boolean isFollowing;

    @Column
    public int followingCount;

    @Column
    public String description;

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            user.id = jsonObject.getLong("id");
            user.name = jsonObject.getString("name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.screenName = "@" + jsonObject.getString("screen_name");
            user.isVerified = jsonObject.getBoolean("verified");
            user.followersCount = jsonObject.getInt("followers_count");
            user.isFollowing = jsonObject.getBoolean("following");
            if (jsonObject.getString("profile_banner_url") != null) {
                user.profileImageBackgroundUrl = jsonObject.getString("profile_banner_url");
            }
            user.followingCount = jsonObject.getInt("friends_count");
            user.description = jsonObject.getString("description");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
