package com.adithyaupadhya.moviemaniac.celebrity.celebritylist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.movies.moviedetails.MovieDetailsActivity;
import com.adithyaupadhya.moviemaniac.tvseries.tvseriesdetails.TVSeriesDetailsActivity;
import com.adithyaupadhya.newtorkmodule.volley.constants.NetworkConstants;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBCelebrityResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBMoviesResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBTVSeriesResponse;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by adithya.upadhya on 28-01-2016.
 */
public class CelebritiesKnownForAdapter extends RecyclerView.Adapter<CelebritiesKnownForAdapter.RecyclerViewHolder> {
    private List<TMDBCelebrityResponse.Results.KnownFor> mKnownForList;
    private Context mContext;

    public CelebritiesKnownForAdapter(Context context) {
        this.mContext = context;
    }

    public void setCelebrityKnownForList(List<TMDBCelebrityResponse.Results.KnownFor> known_for) {
        this.mKnownForList = known_for;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.celebrity_movie_layout, parent, false));
        viewHolder.context = mContext;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.knownFor = mKnownForList.get(position);

        Glide.with(mContext)
                .load(NetworkConstants.IMG_SIMILAR_ITEMS_POSTER_URL + mKnownForList.get(position).poster_path)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.not_found)
                .dontAnimate()
                .into(holder.networkImageView);
    }

    @Override
    public int getItemCount() {
        return mKnownForList != null ? mKnownForList.size() : 0;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView networkImageView;
        private TMDBCelebrityResponse.Results.KnownFor knownFor;
        private Context context;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            networkImageView = (ImageView) itemView.findViewById(R.id.networkImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (knownFor.media_type.toLowerCase()) {
                case "tv":
                    TVSeriesDetailsActivity.startActivityIntent(context, converToTVSeriesResponse(knownFor));
                    break;
                case "movie":
                    MovieDetailsActivity.startActivityIntent(context, convertToMoviesResponse(knownFor));
                    break;
            }
        }

        private TMDBMoviesResponse.Results convertToMoviesResponse(TMDBCelebrityResponse.Results.KnownFor knownFor) {
            TMDBMoviesResponse.Results results = new TMDBMoviesResponse.Results();
            results.poster_path = knownFor.poster_path;
            results.overview = knownFor.overview;
            results.release_date = knownFor.release_date;
            results.genre_ids = knownFor.genre_ids;
            results.id = knownFor.id;
            results.original_language = knownFor.original_language;
            results.title = knownFor.title;
            results.backdrop_path = knownFor.backdrop_path;
            results.vote_count = knownFor.vote_count;
            results.vote_average = knownFor.vote_average;
            return results;
        }

        private TMDBTVSeriesResponse.Results converToTVSeriesResponse(TMDBCelebrityResponse.Results.KnownFor knownFor) {
            TMDBTVSeriesResponse.Results results = new TMDBTVSeriesResponse.Results();
            results.poster_path = knownFor.poster_path;
            results.id = knownFor.id;
            results.backdrop_path = knownFor.backdrop_path;
            results.vote_average = knownFor.vote_average;
            results.overview = knownFor.overview;
            results.first_air_date = knownFor.first_air_date;
            results.genre_ids = knownFor.genre_ids;
            results.original_language = knownFor.original_language;
            results.vote_count = knownFor.vote_count;
            results.name = knownFor.name;
            return results;
        }
    }

}
