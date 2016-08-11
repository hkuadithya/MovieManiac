package com.adithyaupadhya.moviemaniac.tvseries.tvseries;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adithyaupadhya.database.DBConstants;
import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.Utils;
import com.adithyaupadhya.moviemaniac.base.interfaces.OnImageClickListener;
import com.adithyaupadhya.moviemaniac.base.interfaces.OnLoadMoreListener;
import com.adithyaupadhya.moviemaniac.tvseries.tvseriesdetails.TVSeriesDetailsActivity;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBTVSeriesResponse;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

/**
 * Created by adithya.upadhya on 09-01-2016.
 */
public class TVSeriesAdapter extends RecyclerView.Adapter<TVSeriesAdapter.RecyclerViewHolder> {

    private TMDBTVSeriesResponse mResponse;
    private Context mContext;
    private OnLoadMoreListener mLoadMoreListener;
    private OnImageClickListener mImageClickListener;
    private LinearLayoutManager mLayoutManager;
    private boolean loading = false;

    public TVSeriesAdapter(Context context,
                           RecyclerView recyclerView,
                           OnLoadMoreListener onLoadMoreListener,
                           OnImageClickListener onImageClickListener) {

        mContext = context;
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        mLoadMoreListener = onLoadMoreListener;
        mImageClickListener = onImageClickListener;
    }

    public void setNewAPIResponse(TMDBTVSeriesResponse response) {
        mResponse = response;
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.movie_tv_data_layout_row, parent, false));
        viewHolder.context = mContext;
        viewHolder.onImageClickListener = mImageClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        TMDBTVSeriesResponse.Results results = mResponse.results.get(position);
        holder.results = results;

        Glide.with(mContext)
                .load(results.poster_path != null ? NetworkConstants.IMG_BASE_POSTER_URL + results.poster_path :
                        NetworkConstants.IMG_BASE_POSTER_URL + results.backdrop_path)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.not_found)
                .dontAnimate()
                .into(holder.networkImageView);

        holder.textViewMovieName.setText(results.name);
        holder.textViewVoteCountValue.setText(Utils.toString(results.vote_count));
        holder.textViewReleaseDate.setText(results.first_air_date);
        holder.textViewVoteAverageValue.setText(APIConstants.getFormattedDecimal(results.vote_average));
        holder.textViewGenreValue.setText(APIConstants.getInstance().getMovieGenreList(results.genre_ids, mContext));

        holder.imageViewLanguage.setImageDrawable(APIConstants.getInstance().getCountryFlag(results.origin_country) != null ?
                ContextCompat.getDrawable(mContext, APIConstants.getInstance().getCountryFlag(results.origin_country)) : null);

        if (position + 1 >= mLayoutManager.getItemCount() && !loading && mResponse.results.size() < mResponse.total_results) {
            loading = true;
            mLoadMoreListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return mResponse != null ? mResponse.results.size() : 0;
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener,
            MaterialDialog.SingleButtonCallback {

        private ImageView networkImageView;
        private ImageView imageViewLanguage;
        private TextView textViewMovieName, textViewGenreValue, textViewVoteCountValue, textViewVoteAverageValue, textViewReleaseDate;
        boolean isFavoriteItem;

        private TMDBTVSeriesResponse.Results results;
        private Context context;
        private OnImageClickListener onImageClickListener;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            networkImageView = (ImageView) itemView.findViewById(R.id.networkImageView);
            imageViewLanguage = (ImageView) itemView.findViewById(R.id.imageViewLanguage);
            textViewMovieName = (TextView) itemView.findViewById(R.id.textViewMovieName);
            textViewGenreValue = (TextView) itemView.findViewById(R.id.textViewGenreValue);
            textViewVoteCountValue = (TextView) itemView.findViewById(R.id.textViewVoteCountValue);
            textViewVoteAverageValue = (TextView) itemView.findViewById(R.id.textViewVoteAverageValue);
            textViewReleaseDate = (TextView) itemView.findViewById(R.id.textViewReleaseDate);

            networkImageView.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.networkImageView:
                    onImageClickListener.onImageClick((results.poster_path != null) ? results.poster_path : results.backdrop_path);

                default:
                    TVSeriesDetailsActivity.startActivityIntent(context, results);
            }
        }

        @Override
        public boolean onLongClick(View v) {

            isFavoriteItem = Utils.isRecordFavoriteItem(context,
                    DBConstants.TVSERIES_TABLE_URI,
                    DBConstants.TVSERIES_ID,
                    results.id);

            Utils.showFavoritesMaterialDialog(context, this, isFavoriteItem);
            return true;
        }

        @Override
        public void onClick(MaterialDialog dialog, DialogAction which) {
            Utils.insertDeleteFromTVSeriesTable(context, results, isFavoriteItem);
        }
    }
}
