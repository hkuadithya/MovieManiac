package com.adithyaupadhya.searchmodule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by adithya.upadhya on 05-10-2016.
 */

class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder> {

    private List<String> mSuggestions;
    private Context mContext;
    private SearchItemClickListener mSearchItemClickListener;

    SearchRecyclerAdapter(Context context, SearchItemClickListener searchItemClickListener) {
        mContext = context;
        mSearchItemClickListener = searchItemClickListener;
    }

    void setNewSuggestions(List<String> newSuggestions) {
        mSuggestions = newSuggestions;
        notifyDataSetChanged();
    }


    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.suggest_item, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        holder.textViewSuggestions.setText(mSuggestions.get(position));
    }

    @Override
    public int getItemCount() {
        return (mSuggestions != null) ? mSuggestions.size() : 0;
    }

    class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewSuggestions;

        SearchViewHolder(View itemView) {
            super(itemView);
            textViewSuggestions = (TextView) itemView.findViewById(R.id.suggestion_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mSearchItemClickListener.onSearchItemClick(mSuggestions.get(getAdapterPosition()));
        }
    }

    interface SearchItemClickListener {
        void onSearchItemClick(String searchString);
    }
}
