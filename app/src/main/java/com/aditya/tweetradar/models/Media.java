package com.aditya.tweetradar.models;

import android.util.Log;
import com.aditya.tweetradar.persistence.TweetRadarDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by amodi on 3/26/17.
 */

@Table(database = TweetRadarDatabase.class)
@Parcel(analyze = {Media.class})
public class Media extends BaseModel {
    @Column
    @PrimaryKey
    public long uid;

    @Column
    public String type;

    @Column
    public String mediaUrlHttps;

    @Column
    public String mediaUrl;

    @Column
    public String videoUrlHttps;


    public static Media fromJson(JSONObject jsonObject){
        Media media = new Media();
        Log.d("Log", jsonObject.toString());
        try {
            media.type = jsonObject.getString("type");
            media.mediaUrl = jsonObject.getString("media_url");
            media.mediaUrlHttps = jsonObject.getString("media_url_https");
            media.uid = jsonObject.getLong("id");

            //If media type video
            if(media.type.equals("video"))
                media.videoUrlHttps = jsonObject.getJSONObject("video_info").
                    getJSONArray("variants").getJSONObject(0).getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return media;
    }
}
