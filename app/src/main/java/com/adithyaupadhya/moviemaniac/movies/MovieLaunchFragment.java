
package com.adithyaupadhya.moviemaniac.movies;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.AbstractTabFragment;
import com.adithyaupadhya.moviemaniac.movies.favoritemovies.FavoriteMoviesFragment;
import com.adithyaupadhya.moviemaniac.movies.movies.MoviesFragment;
import com.adithyaupadhya.moviemaniac.movies.moviesearch.MovieSearchActivity;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieLaunchFragment extends AbstractTabFragment {

    public MovieLaunchFragment() {
        // Required empty public constructor
    }


    @Override
    public List<Fragment> getViewPagerFragmentList() {
        List<Fragment> mFragmentList = new ArrayList<>();
        Fragment fragment;

        Bundle bundle = new Bundle();
        bundle.putString(AppIntentConstants.BUNDLE_URL, NetworkConstants.UPCOMING_MOVIES_BASE_URL);
        fragment = new MoviesFragment();
        fragment.setArguments(bundle);
        mFragmentList.add(fragment);

        bundle = new Bundle();
        bundle.putString(AppIntentConstants.BUNDLE_URL, NetworkConstants.MOVIES_POPULAR_BASE_URL);
        fragment = new MoviesFragment();
        fragment.setArguments(bundle);
        mFragmentList.add(fragment);

        fragment = new FavoriteMoviesFragment();
        mFragmentList.add(fragment);
        return mFragmentList;
    }

    @Override
    public String[] getSearchAndToolbarTitle() {
        return new String[]{"Upcoming Movies", "Popular Movies", "My Favourites", "Search for movies..."};
    }

    @Override
    public int[] getTabDrawableIcon() {
        return new int[]{R.drawable.vector_upcoming,
                R.drawable.vector_popular,
                R.drawable.vector_favorite};
    }

    @Override
    public void searchQuerySubmission(String searchQuery) {
        startActivity(MovieSearchActivity.getActivityIntent(getContext(), searchQuery));
    }

}
