package com.adithyaupadhya.moviemaniac.movies.moviesearch;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.adithyaupadhya.moviemaniac.base.AbstractSearchActivity;
import com.adithyaupadhya.moviemaniac.movies.movies.MoviesFragment;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;

public class MovieSearchActivity extends AbstractSearchActivity {

    public static Intent getActivityIntent(Context context, String searchQuery) {
        Intent intent = new Intent(context, MovieSearchActivity.class);
        intent.putExtra(AppIntentConstants.QUERY_STRING, searchQuery);
        return intent;
    }

    @Override
    protected String getNetworkBaseUrl() {
        return NetworkConstants.MOVIE_SEARCH_BASE_URL;
    }

    @Override
    protected Fragment getLaunchFragment() {
        return new MoviesFragment();
    }

    @Override
    protected String getToolbarTitle() {
        return "Movie Search Results";
    }
}
