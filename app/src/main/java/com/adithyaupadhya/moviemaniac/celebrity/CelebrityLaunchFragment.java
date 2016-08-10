package com.adithyaupadhya.moviemaniac.celebrity;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.AbstractTabFragment;
import com.adithyaupadhya.moviemaniac.celebrity.celebritylist.CelebritiesFragment;
import com.adithyaupadhya.moviemaniac.celebrity.celebritysearch.CelebritySearchActivity;
import com.adithyaupadhya.moviemaniac.celebrity.favoritecelebrities.FavoriteCelebritiesFragment;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;

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
        List<Fragment> mFragmentList = new ArrayList<>();
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

    @Override
    public void searchQuerySubmission(String searchQuery) {
        startActivity(CelebritySearchActivity.getActivityIntent(getContext(), searchQuery));
    }
}
