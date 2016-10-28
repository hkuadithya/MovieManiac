package com.adithyaupadhya.newtorkmodule.volley.constants;

import android.content.Context;
import android.text.TextUtils;

import com.adithyaupadhya.database.DBConstants;
import com.adithyaupadhya.database.sharedpref.AppPreferenceManager;
import com.adithyaupadhya.newtorkmodule.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by adithya.upadhya on 19-01-2016.
 */
public class APIConstants {
    private static APIConstants mInstance = null;
    private HashMap<Integer, String> mMovieGenre, mTVGenre;
    private HashMap<String, Integer> mCountryLanguageMap;
    private final ObjectMapper mObjectMapper;

    private APIConstants() {
        mObjectMapper = new ObjectMapper();
        mObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        initializeLanguageCountryMap();
    }

    private void loadMovieTVGenresFromCache(Context context) {
        synchronized (this) {
            if (mMovieGenre == null) {
                AppPreferenceManager manager = AppPreferenceManager.getAppPreferenceInstance(context);

                try {
                    mMovieGenre = mObjectMapper.readValue(manager.getPreferenceData(DBConstants.MOVIE_GENRE_CACHE),
                            new TypeReference<HashMap<Integer, String>>() {
                            });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (mTVGenre == null) {
                AppPreferenceManager manager = AppPreferenceManager.getAppPreferenceInstance(context);

                try {
                    mTVGenre = mObjectMapper.readValue(manager.getPreferenceData(DBConstants.TV_GENRE_CACHE),
                            new TypeReference<HashMap<Integer, String>>() {
                            });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static APIConstants getInstance() {
        return (mInstance != null) ? mInstance : (mInstance = new APIConstants());
    }

    public static String getFormattedDecimal(Float number) {
        if (number == null)
            return "N/A";

        return new BigDecimal(number).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static int getScreenWidthPixels(Context context) {
        // Log.d("MMVOLLEY", context.getResources().getDisplayMetrics().widthPixels + " " + context.getResources().getDisplayMetrics().heightPixels);
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    private void initializeLanguageCountryMap() {
        mCountryLanguageMap = new HashMap<>(40, 1);
        mCountryLanguageMap.put("hi", R.drawable.india);
        mCountryLanguageMap.put("te", R.drawable.india);
        mCountryLanguageMap.put("gu", R.drawable.india);
        mCountryLanguageMap.put("kn", R.drawable.india);
        mCountryLanguageMap.put("ta", R.drawable.india);
        mCountryLanguageMap.put("pa", R.drawable.india);
        mCountryLanguageMap.put("sa", R.drawable.india);
        mCountryLanguageMap.put("bn", R.drawable.india);

        mCountryLanguageMap.put("ko", R.drawable.korea);
        mCountryLanguageMap.put("sv", R.drawable.sweden);
        mCountryLanguageMap.put("ja", R.drawable.japan);
        mCountryLanguageMap.put("nl", R.drawable.netherlands);
        mCountryLanguageMap.put("es", R.drawable.spain);
        mCountryLanguageMap.put("pt", R.drawable.portugal);
        mCountryLanguageMap.put("de", R.drawable.germany);
        mCountryLanguageMap.put("it", R.drawable.italy);
        mCountryLanguageMap.put("zh", R.drawable.china);
        mCountryLanguageMap.put("ms", R.drawable.malaysia);
        mCountryLanguageMap.put("ru", R.drawable.russia);

        mCountryLanguageMap.put("se", R.drawable.sweden);
        mCountryLanguageMap.put("fr", R.drawable.france);
        mCountryLanguageMap.put("kr", R.drawable.korea);
        mCountryLanguageMap.put("us", R.drawable.usa);
        mCountryLanguageMap.put("gb", R.drawable.uk);
        mCountryLanguageMap.put("in", R.drawable.india);
        mCountryLanguageMap.put("cn", R.drawable.china);
        mCountryLanguageMap.put("my", R.drawable.malaysia);
    }

    public boolean areGenreMapsEmpty() {
        return mMovieGenre == null || mMovieGenre.size() == 0 || mTVGenre == null || mTVGenre.size() == 0;
    }

    public ObjectMapper getObjectMapper() {
        return mObjectMapper;
    }

    public void setMovieGenreMap(HashMap<Integer, String> movieMap) {
        this.mMovieGenre = movieMap;
    }

    public void setTvGenreMap(HashMap<Integer, String> tvGenreMap) {
        this.mTVGenre = tvGenreMap;
    }

    public String getMovieGenreList(List<Integer> genreIds, Context context) {
        ArrayList<String> genreList = new ArrayList<>(genreIds.size());

        if (mMovieGenre == null)
            loadMovieTVGenresFromCache(context);

        for (int genre : genreIds)
            if (mMovieGenre.containsKey(genre))
                genreList.add(mMovieGenre.get(genre));

        return (genreList.size() > 0) ? TextUtils.join(", ", genreList) : "N/A";
    }

    public String getTVGenreList(List<Integer> genreIds, Context context) {
        ArrayList<String> genreList = new ArrayList<>(genreIds.size());

        if (mTVGenre == null)
            loadMovieTVGenresFromCache(context);

        for (int genre : genreIds)
            if (mTVGenre.containsKey(genre))
                genreList.add(mTVGenre.get(genre));

        return (genreList.size() > 0) ? TextUtils.join(", ", genreList) : "N/A";
    }

    public Integer getCountryFlag(String string) {
        return mCountryLanguageMap.containsKey(string.toLowerCase()) ? mCountryLanguageMap.get(string) : null;
    }

    public Integer getCountryFlag(List<String> stringList) {
        for (String string : stringList)
            if (mCountryLanguageMap.containsKey(string.toLowerCase()))
                return mCountryLanguageMap.get(string.toLowerCase());

        return null;
    }

}
