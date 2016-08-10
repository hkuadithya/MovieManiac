package com.adithyaupadhya.moviemaniac.games;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.Utils;
import com.adithyaupadhya.newtorkmodule.volley.VolleyNetworkListener;
import com.adithyaupadhya.newtorkmodule.volley.VolleySingleton;
import com.adithyaupadhya.newtorkmodule.volley.customjsonrequest.CustomJsonObjectRequest;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBGenericGameResponse;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.adithyaupadhya.uimodule.roundcornerprogressbar.RoundCornerProgressBar;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameSplashScreenActivity extends AppCompatActivity implements View.OnClickListener, MaterialDialog.SingleButtonCallback {
    private static final int TOTAL_REQUESTS = 6;
    private RequestQueue mRequestQueue;
    private int mResponseCount, randomPage1, randomPage2;
    private Intent mIntent;

    private List<Integer> mPendingList = Collections.synchronizedList(new ArrayList<Integer>(TOTAL_REQUESTS));
    private List<TMDBGenericGameResponse.Results> mMoviesList = Collections.synchronizedList(new ArrayList<TMDBGenericGameResponse.Results>(40));
    private List<TMDBGenericGameResponse.Results> mTVList = Collections.synchronizedList(new ArrayList<TMDBGenericGameResponse.Results>(40));
    private List<TMDBGenericGameResponse.Results> mCelebritiesList = Collections.synchronizedList(new ArrayList<TMDBGenericGameResponse.Results>(40));

    private RoundCornerProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_splash_screen);
        mRequestQueue = VolleySingleton.getInstance(this).getVolleyRequestQueue();
        mProgressBar = (RoundCornerProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.buttonBeginGame).setOnClickListener(this);

        Random random = new Random();
        randomPage1 = random.nextInt(10) + 1;
        randomPage2 = random.nextInt(10) + 1;

        while (randomPage2 == randomPage1)
            randomPage2 = random.nextInt(10) + 1;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mResponseCount < TOTAL_REQUESTS) {
            mResponseCount = 0;
            mProgressBar.setProgress(10);
            mMoviesList.clear();
            mTVList.clear();
            mCelebritiesList.clear();
            mPendingList.clear();

            executeGameRequests();
        }
    }

    private void executeGameRequests() {

        // Log.d("MMVOLLEY", randomPage1 + " " + randomPage2);

        mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.MOVIES_POPULAR_BASE_URL + randomPage1, this, movieHandlerOne, movieHandlerOne));

        mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.MOVIES_POPULAR_BASE_URL + randomPage2, this, movieHandlerTwo, movieHandlerTwo));

        mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.TV_SERIES_POPULAR_BASE_URL + randomPage1, this, tvHandlerThree, tvHandlerThree));

        mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.TV_SERIES_POPULAR_BASE_URL + randomPage2, this, tvHandlerFour, tvHandlerFour));

        mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.CELEBRITY_POPULAR_BASE_URL + randomPage1, this, celebrityHandlerFive, celebrityHandlerFive));

        mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.CELEBRITY_POPULAR_BASE_URL + randomPage2, this, celebrityHandlerSix, celebrityHandlerSix));

    }

    private void executePendingRequests() {
        for (int index : mPendingList) {
            switch (index) {
                case 0:
                    mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.MOVIES_POPULAR_BASE_URL + randomPage1, this, movieHandlerOne, movieHandlerOne));
                    break;
                case 1:
                    mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.MOVIES_POPULAR_BASE_URL + randomPage2, this, movieHandlerTwo, movieHandlerTwo));
                    break;
                case 2:
                    mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.TV_SERIES_POPULAR_BASE_URL + randomPage1, this, tvHandlerThree, tvHandlerThree));
                    break;
                case 3:
                    mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.TV_SERIES_POPULAR_BASE_URL + randomPage2, this, tvHandlerFour, tvHandlerFour));
                    break;
                case 4:
                    mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.CELEBRITY_POPULAR_BASE_URL + randomPage1, this, celebrityHandlerFive, celebrityHandlerFive));
                    break;
                case 5:
                    mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, NetworkConstants.CELEBRITY_POPULAR_BASE_URL + randomPage2, this, celebrityHandlerSix, celebrityHandlerSix));
                    break;
            }
        }
    }

    public void handleSuccessOrNetworkFailure() {
        if (mPendingList.size() > 0) {
            //  PENDING ITEMS FOUND
            Utils.displayNetworkErrorSnackBar(findViewById(android.R.id.content), this);
        } else {
            //  ALL NETWORK CALLS COMPLETE

            Collections.shuffle(mMoviesList);
            Collections.shuffle(mTVList);
            Collections.shuffle(mCelebritiesList);

            mIntent = new Intent(this, GameActivity.class);
            mIntent.putParcelableArrayListExtra(AppIntentConstants.MOVIE_LIST, new ArrayList<>(mMoviesList.subList(0, 15)));
            mIntent.putParcelableArrayListExtra(AppIntentConstants.TV_LIST, new ArrayList<>(mTVList.subList(0, 15)));
            mIntent.putParcelableArrayListExtra(AppIntentConstants.CELEBRITY_LIST, new ArrayList<>(mCelebritiesList.subList(0, 15)));
            mIntent.putExtra(AppIntentConstants.CURRENT_INDEX, 0);

            findViewById(R.id.textViewLoading).setVisibility(View.GONE);

            findViewById(R.id.buttonBeginGame).setVisibility(View.VISIBLE);

//            try {
//                Log.d("MMVOLLEY", APIConstants.getInstance().getJacksonObjectMapper().writeValueAsString(mMoviesList));
//                Log.d("MMVOLLEY", APIConstants.getInstance().getJacksonObjectMapper().writeValueAsString(mTVList));
//                Log.d("MMVOLLEY", APIConstants.getInstance().getJacksonObjectMapper().writeValueAsString(mCelebritiesList));
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRequestQueue.cancelAll(this);
    }

    @Override
    public void onBackPressed() {
        Utils.showGenericMaterilaDialog(this, this, "Game Exit confirmation!", "Do you wish to exit this game?");
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        // Handle Network error :
        switch (v.getId()) {
            case R.id.snackbar_action:
                mResponseCount -= mPendingList.size();
                mRequestQueue.stop();

                executePendingRequests();

                mPendingList.clear();
                mRequestQueue.start();
                break;

            case R.id.buttonBeginGame:
                startActivity(mIntent);
                finish();
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPendingList = null;
        mMoviesList = mTVList = mCelebritiesList = null;
    }

    //-----------------------------------------------------------------------------------------------------------

    private VolleyNetworkListener movieHandlerOne = new VolleyNetworkListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mPendingList.add(0);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }

        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                mMoviesList.addAll(APIConstants.getInstance().getJacksonObjectMapper().readValue(jsonObject.toString(), TMDBGenericGameResponse.class).results);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();

            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
            // Log.d("MMVOLLEY", jsonObject.toString());
        }
    };

    private VolleyNetworkListener movieHandlerTwo = new VolleyNetworkListener() {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mPendingList.add(1);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }

        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                mMoviesList.addAll(APIConstants.getInstance().getJacksonObjectMapper().readValue(jsonObject.toString(), TMDBGenericGameResponse.class).results);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();

            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
            // Log.d("MMVOLLEY", jsonObject.toString());

        }
    };

    private VolleyNetworkListener tvHandlerThree = new VolleyNetworkListener() {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mPendingList.add(2);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }

        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                mTVList.addAll(APIConstants.getInstance().getJacksonObjectMapper().readValue(jsonObject.toString(), TMDBGenericGameResponse.class).results);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();

            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
            // Log.d("MMVOLLEY", jsonObject.toString());

        }
    };

    private VolleyNetworkListener tvHandlerFour = new VolleyNetworkListener() {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mPendingList.add(3);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }

        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                mTVList.addAll(APIConstants.getInstance().getJacksonObjectMapper().readValue(jsonObject.toString(), TMDBGenericGameResponse.class).results);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();


            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
            // Log.d("MMVOLLEY", jsonObject.toString());

        }
    };

    private VolleyNetworkListener celebrityHandlerFive = new VolleyNetworkListener() {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mPendingList.add(4);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }

        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                mCelebritiesList.addAll(APIConstants.getInstance().getJacksonObjectMapper().readValue(jsonObject.toString(), TMDBGenericGameResponse.class).results);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();


            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
            // Log.d("MMVOLLEY", jsonObject.toString());

        }
    };

    private VolleyNetworkListener celebrityHandlerSix = new VolleyNetworkListener() {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mPendingList.add(5);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }

        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                mCelebritiesList.addAll(APIConstants.getInstance().getJacksonObjectMapper().readValue(jsonObject.toString(), TMDBGenericGameResponse.class).results);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();

            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
            // Log.d("MMVOLLEY", jsonObject.toString());

        }
    };

    //-----------------------------------------------------------------------------------------------------------

}
