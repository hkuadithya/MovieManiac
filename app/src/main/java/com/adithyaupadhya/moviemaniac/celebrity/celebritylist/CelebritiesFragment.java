package com.adithyaupadhya.moviemaniac.celebrity.celebritylist;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.AbstractListFragment;
import com.adithyaupadhya.moviemaniac.base.AbstractSearchActivity;
import com.adithyaupadhya.moviemaniac.base.AbstractTabFragment;
import com.adithyaupadhya.moviemaniac.base.Utils;
import com.adithyaupadhya.newtorkmodule.volley.constants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBCelebrityResponse;
import com.adithyaupadhya.uimodule.materialprogress.ProgressWheel;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CelebritiesFragment extends AbstractListFragment<TMDBCelebrityResponse> {
    private RecyclerView mRecyclerView;
    private CelebritiesAdapter mAdapter;
    private ProgressWheel mProgressWheel;
    private View mZeroStateLayout;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mPageNumber;
    private TMDBCelebrityResponse mOldResponse;

    public CelebritiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generic_item_list, container, false);
        mProgressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
        mZeroStateLayout = view.findViewById(R.id.layoutZeroState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(0xFFB71C1C, 0xFF1A237E, 0xFF2E7D32);

        mRecyclerView = (RecyclerView) mSwipeRefreshLayout.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new CelebritiesAdapter(getContext(), mRecyclerView, this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        establishNetworkCall(++mPageNumber);
        return view;
    }

    private void establishNetworkCall(int pageNumber) {
        mProgressWheel.setVisibility(View.VISIBLE);

        switch (mApiType) {
            case API_POPULAR_CELEBRITY:
                mApiClient.getPopularCelebrities(pageNumber).enqueue(this);
                break;

            case API_SEARCH_CELEBRITY:
                mApiClient.getCelebritySearchResults(getArguments().getString(AppIntentConstants.QUERY_STRING), pageNumber).enqueue(this);
                break;

        }
    }

    @Override
    public void onResponse(Call<TMDBCelebrityResponse> call, Response<TMDBCelebrityResponse> response) {
        //  CALLED ON SWIPE TO REFRESH OR FIRST TIME LAUNCH
        if (mOldResponse == null || mPageNumber == 1) {
            mOldResponse = response.body();
            mAdapter.setNewAPIResponse(mOldResponse);
            mAdapter.notifyDataSetChanged();

            if (getActivity() instanceof AbstractSearchActivity) {
                if (mOldResponse.results.size() == 0)
                    mZeroStateLayout.setVisibility(View.VISIBLE);
                else
                    mZeroStateLayout.setVisibility(View.GONE);
            }
        }
        //  HANDLES PAGINATION REQUESTS
        else {
            Toast.makeText(getActivity(), "PAGE " + mPageNumber, Toast.LENGTH_SHORT).show();
            int startIndex = mOldResponse.results.size(), totalItems = response.body().results.size();
            mOldResponse.page = response.body().page;
            mOldResponse.total_results = response.body().total_results;
            mOldResponse.results.addAll(response.body().results);
            mAdapter.setNewAPIResponse(mOldResponse);
            mAdapter.notifyItemRangeInserted(startIndex, totalItems);
        }

        mProgressWheel.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.setLoaded();
    }

    @Override
    public void onFailure(Call<TMDBCelebrityResponse> call, Throwable t) {
        mProgressWheel.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        if (getParentFragment() != null)
            ((AbstractTabFragment) getParentFragment()).showNetworkErrorSnackBar();
        else if (getActivity() instanceof AbstractSearchActivity)
            Utils.displayNetworkErrorSnackBar(getActivity().findViewById(R.id.coordinatorLayout), (AbstractSearchActivity) getActivity());
    }

    @Override
    public void onLoadMore() {
        establishNetworkCall(++mPageNumber);
    }


    @Override
    public void onRefresh() {
        mPageNumber = 0;
        establishNetworkCall(++mPageNumber);
    }

    @Override
    public void onFabSnackBarClick(boolean isFabClicked) {
        if (isFabClicked) {
            if (mRecyclerView != null)
                mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, 0);
        } else {
            establishNetworkCall(mPageNumber != 0 ? mPageNumber : ++mPageNumber);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.setOnLoadMoreListener(null);
    }

}
