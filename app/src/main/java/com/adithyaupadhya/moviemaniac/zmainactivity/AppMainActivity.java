package com.adithyaupadhya.moviemaniac.zmainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.adithyaupadhya.database.DBConstants;
import com.adithyaupadhya.database.sharedpref.AppPreferenceManager;
import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.Utils;
import com.adithyaupadhya.moviemaniac.login.SignInActivity;
import com.adithyaupadhya.moviemaniac.navdrawer.NavigationActivity;
import com.adithyaupadhya.newtorkmodule.volley.VolleySingleton;
import com.adithyaupadhya.newtorkmodule.volley.customjsonrequest.CustomJsonObjectRequest;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBGenreResponse;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;

import io.fabric.sdk.android.Fabric;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class AppMainActivity extends AppCompatActivity implements Response.ErrorListener, View.OnClickListener {
    private boolean mMovieGenreLoaded, mTvGenreLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getApplicationContext(), new Crashlytics());

        setContentView(R.layout.activity_app_main);

        VolleySingleton.initInstance(this);

        APIConstants instance = APIConstants.getInstance();

        FacebookSdk.sdkInitialize(getApplicationContext());

        if (instance.areGenreMapsEmpty())
            establishNetworkCall();
        else
            launchNewActivity();

        // LeakCanary.install(this.getApplication());
    }


    private void establishNetworkCall() {
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getVolleyRequestQueue();

        requestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.MOVIE_GENRE_URL, this, new MovieGenreResponse(), this));

        requestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.TV_GENRE_URL, this, new TvGenreResponse(), this));

    }


    public void launchNewActivity() {
        AppPreferenceManager manager = AppPreferenceManager.getAppPreferenceInstance(AppMainActivity.this);

        //  FIRST TIME USER: DIRECT HIM/HER TO SIGN IN ACTIVITY
        if (manager.getPreferenceData(DBConstants.USER_ID) == null) {
            startActivity(new Intent(AppMainActivity.this, SignInActivity.class));
        }
        //  APP VISITED BEFORE: DIRECT HIM/HER TO NAVIGATION ACTIVITY
        else {
            startActivity(new Intent(AppMainActivity.this, NavigationActivity.class));
        }

        overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);

    }


    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Utils.displayNetworkErrorSnackBar(findViewById(android.R.id.content), this);
    }


    @Override
    public void onClick(View v) {
        mTvGenreLoaded = mMovieGenreLoaded = false;
        establishNetworkCall();
    }

    private synchronized void proceedIfBothItemsLoaded() {
        if (mMovieGenreLoaded && mTvGenreLoaded)
            launchNewActivity();
    }

    private class MovieGenreResponse implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                HashMap<Integer, String> movieMap = new HashMap<>();
                TMDBGenreResponse response = APIConstants.getInstance()
                        .getJacksonObjectMapper()
                        .readValue(jsonObject.toString(), TMDBGenreResponse.class);

                for (TMDBGenreResponse.Genres genres : response.genres)
                    movieMap.put(genres.id, genres.name);

                APIConstants.getInstance().setMovieGenreMap(movieMap);
                mMovieGenreLoaded = true;
                proceedIfBothItemsLoaded();
                // Log.d("MMVOLLEY", jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class TvGenreResponse implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                HashMap<Integer, String> tvMap = new HashMap<>();
                TMDBGenreResponse response = APIConstants.getInstance()
                        .getJacksonObjectMapper()
                        .readValue(jsonObject.toString(), TMDBGenreResponse.class);

                for (TMDBGenreResponse.Genres genres : response.genres)
                    tvMap.put(genres.id, genres.name);

                APIConstants.getInstance().setTvGenreMap(tvMap);
                mTvGenreLoaded = true;
                proceedIfBothItemsLoaded();
                // Log.d("MMVOLLEY", jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
