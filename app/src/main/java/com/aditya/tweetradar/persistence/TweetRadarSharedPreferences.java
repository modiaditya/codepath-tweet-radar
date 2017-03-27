package com.aditya.tweetradar.persistence;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by amodi on 3/26/17.
 */

public class TweetRadarSharedPreferences {

    private static final String NAME = "TweetRadarSharedPreference";
    private static final String USER_DRAFT = "UserDraft";
    private static final String MEMBER_ID = "memberId";

    public static void saveUserDraft(Context context, String tweetBody) {
        getSharedPrefernces(context).edit().putString(USER_DRAFT, tweetBody).commit();
    }

    public static String getUserDraft(Context context) {
        return getSharedPrefernces(context).getString(USER_DRAFT, null);
    }

    public static SharedPreferences getSharedPrefernces(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static void clearUserDraft(Context context) {
        getSharedPrefernces(context).edit().remove(USER_DRAFT).commit();
        return;
    }

    public static void saveLoggedInUserId(Context context, Long id) {
        getSharedPrefernces(context).edit().putLong(MEMBER_ID, id).commit();
    }

    public static long getLoggedInUserId(Context context) {
        return getSharedPrefernces(context).getLong(MEMBER_ID, -1);
    }
}
