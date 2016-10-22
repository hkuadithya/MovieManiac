package com.adithyaupadhya.moviemaniac.base;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageDialogFragment extends DialogFragment implements RequestListener<String, GlideDrawable>, View.OnClickListener {

    private TextView mTextViewRetry;
    private String mImageUrl;
    private ImageView mImageViewPic;

    public ImageDialogFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_dialog, container, false);
        mTextViewRetry = (TextView) view.findViewById(R.id.textViewRetry);

        mImageViewPic = (ImageView) view.findViewById(R.id.networkImageView);
        mImageViewPic.requestLayout();

        // 75% width of the screen taken by the image
        mImageViewPic.getLayoutParams().width = (int) (APIConstants.getScreenWidthPixels(getContext()) * 0.75);
        // height = 1.5 * width
        mImageViewPic.getLayoutParams().height = (int) (mImageViewPic.getLayoutParams().width * 1.5);

        mImageUrl = getArguments().getString(AppIntentConstants.BUNDLE_URL);

        loadNetworkImage();

        mTextViewRetry.setOnClickListener(this);
        return view;
    }

    private void loadNetworkImage() {
        Glide.with(this)
                .load(NetworkConstants.IMG_BASE_DIALOG_POSTER_URL + mImageUrl)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.not_found)
                .dontAnimate()
                .listener(this)
                .into(mImageViewPic);
    }

    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        if (e != null && (e instanceof NoConnectionError || e.getCause() instanceof NoConnectionError || e.getCause() instanceof TimeoutError)) {
            mTextViewRetry.setVisibility(View.VISIBLE);
            return true;
        }

        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewRetry:
                loadNetworkImage();
                mTextViewRetry.setVisibility(View.GONE);
                break;
        }
    }
}
