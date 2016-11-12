package com.adithyaupadhya.moviemaniac.games;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.adithyaupadhya.database.DBConstants;
import com.adithyaupadhya.database.sharedpref.AppPreferenceManager;
import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.newtorkmodule.volley.constants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.constants.NetworkConstants;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;
import com.google.android.gms.plus.PlusOneButton;

public class GameSummaryActivity extends AppCompatActivity {
    private PlusOneButton mPlusOneButton;
    private static final int PLUS_ONE_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_summary);

        FacebookSdk.sdkInitialize(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);

        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));

        setTitle(getString(R.string.game_toolbar_title_summary));


        AppPreferenceManager manager = AppPreferenceManager.getAppPreferenceInstance(this);

        int correctAnswers = getIntent().getIntExtra(AppIntentConstants.CORRECT_ANS_COUNT, 0);

        String userHighScorePrefKey = manager.getPreferenceData(DBConstants.USER_ID) + DBConstants.GAME_HIGH_SCORE;

        String highScore = manager.getPreferenceData(userHighScorePrefKey);

        if (highScore == null || Integer.parseInt(highScore) < correctAnswers) {
            manager.setPreferenceData(userHighScorePrefKey, String.valueOf(correctAnswers));
            highScore = String.valueOf(correctAnswers);
        }

        ((TextView) (findViewById(R.id.textViewScore))).setText(getString(R.string.game_score_value, String.valueOf(correctAnswers)));

        ((TextView) (findViewById(R.id.textViewHighScore))).setText(getString(R.string.game_score_value, highScore));

        LikeView likeView = (LikeView) findViewById(R.id.facebookLikeView);

        if (likeView != null) {

            likeView.setObjectIdAndType(NetworkConstants.FACEBOOK_PAGE_URL, LikeView.ObjectType.PAGE);

            likeView.setLikeViewStyle(LikeView.Style.BOX_COUNT);
        }

        mPlusOneButton = (PlusOneButton) findViewById(R.id.googlePlusOneButton);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPlusOneButton.initialize(NetworkConstants.GOOGLE_PLUS_URL, PLUS_ONE_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        GameMediaPlayer.getInstance().releaseResources();

        mPlusOneButton = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
