package com.aditya.tweetradar.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amodi on 4/2/17.
 */

@Parcel(analyze={Trending.class})
public class Trending {

    public String name;

    public String query;

    public Long tweetVolume;

    public static Trending fromJSON(JSONObject jsonObject) {
        Trending trending = new Trending();
        try {
            trending.name = jsonObject.getString("name");
            trending.query = jsonObject.getString("query");
            trending.tweetVolume = jsonObject.getLong("tweet_volume");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trending;
    }

    public static List<Trending> fromJSONArray(JSONArray array) {
        List<Trending> trendings = new ArrayList<>();

        for (int index = 0; index < array.length(); ++index) {
            try {
                Trending trending = Trending.fromJSON(array.getJSONObject(index));
                if (trendings != null) {
                    trendings.add(trending);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return trendings;
    }
}
