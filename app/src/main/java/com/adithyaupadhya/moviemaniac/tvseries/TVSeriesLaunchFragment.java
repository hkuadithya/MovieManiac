package com.adithyaupadhya.moviemaniac.tvseries;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.AbstractTabFragment;
import com.adithyaupadhya.moviemaniac.tvseries.favoritetvseries.FavoriteTVSeriesFragment;
import com.adithyaupadhya.moviemaniac.tvseries.tvseries.TVSeriesFragment;
import com.adithyaupadhya.moviemaniac.tvseries.tvseriessearch.TVSeriesSearchActivity;
import com.adithyaupadhya.newtorkmodule.volley.VolleySingleton;
import com.adithyaupadhya.newtorkmodule.volley.customjsonrequest.CustomJsonObjectRequest;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.android.volley.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TVSeriesLaunchFragment extends AbstractTabFragment {

    public TVSeriesLaunchFragment() {
        // Required empty public constructor
    }

    @Override
    public List<Fragment> getViewPagerFragmentList() {
        List<Fragment> mFragmentList = new ArrayList<>(3);
        Fragment fragment;

        Bundle bundle = new Bundle();
        bundle.putString(AppIntentConstants.BUNDLE_URL, NetworkConstants.TV_SERIES_ON_THE_AIR_BASE_URL);
        fragment = new TVSeriesFragment();
        fragment.setArguments(bundle);
        mFragmentList.add(fragment);

        bundle = new Bundle();
        bundle.putString(AppIntentConstants.BUNDLE_URL, NetworkConstants.TV_SERIES_POPULAR_BASE_URL);
        fragment = new TVSeriesFragment();
        fragment.setArguments(bundle);
        mFragmentList.add(fragment);

        fragment = new FavoriteTVSeriesFragment();
        mFragmentList.add(fragment);
        return mFragmentList;
    }

    @Override
    public String[] getSearchAndToolbarTitle() {
        return new String[]{"Ongoing TV Series", "Popular TV Series", "My Favourites", "Search for TV Series..."};
    }

    @Override
    public int[] getTabDrawableIcon() {
        return new int[]{
                R.drawable.vector_flame,
                R.drawable.vector_popular,
                R.drawable.vector_favorite
        };
    }


    //-----------------------------------------------------------------------------------------------------
    //          SEARCH VIEW QUERY HANDLER
    //-----------------------------------------------------------------------------------------------------
    @Override
    public boolean onQueryTextSubmit(String query) {
        startActivity(TVSeriesSearchActivity.getActivityIntent(getContext(), query));
        return true;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        if (!super.mIsDataLoading) {
            super.mIsDataLoading = true;
            try {
                VolleySingleton.getInstance(
                        getContext())
                        .getVolleyRequestQueue()
                        .add(new CustomJsonObjectRequest(
                                Request.Method.GET,
                                NetworkConstants.TV_SERIES_SEARCH_BASE_URL.replaceFirst("query_string", URLEncoder.encode(newText, "utf-8")),
                                this,
                                this,
                                this));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
