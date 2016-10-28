package com.adithyaupadhya.moviemaniac.tvseries.tvseriesdetails;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.newtorkmodule.volley.constants.NetworkConstants;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBTVSeriesResponse;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by adithya.upadhya on 26-01-2016.
 */
public class TVSeriesSimilarAdapter extends RecyclerView.Adapter<TVSeriesSimilarAdapter.RecyclerViewHolder> {
    private List<TMDBTVSeriesResponse.Results> mTvSeriesList;
    private Context mContext;

    public TVSeriesSimilarAdapter(Context context) {
        this.mContext = context;
    }

    public void setSimilarTvSeriesResponse(List<TMDBTVSeriesResponse.Results> results) {
        this.mTvSeriesList = results;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_movies_layout, parent, false));
        viewHolder.context = mContext;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.results = mTvSeriesList.get(position);

        Glide.with(mContext)
                .load(NetworkConstants.IMG_SIMILAR_ITEMS_POSTER_URL + mTvSeriesList.get(position).poster_path)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.not_found)
                .dontAnimate()
                .into(holder.networkImageView);
    }

    @Override
    public int getItemCount() {
        return mTvSeriesList != null ? mTvSeriesList.size() : 0;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView networkImageView;
        private Context context;
        private TMDBTVSeriesResponse.Results results;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            networkImageView = (ImageView) itemView.findViewById(R.id.networkImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TVSeriesDetailsActivity.startActivityIntent(context, results, Intent.FLAG_ACTIVITY_NO_HISTORY);
        }
    }
}
