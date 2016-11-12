package com.adithyaupadhya.moviemaniac.base;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.newtorkmodule.volley.constants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.constants.NetworkConstants;
import com.adithyaupadhya.newtorkmodule.volley.retrofit.networkwrappers.NetworkActivity;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import retrofit2.Call;

/**
 * Created by adithya.upadhya on 07-01-2016.
 */
public abstract class AbstractDetailsActivity<APIResponseClass> extends NetworkActivity<APIResponseClass> implements
        View.OnClickListener,
        MaterialDialog.SingleButtonCallback {

    protected FloatingActionButton mActionButton;
    protected boolean isFavoriteItem;
    protected String mYouTubeKey;
    private String mShareSubject, mShareBody;

    protected void initializeActivityItems(String title, String imagePath, float imgAspectRatio) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);

        // INITIALIZE COLLAPSING TOOLBAR LAYOUT.
        AppBarLayout layout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        ViewCompat.setTransitionName(layout, "APP_BAR");
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) layout.findViewById(R.id.collapsing_toolbar);
        toolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));
        toolbarLayout.setExpandedTitleColor(Color.WHITE);
        toolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
        toolbarLayout.setTitle(title);

        ImageView networkImageView = (ImageView) toolbarLayout.findViewById(R.id.networkImageView);
        networkImageView.requestLayout();
        networkImageView.getLayoutParams().height = (int) Math.ceil(APIConstants.getScreenWidthPixels(this) * imgAspectRatio);

        Glide.with(this)
                .load(NetworkConstants.IMG_BASE_BACKDROP_URL + imagePath)
                .placeholder(R.drawable.backdrop_def_image)
                .error(R.drawable.no_img_placeholder)
                .dontAnimate()
                .into(networkImageView);

        mActionButton = (FloatingActionButton) findViewById(R.id.fab);
        findViewById(R.id.share_button).setOnClickListener(this);

        if (findViewById(R.id.view_trailer) != null)
            findViewById(R.id.view_trailer).setOnClickListener(this);
    }

    protected abstract String retrieveShareSubject();

    protected abstract String retrieveShareBody();

    protected abstract void establishNetworkCall();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_button:
                if (mShareSubject == null || mShareBody == null) {
                    mShareSubject = retrieveShareSubject();
                    mShareBody = retrieveShareBody();
                }
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mShareSubject);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mShareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share through"));
                break;

            case R.id.fab:
                Utils.showFavoritesMaterialDialog(this, this, isFavoriteItem);
                break;

            case R.id.view_trailer:
                if (mYouTubeKey != null) {

                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mYouTubeKey)));
                    } catch (ActivityNotFoundException exception) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + mYouTubeKey)));
                    } catch (Exception exception) {
                        Toast.makeText(this, "Unable to launch YouTube...", Toast.LENGTH_SHORT).show();
                    }

                }
                break;

            default:
                // This is to handle SnackBar click event.
                establishNetworkCall();
        }
    }


    private void showNetworkErrorSnackbar() {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            Utils.displayNetworkErrorSnackBar(view, this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNetworkFailure(Call<APIResponseClass> call, Throwable t) {
        showNetworkErrorSnackbar();
    }
}
