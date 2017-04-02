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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.adapters.TweetAdapter;
import com.aditya.tweetradar.fragments.SearchTimelineFragment;
import com.aditya.tweetradar.models.User;
import org.parceler.Parcels;

import static com.aditya.tweetradar.activities.TweetTimelineActivity.USER_EXTRA;

/**
 * Created by amodi on 4/2/17.
 */

public class SearchActivity extends AppCompatActivity implements TweetAdapter.OnUserClickListener{

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(USER_EXTRA, Parcels.wrap(user));
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.WHITE);
        et.setHintTextColor(Color.WHITE);
        MenuItemCompat.expandActionView(searchItem);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(SearchActivity.this, "Searched for " + query, Toast.LENGTH_SHORT).show();
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
