package com.adithyaupadhya.moviemaniac.zmainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.adithyaupadhya.database.DBConstants;
import com.adithyaupadhya.database.sharedpref.AppPreferenceManager;
import com.adithyaupadhya.moviemaniac.BuildConfig;
import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.Utils;
import com.adithyaupadhya.moviemaniac.login.SignInActivity;
import com.adithyaupadhya.moviemaniac.navdrawer.NavigationActivity;
import com.adithyaupadhya.newtorkmodule.volley.constants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBGenreResponse;
import com.adithyaupadhya.newtorkmodule.volley.retrofit.RetrofitClient;
import com.adithyaupadhya.newtorkmodule.volley.retrofit.networkwrappers.CallbackWrapper;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.FacebookSdk;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.squareup.leakcanary.LeakCanary;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;

public class AppMainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean mMovieGenreLoaded, mTvGenreLoaded;
    private AppPreferenceManager mPrefManager;
    private RetrofitClient.APIClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_main);

        LeakCanary.install(this.getApplication());

        Fabric.with(this, new Crashlytics.Builder()
                .core(new CrashlyticsCore
                        .Builder()
                        .disabled(BuildConfig.DEBUG)
                        .build())
                .build());

        mApiClient = RetrofitClient.getInstance().getNetworkClient();
        //AlarmManagerUtils.createAlarmInstance(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

        mPrefManager = AppPreferenceManager.getAppPreferenceInstance(this);

        establishNetworkCall();

    }


    private void establishNetworkCall() {

        mApiClient.getMovieGenreList().enqueue(movieGenreResponse);

        mApiClient.getTVGenreList().enqueue(tvGenreResponse);
    }


    private void launchNewActivity() {

        //  FIRST TIME USER: DIRECT HIM/HER TO SIGN IN ACTIVITY
        if (mPrefManager.getPreferenceData(DBConstants.USER_ID) == null) {
            startActivity(new Intent(AppMainActivity.this, SignInActivity.class));
        }
        //  APP VISITED BEFORE: DIRECT HIM/HER TO NAVIGATION ACTIVITY
        else {
            // Crashlytics user logging
            if (Fabric.isInitialized()) {
                Crashlytics.setUserName(mPrefManager.getPreferenceData(DBConstants.USER_NAME));

                Crashlytics.setUserEmail(mPrefManager.getPreferenceData(DBConstants.USER_EMAIL));
            }

            startActivity(new Intent(AppMainActivity.this, NavigationActivity.class));
        }

        overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);

    }


    @Override
    public void onClick(View v) {
        mTvGenreLoaded = mMovieGenreLoaded = false;
        establishNetworkCall();
    }

    private void proceedIfBothItemsLoaded() {
        synchronized (this) {
            if (mMovieGenreLoaded && mTvGenreLoaded) {
                mMovieGenreLoaded = mTvGenreLoaded = false;
                launchNewActivity();
            }
        }
    }


    private Callback<TMDBGenreResponse> movieGenreResponse = new CallbackWrapper<TMDBGenreResponse>() {
        @Override
        public void onNetworkResponse(Call<TMDBGenreResponse> call, retrofit2.Response<TMDBGenreResponse> response) {
            HashMap<Integer, String> movieMap = new HashMap<>(15);

            //SparseArray<String> movieMap = new SparseArray<>(10);

            for (TMDBGenreResponse.Genres genres : response.body().genres)
                movieMap.put(genres.id, genres.name);

            APIConstants.getInstance().setMovieGenreMap(movieMap);
            mMovieGenreLoaded = true;

            try {
                if (mPrefManager.getPreferenceData(DBConstants.MOVIE_GENRE_CACHE) == null) {
                    mPrefManager.setPreferenceData(DBConstants.MOVIE_GENRE_CACHE,
                            APIConstants.getInstance().getObjectMapper().writeValueAsString(movieMap));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


            proceedIfBothItemsLoaded();
        }

        @Override
        public void onFailure(Call<TMDBGenreResponse> call, Throwable t) {
            Utils.displayNetworkErrorSnackBar(findViewById(android.R.id.content), AppMainActivity.this);
        }
    };

    private Callback<TMDBGenreResponse> tvGenreResponse = new CallbackWrapper<TMDBGenreResponse>() {
        @Override
        public void onNetworkResponse(Call<TMDBGenreResponse> call, retrofit2.Response<TMDBGenreResponse> response) {

            HashMap<Integer, String> tvMap = new HashMap<>(15);
            //SparseArray<String> tvMap = new SparseArray<>(10);

            for (TMDBGenreResponse.Genres genres : response.body().genres)
                tvMap.put(genres.id, genres.name);

            APIConstants.getInstance().setTvGenreMap(tvMap);
            mTvGenreLoaded = true;


            try {
                if (mPrefManager.getPreferenceData(DBConstants.TV_GENRE_CACHE) == null) {
                    mPrefManager.setPreferenceData(DBConstants.TV_GENRE_CACHE,
                            APIConstants.getInstance().getObjectMapper().writeValueAsString(tvMap));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


            proceedIfBothItemsLoaded();
        }

        @Override
        public void onFailure(Call<TMDBGenreResponse> call, Throwable t) {
            Utils.displayNetworkErrorSnackBar(findViewById(android.R.id.content), AppMainActivity.this);
        }
    };

}
