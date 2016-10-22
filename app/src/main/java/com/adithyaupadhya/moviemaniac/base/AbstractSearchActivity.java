package com.adithyaupadhya.moviemaniac.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by adithya.upadhya on 12-02-2016.
 */
public abstract class AbstractSearchActivity extends AppCompatActivity implements View.OnClickListener {
    private AbstractTabFragment.OnFabSnackBarClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        setTitle(getToolbarTitle());

        findViewById(R.id.fab).setOnClickListener(this);

        String searchQuery = getIntent().getStringExtra(AppIntentConstants.QUERY_STRING);

        if (savedInstanceState == null) {
            Fragment fragment = getLaunchFragment();
            Bundle bundle = new Bundle();
            try {
                String url = getNetworkBaseUrl().replaceFirst("query_string", URLEncoder.encode(searchQuery, "utf-8"));
                // Log.d("MMVOLLEY", url);
                bundle.putString(AppIntentConstants.BUNDLE_URL, url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    protected abstract String getNetworkBaseUrl();

    protected abstract Fragment getLaunchFragment();

    protected abstract String getToolbarTitle();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof AbstractTabFragment.OnFabSnackBarClickListener)
            listener = (AbstractTabFragment.OnFabSnackBarClickListener) fragment;
    }

    @Override
    public void onClick(View v) {
        listener.onFabSnackBarClick(v.getId() == R.id.fab);
    }
}
