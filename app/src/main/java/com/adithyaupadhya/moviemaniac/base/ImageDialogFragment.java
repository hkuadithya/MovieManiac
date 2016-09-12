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

import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageDialogFragment extends DialogFragment {

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

        ImageView imageView = (ImageView) view.findViewById(R.id.networkImageView);
        imageView.requestLayout();

        // 75% width of the screen taken by the image
        imageView.getLayoutParams().width = (int) (APIConstants.getScreenWidthPixels(getContext()) * 0.75);
        // height = 1.5 * width
        imageView.getLayoutParams().height = (int) (imageView.getLayoutParams().width * 1.5);

        String networkUrl = getArguments().getString(AppIntentConstants.BUNDLE_URL);

        Glide.with(this)
                .load(NetworkConstants.IMG_BASE_DIALOG_POSTER_URL + networkUrl)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.not_found)
                .dontAnimate()
                .into(imageView);

        return view;
    }

}
