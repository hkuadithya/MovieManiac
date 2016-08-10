package com.adithyaupadhya.moviemaniac.celebrity.favoritecelebrities;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
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
import com.adithyaupadhya.moviemaniac.celebrity.celebritydetails.CelebrityDetailsActivity;
import com.adithyaupadhya.moviemaniac.celebrity.celebritylist.CelebritiesKnownForAdapter;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBCelebrityResponse;
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
public class FavoriteCelebritiesAdapter extends AbstractCursorAdapter<FavoriteCelebritiesAdapter.RecyclerVH> {
    private Context mContext;
    private ObjectMapper mObjectMapper;
    private OnImageClickListener mImageClickListener;

    public FavoriteCelebritiesAdapter(Context context, Cursor cursor) {
        super(cursor);
        mContext = context;
        mObjectMapper = APIConstants.getInstance().getJacksonObjectMapper();
    }

    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.celebrity_layout_row, parent, false);
        return new RecyclerVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerVH recyclerVH, Cursor cursor) {
        TMDBCelebrityResponse.Results results = null;
        try {
            results = mObjectMapper.readValue(cursor.getString(cursor.getColumnIndex(DBConstants.CELEBRITY_DETAILS)), TMDBCelebrityResponse.Results.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Glide.with(mContext)
                .load(NetworkConstants.IMG_BASE_POSTER_URL + results.profile_path)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.not_found)
                .dontAnimate()
                .into(recyclerVH.networkImageView);

        recyclerVH.textViewCelebrityName.setText(results.name);
        recyclerVH.adapter.setCelebrityKnownForList(results.known_for);
    }


    public class RecyclerVH extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener,
            MaterialDialog.SingleButtonCallback {

        private ImageView networkImageView;
        private TextView textViewCelebrityName;
        private RecyclerView recyclerView;
        private CelebritiesKnownForAdapter adapter;

        public RecyclerVH(View itemView) {
            super(itemView);
            networkImageView = (ImageView) itemView.findViewById(R.id.networkImageView);
            textViewCelebrityName = (RobotoTextView) itemView.findViewById(R.id.textViewCelebrityName);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            adapter = new CelebritiesKnownForAdapter(mContext);
            recyclerView.setAdapter(adapter);

            networkImageView.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TMDBCelebrityResponse.Results results = null;
            Cursor cursor = getCursor();
            cursor.moveToPosition(getAdapterPosition());
            try {
                results = mObjectMapper.readValue(cursor.getString(cursor.getColumnIndex(DBConstants.CELEBRITY_DETAILS)), TMDBCelebrityResponse.Results.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (v.getId() == R.id.networkImageView)
                mImageClickListener.onImageClick(results.profile_path);
            else {
                CelebrityDetailsActivity.startActivityIntent(mContext, results);
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
            TMDBCelebrityResponse.Results results = new TMDBCelebrityResponse.Results();
            results.id = cursor.getInt(cursor.getColumnIndex(DBConstants.CELEBRITY_ID));
            Utils.insertDeleteFromCelebritiesTable(mContext, results, true);
        }
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mImageClickListener = listener;
    }
}
