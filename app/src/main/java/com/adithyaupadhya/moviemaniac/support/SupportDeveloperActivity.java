package com.adithyaupadhya.moviemaniac.support;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;
import com.google.android.gms.plus.PlusOneButton;

public class SupportDeveloperActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PLUS_ONE_REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_developer);

        FacebookSdk.sdkInitialize(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        setTitle(R.string.support_developers_string);

        LikeView likeView = (LikeView) findViewById(R.id.facebookLikeView);
        if (likeView != null) {
            likeView.setObjectIdAndType(NetworkConstants.FACEBOOK_PAGE_URL, LikeView.ObjectType.PAGE);
            likeView.setLikeViewStyle(LikeView.Style.BOX_COUNT);
        }

        findViewById(R.id.buttonShareApp).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlusOneButton plusOneButton = (PlusOneButton) findViewById(R.id.googlePlusOneButton);

        if (plusOneButton != null)
            plusOneButton.initialize(NetworkConstants.GOOGLE_PLUS_URL, PLUS_ONE_REQUEST_CODE);
    }

    @Override
    public void onClick(View v) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MovieManiac Android Application");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_app_string));
        startActivity(Intent.createChooser(sharingIntent, "Share this app via"));
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
