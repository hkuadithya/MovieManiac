package com.adithyaupadhya.moviemaniac.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import com.adithyaupadhya.moviemaniac.base.interfaces.OnLoadMoreListener;
import com.adithyaupadhya.newtorkmodule.volley.constants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.retrofit.RetrofitClient;

import retrofit2.Callback;

/**
 * Created by adithya.upadhya on 21-01-2016.
 */
public abstract class AbstractListFragment<APIResponseClass> extends Fragment implements
        AbstractTabFragment.OnFabSnackBarClickListener,
        OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener, Callback<APIResponseClass> {

    protected NetworkAPI mApiType;
    protected RetrofitClient.APIClient mApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiType = (NetworkAPI) getArguments().getSerializable(AppIntentConstants.API_REQUEST);
        mApiClient = RetrofitClient.getInstance().getNetworkClient();
    }

    public enum NetworkAPI {
        API_UPCOMING_MOVIES,
        API_POPULAR_MOVIES,
        API_SEARCH_MOVIE,

        API_ON_THE_AIR_TV,
        API_POPULAR_TV,
        API_SEARCH_TV,

        API_POPULAR_CELEBRITY,
        API_SEARCH_CELEBRITY
    }
}
