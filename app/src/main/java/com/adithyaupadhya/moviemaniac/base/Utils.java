package com.adithyaupadhya.moviemaniac.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.adithyaupadhya.database.DBConstants;
import com.adithyaupadhya.database.sharedpref.AppPreferenceManager;
import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.newtorkmodule.volley.constants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.constants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBCelebrityResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBMoviesResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBTVSeriesResponse;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by adithya.upadhya on 09-03-2016.
 */
public class Utils {

    public static void showFavoritesMaterialDialog(Context context, MaterialDialog.SingleButtonCallback listener, boolean isFavoriteItem) {
        String content, title;
        Drawable icon;
        if (!isFavoriteItem) {
            content = "Do you wish to add this item to your list of favourites?";
            title = "Add to favourites";
            icon = ContextCompat.getDrawable(context, R.drawable.vector_dialog_favorite);
        } else {
            content = "Do you wish to remove this item from your list of favourites?";
            title = "Remove from favourites";
            icon = ContextCompat.getDrawable(context, R.drawable.vector_dislike);
        }

        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText("ACCEPT")
                .negativeText("CANCEL")
                .icon(icon)
                .positiveColor(ContextCompat.getColor(context, R.color.app_material_blue))
                .negativeColor(ContextCompat.getColor(context, R.color.app_material_blue))
                .onPositive(listener)
                .build()
                .show();

    }

    public static void showLogoutMaterialDialog(Context context, MaterialDialog.SingleButtonCallback listener) {
        new MaterialDialog.Builder(context)
                .title("Logout confirmation")
                .content("Do you wish to Logout from this application?")
                .positiveText("ACCEPT")
                .negativeText("CANCEL")
                .positiveColor(ContextCompat.getColor(context, R.color.app_material_blue))
                .negativeColor(ContextCompat.getColor(context, R.color.app_material_blue))
                .onPositive(listener)
                .build()
                .show();
    }

    public static void showGenericMaterilaDialog(Context context, MaterialDialog.SingleButtonCallback listener, String title, String content) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText("ACCEPT")
                .negativeText("CANCEL")
                .positiveColor(ContextCompat.getColor(context, R.color.app_material_blue))
                .negativeColor(ContextCompat.getColor(context, R.color.app_material_blue))
                .onPositive(listener)
                .build()
                .show();
    }


    public static void insertDeleteFromMovieTable(Context context, TMDBMoviesResponse.Results results, boolean isFavoriteItem) {

        String userId = AppPreferenceManager.getAppPreferenceInstance(context).getPreferenceData(DBConstants.USER_ID);

        if (!isFavoriteItem) {
            ContentValues values = new ContentValues();
            values.put(DBConstants.USER_ID, userId);
            values.put(DBConstants.MOVIE_ID, results.id);

            try {
                values.put(DBConstants.MOVIE_DETAILS, APIConstants.getInstance().getObjectMapper().writeValueAsString(results));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            context.getContentResolver().insert(DBConstants.MOVIE_TABLE_URI, values);
            Toast.makeText(context, "This movie has been added to your favourites", Toast.LENGTH_SHORT).show();
        } else {
            context.getContentResolver().delete(DBConstants.MOVIE_TABLE_URI,
                    DBConstants.USER_ID + "=? AND " + DBConstants.MOVIE_ID + "=?", new String[]{userId, String.valueOf(results.id)});

            Toast.makeText(context, "This movie has been removed from your favourites", Toast.LENGTH_SHORT).show();
        }
    }


    public static void insertDeleteFromTVSeriesTable(Context context, TMDBTVSeriesResponse.Results results, boolean isFavoriteItem) {

        String userId = AppPreferenceManager.getAppPreferenceInstance(context).getPreferenceData(DBConstants.USER_ID);

        if (!isFavoriteItem) {
            ContentValues values = new ContentValues();
            values.put(DBConstants.USER_ID, userId);
            values.put(DBConstants.TVSERIES_ID, results.id);
            try {
                values.put(DBConstants.TVSERIES_DETAILS, APIConstants.getInstance().getObjectMapper().writeValueAsString(results));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            context.getContentResolver().insert(DBConstants.TVSERIES_TABLE_URI, values);
            Toast.makeText(context, "This TVSeries has been added to your favourites", Toast.LENGTH_SHORT).show();
        } else {
            context.getContentResolver().delete(DBConstants.TVSERIES_TABLE_URI,
                    DBConstants.USER_ID + "=? AND " + DBConstants.TVSERIES_ID + "=?", new String[]{userId, String.valueOf(results.id)});

            Toast.makeText(context, "This TVSeries has been removed from your favourites", Toast.LENGTH_SHORT).show();
        }
    }


    public static void insertDeleteFromCelebritiesTable(Context context, TMDBCelebrityResponse.Results results, boolean isFavoriteItem) {

        String userId = AppPreferenceManager.getAppPreferenceInstance(context).getPreferenceData(DBConstants.USER_ID);

        if (!isFavoriteItem) {
            ContentValues values = new ContentValues();
            values.put(DBConstants.USER_ID, userId);
            values.put(DBConstants.CELEBRITY_ID, results.id);
            try {
                values.put(DBConstants.CELEBRITY_DETAILS, APIConstants.getInstance().getObjectMapper().writeValueAsString(results));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            context.getContentResolver().insert(DBConstants.CELEBRITY_TABLE_URI, values);
            Toast.makeText(context, "This Celebrity has been added to your favourites", Toast.LENGTH_SHORT).show();
        } else {
            context.getContentResolver().delete(DBConstants.CELEBRITY_TABLE_URI,
                    DBConstants.USER_ID + "=? AND " + DBConstants.CELEBRITY_ID + "=?", new String[]{userId, String.valueOf(results.id)});

            Toast.makeText(context, "This Celebrity has been removed from your favourites", Toast.LENGTH_SHORT).show();
        }
    }


    public static void displayNetworkErrorSnackBar(View view, View.OnClickListener listener) {
        if (view != null && listener != null)
            Snackbar.make(view, "NETWORK ERROR", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", listener)
                    .setActionTextColor(Color.RED)
                    .show();
    }

    public static boolean isConnectedToInternet() {
//        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
//        return networkInfo != null && networkInfo.isConnected();

        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            // Check if exit value == 0, exit status = 0 => success status...
            return process.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isRecordFavoriteItem(Context context, Uri tableUri, String itemAttribute, int itemId) {

        String userId = AppPreferenceManager.getAppPreferenceInstance(context).getPreferenceData(DBConstants.USER_ID);

        Cursor cursor = context.getContentResolver().query(tableUri,
                new String[]{"1"},
                DBConstants.USER_ID + "=? AND " + itemAttribute + "=?",
                new String[]{userId, String.valueOf(itemId)},
                null);

        boolean recordExists = (cursor != null && cursor.getCount() > 0);

        if (cursor != null)
            cursor.close();

        return recordExists;
    }

    public static String toString(Object object) {
        return (object != null && object.toString().length() > 0) ? object.toString() : "N/A";
    }

    public static String preventStringCut(Object object) {
        return (object != null && object.toString().length() > 0) ? object.toString() + "\n" : "N/A";

    }

    public static float convertDPtoPixels(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void showImageDialogFragment(Context context, String url) {
        ImageDialogFragment dialogFragment = new ImageDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppIntentConstants.BUNDLE_URL, url);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "ImageDialog");
    }
}
