package com.aditya.tweetradar.adapters;

import android.content.Context;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.TweetRadarApplication;
import com.aditya.tweetradar.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by amodi on 4/2/17.
 */

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {

    List<User> users;
    Context context;
    public PersonAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.person_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final User user = users.get(position);
        holder.name.setText(user.name);
        holder.screenName.setText(user.screenName);

        if (user.isVerified) {
            holder.isVerified.setVisibility(View.VISIBLE);
        } else {
            holder.isVerified.setVisibility(View.GONE);
        }
        if (user.isFollowing) {
            holder.following.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_following));
            holder.following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TweetRadarApplication.getTwitterClient().followUser(false, user.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            User user = User.fromJSON(response);
                            users.set(position, user);
                            notifyDataSetChanged();
                        }
                    });
                }
            });

        } else {
            holder.following.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_not_following));
            holder.following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TweetRadarApplication.getTwitterClient().followUser(true, user.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            User user = User.fromJSON(response);
                            users.set(position, user);
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        }

        Glide.with(context).load(user.profileImageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addUsers(List<User> users) {
        this.users.addAll(users);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfile) ImageView profileImage;
        @BindView(R.id.tvName) TextView name;
        @BindView(R.id.tvScreenName) TextView screenName;
        @BindView(R.id.ivVerified) ImageView isVerified;
        @BindView(R.id.ivFollowingButton) Button following;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
