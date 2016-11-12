package com.adithyaupadhya.moviemaniac.tvseries.favoritetvseries;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adithyaupadhya.database.DBConstants;
import com.adithyaupadhya.database.sharedpref.AppPreferenceManager;
import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.AbstractTabFragment;
import com.adithyaupadhya.moviemaniac.base.interfaces.ZeroStateImageListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteTVSeriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AbstractTabFragment.OnFabSnackBarClickListener, ZeroStateImageListener {
    private static final int URL_LOADER = 0;
    private View mLayoutZeroState;
    private RecyclerView mRecyclerView;
    private FavoriteTVSeriesAdapter mCursorAdapter;

    public FavoriteTVSeriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_layout, container, false);
        mLayoutZeroState = view.findViewById(R.id.layoutZeroState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewFavorites);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getLoaderManager().initLoader(URL_LOADER, null, this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCursorAdapter = new FavoriteTVSeriesAdapter(getContext(), null);
        mCursorAdapter.setZeroStateImageListener(this);
        mRecyclerView.setAdapter(mCursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;
        switch (id) {
            case URL_LOADER:
                String userId = AppPreferenceManager.getAppPreferenceInstance(getContext()).getPreferenceData(DBConstants.USER_ID);
                cursorLoader = new CursorLoader(getContext(),
                        DBConstants.TVSERIES_TABLE_URI,
                        new String[]{" * "},
                        DBConstants.USER_ID + "=?",
                        new String[]{userId},
                        DBConstants.DATETIME + " DESC");
                break;

            default:
                cursorLoader = null;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onFabSnackBarClick(boolean isFabClicked) {
        if (isFabClicked)
            if (mRecyclerView != null)
                mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, 0);
    }

    @Override
    public void handleZeroStateImage(boolean isZeroState) {
        if (isZeroState) {
            mRecyclerView.setVisibility(View.GONE);
            mLayoutZeroState.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mLayoutZeroState.setVisibility(View.GONE);
        }
    }
}
