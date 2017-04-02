package com.aditya.tweetradar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.adapters.PersonAdapter;
import com.aditya.tweetradar.listeners.EndlessRecyclerViewScrollListener;
import com.aditya.tweetradar.models.User;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.aditya.tweetradar.activities.TweetTimelineActivity.USER_EXTRA;

/**
 * Created by amodi on 4/2/17.
 */

public abstract class BaseUserListFragment extends Fragment {

    @BindView(R.id.rv_user) RecyclerView recyclerView;
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    RecyclerView.LayoutManager layoutManager;
    PersonAdapter personAdapter;
    User user;
    Long nextCursor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = Parcels.unwrap(getArguments().getParcelable(USER_EXTRA));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, view);


        layoutManager = new LinearLayoutManager(getContext());
        personAdapter = new PersonAdapter(getContext(), new ArrayList<User>());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                                                                getResources().getConfiguration().orientation);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(personAdapter);
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount <= 5) {
                    return;
                }
                fetchFollowList();
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
        fetchFollowList();
        return view;
    }

    private void fetchFollowList() {
        fetchFollowList(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    BaseUserListFragment.this.nextCursor = response.getLong("next_cursor");
                    List<User> users = User.fromJSONArray(response.getJSONArray("users"));
                    personAdapter.addUsers(users);
                    personAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    abstract void fetchFollowList(AsyncHttpResponseHandler responseHandler);
}
