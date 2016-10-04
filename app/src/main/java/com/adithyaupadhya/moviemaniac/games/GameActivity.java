package com.adithyaupadhya.moviemaniac.games;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.Utils;
import com.adithyaupadhya.newtorkmodule.volley.VolleySingleton;
import com.adithyaupadhya.newtorkmodule.volley.customjsonrequest.CustomJsonObjectRequest;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBGenericGameResponse;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBImageBackdropResponse;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBImageProfileResponse;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity implements Response.ErrorListener,
        Response.Listener<JSONObject>,
        View.OnClickListener,
        RadioGroup.OnCheckedChangeListener,
        MaterialDialog.SingleButtonCallback,
        RequestListener<String, GlideDrawable> {

    private static final int TOTAL_OPTIONS = 4;
    private static final int TOTAL_QUESTIONS = 9;

    private int currentIndex, offset, successImageCount;

    private ImageView imageView1, imageView2;
    private String url1, url2;
    private RadioGroup mRadioGroupOptions;
    private List<TMDBGenericGameResponse.Results> mDataList;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toast.makeText(GameActivity.this, "Loading Images. Please wait", Toast.LENGTH_SHORT).show();

        currentIndex = getIntent().getIntExtra(AppIntentConstants.CURRENT_INDEX, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView) (toolbar.findViewById(R.id.textViewHeading))).setText("Question " + (currentIndex + 1) + "/9");

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        mRadioGroupOptions = (RadioGroup) findViewById(R.id.radioGroupOptions);

        if (currentIndex < TOTAL_QUESTIONS / 3) {
            ((TextView) (findViewById(R.id.textViewQuestion))).setText("Identify this Movie");
            mDataList = getIntent().getParcelableArrayListExtra(AppIntentConstants.MOVIE_LIST);
            offset = 0;
        } else if (currentIndex < 2 * TOTAL_QUESTIONS / 3) {
            ((TextView) (findViewById(R.id.textViewQuestion))).setText("Identify this TV Series");
            mDataList = getIntent().getParcelableArrayListExtra(AppIntentConstants.TV_LIST);
            offset = TOTAL_QUESTIONS / 3;
        } else {
            ((TextView) (findViewById(R.id.textViewQuestion))).setText("Identify this Celebrity");
            mDataList = getIntent().getParcelableArrayListExtra(AppIntentConstants.CELEBRITY_LIST);
            offset = 2 * TOTAL_QUESTIONS / 3;
        }

        List<String> options = new ArrayList<>(TOTAL_OPTIONS);
        options.add(mDataList.get(currentIndex - offset).name);

        for (int i = 0; i < TOTAL_OPTIONS - 1; i++)
            options.add(mDataList.get(3 * (currentIndex - offset + 1) + i).name);

        Collections.shuffle(options);

        for (int i = 0; i < mRadioGroupOptions.getChildCount(); i++)
            ((RadioButton) mRadioGroupOptions.getChildAt(i)).setText(options.get(i));

    }

    @Override
    protected void onResume() {
        super.onResume();
        successImageCount = 0;
        establishNetworkCall();
        mediaPlayer = MediaPlayer.create(this, R.raw.bell);
    }

    public void establishNetworkCall() {
        String url;
        if (currentIndex < TOTAL_QUESTIONS / 3)
            url = NetworkConstants.MOVIE_IMAGES.replaceFirst("movie_id", String.valueOf(mDataList.get(currentIndex - offset).id));
        else if (currentIndex < 2 * TOTAL_QUESTIONS / 3)
            url = NetworkConstants.TV_SERIES_IMAGES.replaceFirst("tv_id", String.valueOf(mDataList.get(currentIndex - offset).id));
        else
            url = NetworkConstants.CELEBRITY_IMAGES.replaceFirst("person_id", String.valueOf(mDataList.get(currentIndex - offset).id));

        VolleySingleton.getInstance(this).getVolleyRequestQueue().add(new CustomJsonObjectRequest(Request.Method.GET, url, this, this, this));
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        // Log.d("MMVOLLEY", jsonObject.toString());

        List<TMDBImageBackdropResponse.Backdrop> imageList = null;

        String baseImageUrl = null;

        try {
            if (currentIndex < 2 * TOTAL_QUESTIONS / 3) {
                imageList = APIConstants.getInstance().getJacksonObjectMapper().readValue(jsonObject.toString(), TMDBImageBackdropResponse.class).backdrops;

                baseImageUrl = NetworkConstants.IMG_GAME_BACKDROP_URL;
            } else {
                imageList = APIConstants.getInstance().getJacksonObjectMapper().readValue(jsonObject.toString(), TMDBImageProfileResponse.class).profiles;

                baseImageUrl = NetworkConstants.IMG_GAME_POSTER_URL;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (imageList != null && imageList.size() > 0) {
            url1 = baseImageUrl + imageList.get(0).file_path;

            if (imageList.size() > 1)
                url2 = baseImageUrl + imageList.get(1).file_path;
        }

        Glide.with(this)
                .load(url1)
                .error(R.drawable.not_found)
                .listener(this)
                .into(imageView1);


        Glide.with(this)
                .load(url2)
                .error(R.drawable.not_found)
                .listener(this)
                .into(imageView2);

    }

    private void handleSuccessOrErrorImageLoading() {
        synchronized (this) {
            if (++successImageCount == 2) {
                mRadioGroupOptions.clearCheck();
                mRadioGroupOptions.setOnCheckedChangeListener(this);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId > 0) {
            RadioButton radioButtonAnswer = (RadioButton) group.findViewById(checkedId);
            String answer = mDataList.get(currentIndex - offset).name;

            if (radioButtonAnswer.getText().equals(answer)) {
                mediaPlayer.start();

                radioButtonAnswer.setBackgroundColor(ContextCompat.getColor(this, R.color.gameCorrectAnswer));

                int correctAnswers = getIntent().getIntExtra(AppIntentConstants.CORRECT_ANS_COUNT, 0);

                getIntent().putExtra(AppIntentConstants.CORRECT_ANS_COUNT, correctAnswers + 1);
            } else {

                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(80);

                radioButtonAnswer.setBackgroundColor(ContextCompat.getColor(this, R.color.gameWrongAnswer));
            }

            Intent intent;
            if (currentIndex < TOTAL_QUESTIONS - 1) {
                // FIRST TO LAST BUT ONE QUESTION
                intent = new Intent(this, GameActivity.class);

                intent.putExtras(getIntent());

                intent.putExtra(AppIntentConstants.CURRENT_INDEX, getIntent().getIntExtra(AppIntentConstants.CURRENT_INDEX, 0) + 1);
            } else {
                // THIS IS THE LAST QUESTION
                int correctAnswers = getIntent().getIntExtra(AppIntentConstants.CORRECT_ANS_COUNT, 0);

                getIntent().removeExtra(AppIntentConstants.MOVIE_LIST);
                getIntent().removeExtra(AppIntentConstants.TV_LIST);
                getIntent().removeExtra(AppIntentConstants.CELEBRITY_LIST);

                intent = new Intent(this, GameSummaryActivity.class);

                intent.putExtra(AppIntentConstants.CORRECT_ANS_COUNT, correctAnswers);
            }
            startActivity(intent);

            finish();

            overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);

            //mediaPlayer.release();
        }
    }


    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        super.onBackPressed();
        mDataList = null;
        getIntent().getExtras().clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        successImageCount = 0;
        establishNetworkCall();
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleySingleton.getInstance(this).getVolleyRequestQueue().cancelAll(this);
        //mediaPlayer.release();
    }

    @Override
    public void onBackPressed() {
        Utils.showGenericMaterilaDialog(this, this, "Do you wish to exit the game?", "All your game progress will be lost!");
    }


    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Utils.displayNetworkErrorSnackBar(findViewById(android.R.id.content), this);
    }


    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        // Handling connection error
        if (e != null && (e instanceof NoConnectionError || e.getCause() instanceof NoConnectionError || e.getCause() instanceof TimeoutError)) {
            Utils.displayNetworkErrorSnackBar(findViewById(android.R.id.content), this);
            return true;
        }
        handleSuccessOrErrorImageLoading();

        //  Handling invalid URL error

        if (url1 == null || url1.equals(model))
            imageView1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

        if (url2 == null || url2.equals(model))
            imageView2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        handleSuccessOrErrorImageLoading();
        return false;
    }
}
