package com.adithyaupadhya.moviemaniac.tvseries.favoritetvseries;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adithyaupadhya.database.DBConstants;
import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.AbstractCursorAdapter;
import com.adithyaupadhya.moviemaniac.base.Utils;
import com.adithyaupadhya.moviemaniac.base.interfaces.OnImageClickListener;
import com.adithyaupadhya.moviemaniac.tvseries.tvseriesdetails.TVSeriesDetailsActivity;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBTVSeriesResponse;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.adithyaupadhya.uimodule.applicationfont.RobotoTextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by adithya.upadhya on 01-03-2016.
 */
public class FavoriteTVSeriesAdapter extends AbstractCursorAdapter<FavoriteTVSeriesAdapter.RecyclerVH> {
    private Context mContext;
    private ObjectMapper mObjectMapper;
    private OnImageClickListener mImageClickListener;

    public FavoriteTVSeriesAdapter(Context context, Cursor cursor) {
        super(cursor);
        mContext = context;
        mObjectMapper = APIConstants.getInstance().getJacksonObjectMapper();
    }

    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_tv_data_layout_row, parent, false);
        return new RecyclerVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerVH recyclerVH, Cursor cursor) {
        TMDBTVSeriesResponse.Results results = null;
        try {
            results = mObjectMapper.readValue(cursor.getString(cursor.getColumnIndex(DBConstants.TVSERIES_DETAILS)), TMDBTVSeriesResponse.Results.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if (results.poster_path != null)
//            recyclerVH.networkImageView.setImageUrl(NetworkConstants.IMG_BASE_POSTER_URL + results.poster_path, mImageLoader);
//        else if (results.backdrop_path != null)
//            recyclerVH.networkImageView.setImageUrl(NetworkConstants.IMG_BASE_POSTER_URL + results.backdrop_path, mImageLoader);

        Glide.with(mContext)
                .load(results.poster_path != null ? NetworkConstants.IMG_BASE_POSTER_URL + results.poster_path :
                        NetworkConstants.IMG_BASE_POSTER_URL + results.backdrop_path)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.not_found)
                .dontAnimate()
                .into(recyclerVH.networkImageView);

        recyclerVH.textViewMovieName.setText(results.name);
        recyclerVH.textViewVoteCountValue.setText(Utils.toString(results.vote_count));
        recyclerVH.textViewReleaseDate.setText(results.first_air_date);
        recyclerVH.textViewVoteAverageValue.setText(APIConstants.getFormattedDecimal(results.vote_average));
        recyclerVH.textViewGenreValue.setText(APIConstants.getInstance().getMovieGenreList(results.genre_ids));

        recyclerVH.imageViewLanguage.setImageDrawable(APIConstants.getInstance().getCountryFlag(results.original_language) != null ?
                ContextCompat.getDrawable(mContext, APIConstants.getInstance().getCountryFlag(results.original_language)) : null);
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mImageClickListener = listener;
    }

    public class RecyclerVH extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener,
            MaterialDialog.SingleButtonCallback {
        private ImageView networkImageView;
        private ImageView imageViewLanguage;
        private TextView textViewMovieName, textViewGenreValue, textViewVoteCountValue, textViewVoteAverageValue, textViewReleaseDate;

        public RecyclerVH(View itemView) {
            super(itemView);
            networkImageView = (ImageView) itemView.findViewById(R.id.networkImageView);
            imageViewLanguage = (ImageView) itemView.findViewById(R.id.imageViewLanguage);
            textViewMovieName = (RobotoTextView) itemView.findViewById(R.id.textViewMovieName);
            textViewGenreValue = (RobotoTextView) itemView.findViewById(R.id.textViewGenreValue);
            textViewVoteCountValue = (RobotoTextView) itemView.findViewById(R.id.textViewVoteCountValue);
            textViewVoteAverageValue = (RobotoTextView) itemView.findViewById(R.id.textViewVoteAverageValue);
            textViewReleaseDate = (RobotoTextView) itemView.findViewById(R.id.textViewReleaseDate);

//            networkImageView.setErrorImageResId(R.drawable.not_found);
//            networkImageView.setDefaultImageResId(R.drawable.default_img);
            networkImageView.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TMDBTVSeriesResponse.Results results = null;
            Cursor cursor = getCursor();
            cursor.moveToPosition(getAdapterPosition());
            try {
                results = mObjectMapper.readValue(cursor.getString(cursor.getColumnIndex(DBConstants.TVSERIES_DETAILS)), TMDBTVSeriesResponse.Results.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (v.getId() == R.id.networkImageView)
                mImageClickListener.onImageClick((results.poster_path != null) ?
                        results.poster_path : results.backdrop_path);
            else {
                /*mContext.startActivity(TVSeriesDetailsActivity.getActivityIntent(mContext,
                        cursor.getString(cursor.getColumnIndex(DBConstants.TVSERIES_DETAILS))));*/
                try {
                    TMDBTVSeriesResponse.Results response = APIConstants.getInstance().getJacksonObjectMapper().readValue(cursor.getString(cursor.getColumnIndex(DBConstants.TVSERIES_DETAILS)), TMDBTVSeriesResponse.Results.class);
                    TVSeriesDetailsActivity.startActivityIntent(mContext, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //cursor.close();
            }

        }

        @Override
        public boolean onLongClick(View v) {
            Utils.showFavoritesMaterialDialog(mContext, this, true);
            return true;
        }

        @Override
        public void onClick(MaterialDialog dialog, DialogAction which) {
            Cursor cursor = getCursor();
            cursor.moveToPosition(getAdapterPosition());
            TMDBTVSeriesResponse.Results results = new TMDBTVSeriesResponse.Results();
            results.id = cursor.getInt(cursor.getColumnIndex(DBConstants.TVSERIES_ID));
            Utils.insertDeleteFromTVSeriesTable(mContext, results, true);
        }
    }
}
