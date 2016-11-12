
package com.adithyaupadhya.moviemaniac.movies;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.AbstractListFragment;
import com.adithyaupadhya.moviemaniac.base.AbstractTabFragment;
import com.adithyaupadhya.moviemaniac.movies.favoritemovies.FavoriteMoviesFragment;
import com.adithyaupadhya.moviemaniac.movies.movies.MoviesFragment;
import com.adithyaupadhya.moviemaniac.movies.moviesearch.MovieSearchActivity;
import com.adithyaupadhya.newtorkmodule.volley.constants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.retrofit.RetrofitClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
        List<Fragment> mFragmentList = new ArrayList<>(3);
        Fragment fragment;

        Bundle bundle = new Bundle();
        bundle.putSerializable(AppIntentConstants.API_REQUEST, AbstractListFragment.NetworkAPI.API_UPCOMING_MOVIES);
        fragment = new MoviesFragment();
        fragment.setArguments(bundle);
        mFragmentList.add(fragment);

        bundle = new Bundle();
        bundle.putSerializable(AppIntentConstants.API_REQUEST, AbstractListFragment.NetworkAPI.API_POPULAR_MOVIES);
        fragment = new MoviesFragment();
        fragment.setArguments(bundle);
        mFragmentList.add(fragment);

        fragment = new FavoriteMoviesFragment();
        mFragmentList.add(fragment);
        return mFragmentList;
    }

    @Override
    public String[] getSearchAndToolbarTitle() {
        return getResources().getStringArray(R.array.movie_toolbar_hint_array);
    }

    @Override
    public int[] getTabDrawableIcon() {
        return new int[]{R.drawable.vector_upcoming,
                R.drawable.vector_popular,
                R.drawable.vector_favorite};
    }


    //-----------------------------------------------------------------------------------------------------
    //          SEARCH VIEW QUERY HANDLER
    //-----------------------------------------------------------------------------------------------------
    @Override
    public boolean onQueryTextSubmit(String query) {
        startActivity(MovieSearchActivity.getActivityIntent(getContext(), query));
        return true;
    }


    @Override
    public boolean onQueryTextChange(String newText) {

        try {
            RetrofitClient.getInstance()
                    .getNetworkClient()
                    .getMovieSearchSuggestions(URLEncoder.encode(newText, "utf-8"), 1)
                    .enqueue(this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

}
