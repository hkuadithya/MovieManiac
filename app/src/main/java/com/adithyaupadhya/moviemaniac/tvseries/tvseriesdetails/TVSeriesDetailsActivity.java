package com.adithyaupadhya.moviemaniac.tvseries.tvseriesdetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adithyaupadhya.database.DBConstants;
import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.AbstractDetailsActivity;
import com.adithyaupadhya.moviemaniac.base.Utils;
import com.adithyaupadhya.newtorkmodule.volley.constants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.constants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.constants.NetworkConstants;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBMovieTVCastResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBTVRecosCreditsVideosResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBTVSeriesResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBTrailerResponse;
import com.adithyaupadhya.newtorkmodule.volley.retrofit.RetrofitClient;
import com.adithyaupadhya.uimodule.applicationfont.RobotoTextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import retrofit2.Call;

public class TVSeriesDetailsActivity extends AbstractDetailsActivity<TMDBTVRecosCreditsVideosResponse> {
    private static final String SHARE_SUBJECT = "TV SERIES DETAILS";
    private TMDBTVSeriesResponse.Results results;
    private RecyclerView recyclerView;
    private RobotoTextView textViewBanner;


    public static void startActivityIntent(Context context, TMDBTVSeriesResponse.Results results) {
        Intent intent = new Intent(context, TVSeriesDetailsActivity.class);
        intent.putExtra(AppIntentConstants.TV_DETAILS, results);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_tvseries_details);

        results = getIntent().getParcelableExtra(AppIntentConstants.TV_DETAILS);

        // Log.d("MMVOLLEY", results.backdrop_path + " " + results.poster_path);
        super.initializeActivityItems(results.name, results.backdrop_path != null ? results.backdrop_path : results.poster_path,
                ((float) NetworkConstants.backdropDim[1]) / NetworkConstants.backdropDim[0]);

        establishNetworkCall();

        if (Utils.isRecordFavoriteItem(this, DBConstants.TVSERIES_TABLE_URI, DBConstants.TVSERIES_ID, results.id)) {
            super.isFavoriteItem = true;
            super.mActionButton.setImageResource(R.drawable.vector_dislike);
        }

        mActionButton.setOnClickListener(this);

        bindTVSeriesData(findViewById(R.id.rooLinearLayout));

    }


    private void bindTVSeriesData(View view) {
        ((TextView) view.findViewById(R.id.textViewMovieTitle)).setText(results.name);
        ((TextView) view.findViewById(R.id.textViewDescription)).setText(results.overview);
        ((TextView) view.findViewById(R.id.textViewMovieGenre)).setText(APIConstants.getInstance().getTVGenreList(results.genre_ids, this));
        ((TextView) view.findViewById(R.id.textViewLanguage)).setText(results.original_language.toUpperCase());
        ((TextView) view.findViewById(R.id.textViewReleaseDate)).setText(results.first_air_date);
        ((TextView) view.findViewById(R.id.textViewVotes)).setText(String.valueOf(results.vote_count));
        ((TextView) view.findViewById(R.id.textViewVoteAverage)).setText(APIConstants.getFormattedDecimal(results.vote_average));

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        //recyclerView.startNestedScroll(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new TVSeriesSimilarAdapter(this));
        textViewBanner = (RobotoTextView) view.findViewById(R.id.textViewBanner);
    }


    @Override
    public void establishNetworkCall() {

        RetrofitClient.getInstance()
                .getNetworkClient()
                .getTVSeriesDetails(results.id)
                .enqueue(this);

    }

    private void showRecommendations(List<TMDBTVSeriesResponse.Results> results) {
        textViewBanner.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        ((TVSeriesSimilarAdapter) (recyclerView.getAdapter())).setSimilarTvSeriesResponse(results);
    }

    private void hideRecyclerView() {
        textViewBanner.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public String retrieveShareSubject() {
        return SHARE_SUBJECT;
    }

    @Override
    public String retrieveShareBody() {

        return "TV SERIES TITLE\n" + results.name +
                "\n\nGENRE\n" + APIConstants.getInstance().getTVGenreList(results.genre_ids, this) +
                "\n\nCREDITS\n" + ((TextView) findViewById(R.id.textViewMovieCast)).getText() +
                "\n\nRELEASE DATE\n" + results.first_air_date +
                "\n\nLANGUAGE\n" + results.original_language +
                "\n\nTMDB AVG VOTE\n" + results.vote_average +
                "\n\nTMDB VOTES\n" + results.vote_count;
    }

    @Override
    public void onClick(MaterialDialog dialog, DialogAction which) {
        Utils.insertDeleteFromTVSeriesTable(this, results, isFavoriteItem);
        isFavoriteItem = !isFavoriteItem;
        mActionButton.setImageResource(isFavoriteItem ? R.drawable.vector_dislike : R.drawable.vector_favorite);
    }

    @Override
    public void onNetworkResponse(Call<TMDBTVRecosCreditsVideosResponse> call, retrofit2.Response<TMDBTVRecosCreditsVideosResponse> response) {

        TextView textViewMovieCast = (TextView) findViewById(R.id.textViewMovieCast);
        View shareButton = findViewById(R.id.share_button);
        Button viewTrailer = (Button) findViewById(R.id.view_trailer);

        // HANDLING TV SERIES CAST RESPONSE
        List<TMDBMovieTVCastResponse.Cast> casts = response.body().credits.cast;

        StringBuilder sb = new StringBuilder();
        if (casts != null) {
            for (int i = 0; i < casts.size() && i < 5; i++)
                sb.append(Utils.toString(casts.get(i).name)).append(" as ").append(Utils.toString(casts.get(i).character)).append("\n");
        }

        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);

        if (textViewMovieCast != null)
            textViewMovieCast.setText(Utils.toString(sb));


        //  HANDLE SIMILAR TV SERIES RESPONSE:
        List<TMDBTVSeriesResponse.Results> similarResults = response.body().recommendations.results;
        if (similarResults == null || similarResults.size() == 0) {
            hideRecyclerView();
        } else {
            showRecommendations(similarResults);
        }


        //  HANDLE OPENING CREDITS VIDEO RESPONSE:
        List<TMDBTrailerResponse.Results> results = response.body().videos.results;

        if (results != null)
            for (TMDBTrailerResponse.Results result : results) {
                if (result.key != null && result.site != null && result.type != null && result.site.equalsIgnoreCase("youtube")) {
                    super.mYouTubeKey = result.key;
                    if (viewTrailer != null) {
                        viewTrailer.setText(result.type.toUpperCase());
                        viewTrailer.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }


        if (shareButton != null)
            shareButton.setVisibility(View.VISIBLE);
    }

}
