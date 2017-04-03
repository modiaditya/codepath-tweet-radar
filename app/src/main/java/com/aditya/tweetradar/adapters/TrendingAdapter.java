package com.aditya.tweetradar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aditya.tweetradar.R;
import com.aditya.tweetradar.fragments.SearchTimelineFragment;
import com.aditya.tweetradar.helpers.Formatters;
import com.aditya.tweetradar.models.Trending;

import java.util.List;

/**
 * Created by amodi on 4/2/17.
 */

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {

    Context context;
    List<Trending> trendingList;
    SearchTimelineFragment.OnSearchListener onSearchListener;
    public TrendingAdapter(Context context, List<Trending> trendingList) {
        this.context = context;
        this.trendingList = trendingList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trending, parent, false);
        return new TrendingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trending trending = trendingList.get(position);
        holder.position.setText(String.valueOf(position + 1));
        holder.trendName.setText(trending.name);
        if (trending.tweetVolume != null) {
            holder.tweetCount.setText(Formatters.formatFollowingInfo(trending.tweetVolume) + " Tweets");
        }

    }

    @Override
    public int getItemCount() {
        return trendingList.size();
    }

    public void addTrendingItems(List<Trending> trendingList) {
        this.trendingList.addAll(trendingList);
    }

    public void setOnSearchListener(SearchTimelineFragment.OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvPosition) TextView position;
        @BindView(R.id.tvTrendName) TextView trendName;
        @BindView(R.id.tvTweetCount) TextView tweetCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Trending trending = trendingList.get(getAdapterPosition());
                    onSearchListener.onSearch(trending.name);
                }
            });
        }
    }
}
