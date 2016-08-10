package com.adithyaupadhya.moviemaniac.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.interfaces.OnFragmentBackPress;
import com.adithyaupadhya.moviemaniac.navdrawer.NavigationActivity;
import com.adithyaupadhya.searchmodule.MaterialSearchView;
import com.adithyaupadhya.uimodule.slidingtabs.BaseSlidingTabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by adithya.upadhya on 09-02-2016.
 */
public abstract class AbstractTabFragment extends Fragment implements View.OnClickListener,
        ViewPager.OnPageChangeListener,
        MaterialSearchView.OnQueryTextListener,
        OnFragmentBackPress,
        MaterialSearchView.SearchViewListener {

    private List<Fragment> mFragmentList;
    private Toolbar mToolbar;
    private String[] mToolbarTitle;
    private FloatingActionButton mFab;
    private MaterialSearchView mSearchView;
    private NavigationActivity mParentActivity;
    private WeakHashMap<Integer, Fragment> mFragmentMap;

    public abstract List<Fragment> getViewPagerFragmentList();

    public abstract String[] getSearchAndToolbarTitle();

    public abstract int[] getTabDrawableIcon();

    public abstract void searchQuerySubmission(String searchQuery);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentList = getViewPagerFragmentList();
        mToolbarTitle = getSearchAndToolbarTitle();
        mFragmentMap = new WeakHashMap<>(mFragmentList.size());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_bar_navigation, container, false);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        getActivity().setTitle(mToolbarTitle[0]);
        mSearchView = (MaterialSearchView) view.findViewById(R.id.search_view);
        mSearchView.setVoiceSearch(true, this);
        mSearchView.setHint(mToolbarTitle[mToolbarTitle.length - 1]);

        BaseSlidingTabs slidingTabs = (BaseSlidingTabs) view.findViewById(R.id.slidingTabLayout);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(mFragmentList.size());

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        if (mFab != null) mFab.setOnClickListener(this);

        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        slidingTabs.setOnPageChangeListener(this);
        slidingTabs.setViewPager(viewPager);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSearchViewListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mParentActivity = ((NavigationActivity) getActivity());
        mParentActivity.buildActivityDrawerAndToolbar(mToolbar);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_search, menu);
        mSearchView.setMenuItem(menu.findItem(R.id.action_search));
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    public void showNetworkErrorSnackBar() {
        if (getView() != null)
            Utils.displayNetworkErrorSnackBar(getView(), this);
    }

    //-----------------------------------------------------------------------------------------------------
    //          HANDLE SPEECH RECOGNITION
    //-----------------------------------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Log.d("MMSPEECH", requestCode + " " + resultCode);
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd))
                    mSearchView.setQuery(searchWrd, false);
                else
                    Toast.makeText(getContext(), "No Valid Search String found! please try again...", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //-----------------------------------------------------------------------------------------------------
    //          ON CLICK EVENT HANDLER [ FAB OR SNACKBAR HANDLER ]
    //-----------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        for (Map.Entry<Integer, Fragment> entry : mFragmentMap.entrySet())
            if (entry.getValue() instanceof OnFabSnackBarClickListener)
                ((OnFabSnackBarClickListener) entry.getValue()).onFabSnackBarClick(v.getId() == mFab.getId());
    }


    //-----------------------------------------------------------------------------------------------------
    //          TAB ON PAGE CHANGE LISTENER
    //-----------------------------------------------------------------------------------------------------
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mToolbar.setTitle(mToolbarTitle[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    //-----------------------------------------------------------------------------------------------------
    //          SEARCH VIEW QUERY HANDLER
    //-----------------------------------------------------------------------------------------------------
    @Override
    public boolean onQueryTextSubmit(String query) {
        searchQuerySubmission(query);
        return true;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    //-----------------------------------------------------------------------------------------------------
    //          SEARCH VIEW OPEN AND CLOSE HANDLER
    //-----------------------------------------------------------------------------------------------------
    @Override
    public void onSearchViewShown() {
        mFab.setVisibility(View.INVISIBLE);
        mParentActivity.setOnFragmentBackPressListener(this);
    }

    @Override
    public void onSearchViewClosed() {
        mFab.setVisibility(View.VISIBLE);
        mParentActivity.setOnFragmentBackPressListener(null);
    }

    //-----------------------------------------------------------------------------------------------------
    //          FRAGMENT BACK PRESS HANDLER [ CLOSE THE SEARCH VIEW ]
    //-----------------------------------------------------------------------------------------------------
    @Override
    public void handleFragmentSearchView() {
        if (mSearchView != null && mSearchView.isSearchOpen())
            mSearchView.closeSearch();
    }

    public interface OnFabSnackBarClickListener {
        void onFabSnackBarClick(boolean isFabClicked);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter implements BaseSlidingTabs.IconTabProvider {
        private int[] mDrawableList;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mDrawableList = getTabDrawableIcon();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public int getPageIconResId(int position) {
            return mDrawableList[position];
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mFragmentMap.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mFragmentMap.remove(position);
            super.destroyItem(container, position, object);
        }
    }

}
