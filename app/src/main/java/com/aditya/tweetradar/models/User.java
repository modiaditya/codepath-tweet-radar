package com.aditya.tweetradar.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by amodi on 3/23/17.
 */

@Parcel
public class User {

    public String id;
    public String name;
    public String profileImageUrl;
    public String screenName;

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            user.id = jsonObject.getString("id_str");
            user.name = jsonObject.getString("name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.screenName = "@" + jsonObject.getString("screen_name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
