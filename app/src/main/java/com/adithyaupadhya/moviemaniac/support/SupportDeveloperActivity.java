package com.adithyaupadhya.moviemaniac.support;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.newtorkmodule.volley.constants.NetworkConstants;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;
import com.google.android.gms.plus.PlusOneButton;

public class SupportDeveloperActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    private PlusOneButton mPlusOneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_developer);

        FacebookSdk.sdkInitialize(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        setTitle(R.string.support_developers_string);

        LikeView likeView = (LikeView) findViewById(R.id.facebookLikeView);
        if (likeView != null) {
            likeView.setObjectIdAndType(NetworkConstants.FACEBOOK_PAGE_URL, LikeView.ObjectType.PAGE);
            likeView.setLikeViewStyle(LikeView.Style.BOX_COUNT);
        }

        findViewById(R.id.buttonReportBugs).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlusOneButton = (PlusOneButton) findViewById(R.id.googlePlusOneButton);

        if (mPlusOneButton != null)
            mPlusOneButton.initialize(NetworkConstants.GOOGLE_PLUS_URL, PLUS_ONE_REQUEST_CODE);
    }

    @Override
    public void onClick(View v) {

        String personalDetails = getString(R.string.report_bugs_email_body, Build.MANUFACTURER, Build.MODEL,
                Build.VERSION.RELEASE, String.valueOf(Build.VERSION.SDK_INT));

        try {
            Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "developer.moviemaniac@gmail.com"));

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_bugs_email_subject));

            emailIntent.putExtra(Intent.EXTRA_TEXT, personalDetails);

            startActivity(emailIntent);
        } catch (Exception exception) {
            Toast.makeText(this, R.string.toast_no_email_app_found, Toast.LENGTH_LONG).show();
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlusOneButton.setOnPlusOneClickListener(null);
        mPlusOneButton = null;
    }
}
