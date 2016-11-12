package com.adithyaupadhya.moviemaniac.games;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.Utils;
import com.adithyaupadhya.newtorkmodule.volley.constants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBGenericGameResponse;
import com.adithyaupadhya.newtorkmodule.volley.retrofit.RetrofitClient;
import com.adithyaupadhya.newtorkmodule.volley.retrofit.networkwrappers.CallbackWrapper;
import com.adithyaupadhya.uimodule.roundcornerprogressbar.RoundCornerProgressBar;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameSplashScreenActivity extends AppCompatActivity implements View.OnClickListener,
        MaterialDialog.SingleButtonCallback {

    private static final int TOTAL_REQUESTS = 6;
    private int mResponseCount, randomPage1, randomPage2;
    private Intent mIntent;
    private RetrofitClient.APIClient mApiClient;

    private List<Integer> mPendingList = Collections.synchronizedList(new ArrayList<Integer>(TOTAL_REQUESTS));
    private List<TMDBGenericGameResponse.Results> mMoviesList = Collections.synchronizedList(new ArrayList<TMDBGenericGameResponse.Results>(40));
    private List<TMDBGenericGameResponse.Results> mTVList = Collections.synchronizedList(new ArrayList<TMDBGenericGameResponse.Results>(40));
    private List<TMDBGenericGameResponse.Results> mCelebritiesList = Collections.synchronizedList(new ArrayList<TMDBGenericGameResponse.Results>(40));

    private RoundCornerProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_splash_screen);
        mProgressBar = (RoundCornerProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.buttonBeginGame).setOnClickListener(this);

        mApiClient = RetrofitClient.getInstance().getNetworkClient();

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

        mApiClient.getGameMovies(randomPage1).enqueue(movieHandlerOne);

        mApiClient.getGameMovies(randomPage2).enqueue(movieHandlerTwo);

        mApiClient.getGameTVSeries(randomPage1).enqueue(tvHandlerThree);

        mApiClient.getGameTVSeries(randomPage2).enqueue(tvHandlerFour);

        mApiClient.getGameCelebrities(randomPage1).enqueue(celebrityHandlerFive);

        mApiClient.getGameCelebrities(randomPage2).enqueue(celebrityHandlerSix);

    }

    private void executePendingRequests(List<Integer> pendingList) {

        mPendingList.clear();

        for (int index : pendingList) {
            switch (index) {
                case 0:
                    mApiClient.getGameMovies(randomPage1).enqueue(movieHandlerOne);
                    break;
                case 1:
                    mApiClient.getGameMovies(randomPage2).enqueue(movieHandlerTwo);
                    break;
                case 2:
                    mApiClient.getGameTVSeries(randomPage1).enqueue(tvHandlerThree);
                    break;
                case 3:
                    mApiClient.getGameTVSeries(randomPage2).enqueue(tvHandlerFour);
                    break;
                case 4:
                    mApiClient.getGameCelebrities(randomPage1).enqueue(celebrityHandlerFive);
                    break;
                case 5:
                    mApiClient.getGameCelebrities(randomPage2).enqueue(celebrityHandlerSix);
                    break;
            }
        }
    }

    private void handleSuccessOrNetworkFailure() {
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

        }
    }


    @Override
    public void onBackPressed() {
        Utils.showGenericMaterialDialog(this, this, R.string.dialog_game_exit_title, R.string.dialog_game_exit_content);
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

                executePendingRequests(new ArrayList<>(mPendingList));

                break;

            case R.id.buttonBeginGame:
                startActivity(mIntent);
                finish();
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        RetrofitClient.getInstance().cancelAllRequests();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        mPendingList = null;
//
//        mMoviesList = mTVList = mCelebritiesList = null;
//
//    }


    //-----------------------------------------------------------------------------------------------------------

    private Callback<TMDBGenericGameResponse> movieHandlerOne = new CallbackWrapper<TMDBGenericGameResponse>() {
        @Override
        public void onNetworkResponse(Call<TMDBGenericGameResponse> call, Response<TMDBGenericGameResponse> response) {
            mMoviesList.addAll(response.body().results);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();

            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
        }

        @Override
        public void onNetworkFailure(Call<TMDBGenericGameResponse> call, Throwable t) {
            mPendingList.add(0);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }
    };


    private Callback<TMDBGenericGameResponse> movieHandlerTwo = new CallbackWrapper<TMDBGenericGameResponse>() {

        @Override
        public void onNetworkResponse(Call<TMDBGenericGameResponse> call, Response<TMDBGenericGameResponse> response) {

            mMoviesList.addAll(response.body().results);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();

            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
        }

        @Override
        public void onNetworkFailure(Call<TMDBGenericGameResponse> call, Throwable t) {
            mPendingList.add(1);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }
    };


    private Callback<TMDBGenericGameResponse> tvHandlerThree = new CallbackWrapper<TMDBGenericGameResponse>() {

        @Override
        public void onNetworkResponse(Call<TMDBGenericGameResponse> call, Response<TMDBGenericGameResponse> response) {
            mTVList.addAll(response.body().results);


            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();

            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
        }

        @Override
        public void onNetworkFailure(Call<TMDBGenericGameResponse> call, Throwable t) {
            mPendingList.add(2);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }

    };


    private Callback<TMDBGenericGameResponse> tvHandlerFour = new CallbackWrapper<TMDBGenericGameResponse>() {

        @Override
        public void onNetworkResponse(Call<TMDBGenericGameResponse> call, Response<TMDBGenericGameResponse> response) {

            mTVList.addAll(response.body().results);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();


            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
        }

        @Override
        public void onNetworkFailure(Call<TMDBGenericGameResponse> call, Throwable t) {
            mPendingList.add(3);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }
    };


    private Callback<TMDBGenericGameResponse> celebrityHandlerFive = new CallbackWrapper<TMDBGenericGameResponse>() {

        @Override
        public void onNetworkResponse(Call<TMDBGenericGameResponse> call, Response<TMDBGenericGameResponse> response) {
            mCelebritiesList.addAll(response.body().results);


            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();


            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
        }

        @Override
        public void onNetworkFailure(Call<TMDBGenericGameResponse> call, Throwable t) {
            mPendingList.add(4);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }

    };


    private Callback<TMDBGenericGameResponse> celebrityHandlerSix = new CallbackWrapper<TMDBGenericGameResponse>() {

        @Override
        public void onNetworkResponse(Call<TMDBGenericGameResponse> call, Response<TMDBGenericGameResponse> response) {

            mCelebritiesList.addAll(response.body().results);


            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();

            mProgressBar.setSynchronizedProgress(mProgressBar.getProgress() + 15);
        }

        @Override
        public void onNetworkFailure(Call<TMDBGenericGameResponse> call, Throwable t) {
            mPendingList.add(5);

            if (++mResponseCount >= TOTAL_REQUESTS)
                handleSuccessOrNetworkFailure();
        }

    };

    //-----------------------------------------------------------------------------------------------------------

}
