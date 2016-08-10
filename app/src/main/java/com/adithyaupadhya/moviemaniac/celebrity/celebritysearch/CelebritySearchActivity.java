package com.adithyaupadhya.moviemaniac.celebrity.celebritysearch;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.adithyaupadhya.moviemaniac.base.AbstractSearchActivity;
import com.adithyaupadhya.moviemaniac.celebrity.celebritylist.CelebritiesFragment;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;

/**
 * Created by adithya.upadhya on 12-02-2016.
 */
public class CelebritySearchActivity extends AbstractSearchActivity {
    public static Intent getActivityIntent(Context context, String searchQuery) {
        Intent intent = new Intent(context, CelebritySearchActivity.class);
        intent.putExtra(AppIntentConstants.QUERY_STRING, searchQuery);
        return intent;
    }

    @Override
    protected String getNetworkBaseUrl() {
        return NetworkConstants.CELEBRITY_SEARCH_BASE_URL;
    }

    @Override
    protected Fragment getLaunchFragment() {
        return new CelebritiesFragment();
    }

    @Override
    protected String getToolbarTitle() {
        return "Celebrity Search Results";
    }
}
