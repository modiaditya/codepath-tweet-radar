package com.aditya.tweetradar.client;

import android.content.Context;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/**
 * Created by amodi on 3/22/17.
 */

public class TwitterClient extends OAuthBaseClient {

    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "kQuoLrVxorkhXhGO2kb0eFfTD";
    public static final String REST_CONSUMER_SECRET = "wTaD68OECBPQDkQW2I911BIzyHqbjUa8hbwxfZbYT8skTL4DZg";
    public static final String REST_CALLBACK_URL = "x-oauthflow-twitter://aditya.tweetradar";



    public TwitterClient(Context c) {
        super(c, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getHomeTimeline(Long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        if (maxId != null) {
            params.put("max_id", (maxId - 1));
        }
        client.get(apiUrl, params, handler);
    }

    public void getUserInformation(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        client.get(apiUrl, handler);
    }


}
