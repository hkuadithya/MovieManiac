package com.adithyaupadhya.moviemaniac.celebrity;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.AbstractTabFragment;
import com.adithyaupadhya.moviemaniac.celebrity.celebritylist.CelebritiesFragment;
import com.adithyaupadhya.moviemaniac.celebrity.celebritysearch.CelebritySearchActivity;
import com.adithyaupadhya.moviemaniac.celebrity.favoritecelebrities.FavoriteCelebritiesFragment;
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
public class CelebrityLaunchFragment extends AbstractTabFragment {

    public CelebrityLaunchFragment() {
        // Required empty public constructor
    }

    @Override
    public List<Fragment> getViewPagerFragmentList() {
        List<Fragment> mFragmentList = new ArrayList<>(2);
        Fragment fragment;

        Bundle bundle = new Bundle();
        bundle.putString(AppIntentConstants.BUNDLE_URL, NetworkConstants.CELEBRITY_POPULAR_BASE_URL);
        fragment = new CelebritiesFragment();
        fragment.setArguments(bundle);
        mFragmentList.add(fragment);

        fragment = new FavoriteCelebritiesFragment();
        mFragmentList.add(fragment);
        return mFragmentList;
    }

    @Override
    public String[] getSearchAndToolbarTitle() {
        return new String[]{"Popular Celebrities", "My Favourites", "Search for celebrities..."};
    }

    @Override
    public int[] getTabDrawableIcon() {
        return new int[]{R.drawable.vector_popular, R.drawable.vector_favorite};
    }

    //-----------------------------------------------------------------------------------------------------
    //          SEARCH VIEW QUERY HANDLER
    //-----------------------------------------------------------------------------------------------------
    @Override
    public boolean onQueryTextSubmit(String query) {
        startActivity(CelebritySearchActivity.getActivityIntent(getContext(), query));
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
                                NetworkConstants.CELEBRITY_SEARCH_BASE_URL.replaceFirst("query_string", URLEncoder.encode(newText, "utf-8")),
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
