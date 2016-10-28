package com.adithyaupadhya.moviemaniac.support;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

        findViewById(R.id.buttonReportBugs).setOnClickListener(this);
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
        String personalDetails = "Manufacturer : " + Build.MANUFACTURER +
                "\nDevice Model : " + Build.MODEL +
                "\nAndroid OS version : " + Build.VERSION.RELEASE +
                "\nSDK version : " + Build.VERSION.SDK_INT +
                "\n\nPLEASE NOTE: Device data is used only for Debugging purposes. Your identity is completely safe. \n\n\n";

        try {
            Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "developer.moviemaniac@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MovieManiac: Raise a bug / Request new features");
            emailIntent.putExtra(Intent.EXTRA_TEXT, personalDetails);
            startActivity(emailIntent);
        } catch (Exception exception) {
            Toast.makeText(this, "Error: No email application found in your device...", Toast.LENGTH_LONG).show();
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
}
