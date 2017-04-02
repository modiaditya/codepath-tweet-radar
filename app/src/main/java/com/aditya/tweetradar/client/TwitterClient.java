package com.aditya.tweetradar.client;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.net.URLEncoder;

/**
 * Created by amodi on 3/22/17.
 */

public class TwitterClient extends OAuthBaseClient {

    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "zyIdPjz2XvyWSmvN3kUm2g5gW";
    public static final String REST_CONSUMER_SECRET = "AY6EzisEyBziYhrjTrMfm4OuKEo0XLRwVOR82W2QwMA4r7Hn5p";
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

    public void getMentionsTimeline(Long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        if (maxId != null) {
            params.put("max_id", (maxId - 1));
        }
        client.get(apiUrl, params, handler);
    }

    public void getUserTimeline(Long maxId, Long userId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        if (maxId != null) {
            params.put("max_id", (maxId - 1));
        }
        if (userId != null) {
            params.put("user_id", userId);
        }
        client.get(apiUrl, params, handler);
    }

    public void search(Long maxId, String q, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        if (maxId != null) {
            params.put("max_id", (maxId - 1));
        }
        if (q != null) {
            params.put("q", URLEncoder.encode(q));
        }
        client.get(apiUrl, params, handler);
    }

    public void getUserInformation(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        client.get(apiUrl, handler);
    }

    public void postTweet(String tweetBody, Long replyTweetId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweetBody);
        if (replyTweetId != null && replyTweetId != -1) {
            params.put("in_reply_to_status_id", replyTweetId);
        }
        client.post(apiUrl, params, handler);
    }

    public void postFavorite(Long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(apiUrl, params, handler);
    }

    public void getFavoriteList(Long userId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/list.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        client.get(apiUrl, params, handler);
    }

    public void postRetweet(Long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(String.format("statuses/retweet/%s.json", id));
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(apiUrl, params, handler);
    }

    public void followUser(boolean follow, Long userId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(follow ? "friendships/create.json" : "friendships/destroy.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        getClient().post(apiUrl, params, handler);
    }

    public void getFollowersList(Long userId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        getClient().get(apiUrl, params, handler);
    }

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
            = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
