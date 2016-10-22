package com.adithyaupadhya.moviemaniac.celebrity.celebritylist;

import android.content.Context;
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
import com.adithyaupadhya.moviemaniac.base.interfaces.OnLoadMoreListener;
import com.adithyaupadhya.moviemaniac.celebrity.celebritydetails.CelebrityDetailsActivity;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBCelebrityResponse;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.adithyaupadhya.uimodule.applicationfont.RobotoTextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

/**
 * Created by adithya.upadhya on 09-01-2016.
 */
class CelebritiesAdapter extends RecyclerView.Adapter<CelebritiesAdapter.RecyclerViewHolder> {

    private TMDBCelebrityResponse mResponse;
    private Context mContext;
    private OnLoadMoreListener mLoadMoreListener;
    private LinearLayoutManager mLayoutManager;
    private boolean loading = false;

    CelebritiesAdapter(Context context,
                       RecyclerView recyclerView,
                       OnLoadMoreListener onLoadMoreListener) {

        mContext = context;
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        mLoadMoreListener = onLoadMoreListener;
    }

    void setNewAPIResponse(TMDBCelebrityResponse response) {
        mResponse = response;
    }


    void setLoaded() {
        loading = false;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.celebrity_layout_row, parent, false), mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        TMDBCelebrityResponse.Results results = mResponse.results.get(position);
        holder.results = results;

        Glide.with(mContext)
                .load(NetworkConstants.IMG_BASE_POSTER_URL + results.profile_path)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.not_found)
                .dontAnimate()
                .into(holder.networkImageView);

        holder.textViewCelebrityName.setText(results.name);
        holder.adapter.setCelebrityKnownForList(results.known_for);

        if (position + 1 >= mLayoutManager.getItemCount() && !loading && mResponse.results.size() < mResponse.total_results) {
            loading = true;
            mLoadMoreListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return mResponse != null ? mResponse.results.size() : 0;
    }

    void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mLoadMoreListener = onLoadMoreListener;
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener,
            MaterialDialog.SingleButtonCallback {

        private ImageView networkImageView;
        private TextView textViewCelebrityName;
        private RecyclerView recyclerView;
        private boolean isFavoriteItem;
        private CelebritiesKnownForAdapter adapter;

        private Context context;
        private TMDBCelebrityResponse.Results results;

        RecyclerViewHolder(View itemView, Context context) {
            super(itemView);
            networkImageView = (ImageView) itemView.findViewById(R.id.networkImageView);
            textViewCelebrityName = (RobotoTextView) itemView.findViewById(R.id.textViewCelebrityName);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.startNestedScroll(RecyclerView.HORIZONTAL);
            recyclerView.setNestedScrollingEnabled(false);

            this.context = context;
            adapter = new CelebritiesKnownForAdapter(context);
            recyclerView.setAdapter(adapter);

            networkImageView.setOnClickListener(this);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.networkImageView:
                    Utils.showImageDialogFragment(context, results.profile_path);
                    break;

                default:
                    CelebrityDetailsActivity.startActivityIntent(context, results);
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            isFavoriteItem = Utils.isRecordFavoriteItem(context,
                    DBConstants.CELEBRITY_TABLE_URI,
                    DBConstants.CELEBRITY_ID,
                    results.id);

            Utils.showFavoritesMaterialDialog(context, this, isFavoriteItem);
            return true;
        }

        @Override
        public void onClick(MaterialDialog dialog, DialogAction which) {
            Utils.insertDeleteFromCelebritiesTable(context, results, isFavoriteItem);
        }
    }
}
