package com.aditya.tweetradar.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.adapters.TweetAdapter;
import com.aditya.tweetradar.fragments.SearchTimelineFragment;
import com.aditya.tweetradar.fragments.TrendingFragment;
import com.aditya.tweetradar.models.User;
import org.parceler.Parcels;

import static com.aditya.tweetradar.activities.TweetTimelineActivity.USER_EXTRA;

/**
 * Created by amodi on 4/2/17.
 */

public class SearchActivity extends AppCompatActivity implements TweetAdapter.OnUserClickListener, SearchTimelineFragment.OnSearchListener {
    public static final String SEARCH_QUERY_EXTRA = "search_query_extra";

    @BindView(R.id.toolbar) Toolbar toolbar;
    private String query;
    private SearchView searchView;

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(USER_EXTRA, Parcels.wrap(user));
        startActivity(intent);
    }

    @Override
    public void onSearch(String query) {
        FragmentManager fm = getSupportFragmentManager();
        searchView.setQuery(query, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            String query = intent.getStringExtra(SEARCH_QUERY_EXTRA);
            if (!TextUtils.isEmpty(query)) {
                this.query = query;
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.searchContainer, SearchTimelineFragment.newInstance(query)).commit();
            }
        }

        if (TextUtils.isEmpty(this.query)) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.searchContainer, new TrendingFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.WHITE);
        et.setHintTextColor(Color.WHITE);

        MenuItemCompat.expandActionView(searchItem);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        if (!TextUtils.isEmpty(this.query)) {
            searchView.setQuery(this.query, false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.searchContainer, SearchTimelineFragment.newInstance(query)).commit();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

}
