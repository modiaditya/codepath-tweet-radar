package com.aditya.tweetradar.models;

import com.aditya.tweetradar.database.TweetRadarDatabase;
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
    public String screenName;

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            user.id = jsonObject.getLong("id");
            user.name = jsonObject.getString("name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.screenName = "@" + jsonObject.getString("screen_name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
