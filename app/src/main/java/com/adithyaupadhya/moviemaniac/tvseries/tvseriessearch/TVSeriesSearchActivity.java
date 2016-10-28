package com.adithyaupadhya.moviemaniac.tvseries.tvseriessearch;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.adithyaupadhya.moviemaniac.base.AbstractListFragment;
import com.adithyaupadhya.moviemaniac.base.AbstractSearchActivity;
import com.adithyaupadhya.moviemaniac.tvseries.tvseries.TVSeriesFragment;
import com.adithyaupadhya.newtorkmodule.volley.constants.AppIntentConstants;

/**
 * Created by adithya.upadhya on 12-02-2016.
 */
public class TVSeriesSearchActivity extends AbstractSearchActivity {
    public static Intent getActivityIntent(Context context, String searchQuery) {
        Intent intent = new Intent(context, TVSeriesSearchActivity.class);
        intent.putExtra(AppIntentConstants.QUERY_STRING, searchQuery);
        return intent;
    }


    @Override
    protected AbstractListFragment.NetworkAPI getNetworkApiType() {
        return AbstractListFragment.NetworkAPI.API_SEARCH_TV;
    }

    @Override
    protected Fragment getLaunchFragment() {
        return new TVSeriesFragment();
    }

    @Override
    protected String getToolbarTitle() {
        return "TVSeries search results";
    }
}
