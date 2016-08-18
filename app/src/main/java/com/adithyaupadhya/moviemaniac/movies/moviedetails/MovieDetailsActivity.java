package com.adithyaupadhya.moviemaniac.movies.moviedetails;

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
import com.adithyaupadhya.newtorkmodule.volley.VolleySingleton;
import com.adithyaupadhya.newtorkmodule.volley.customjsonrequest.CustomJsonObjectRequest;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBMovieSimilarCreditsVideosResponse;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBMovieTVCastResponse;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBMoviesResponse;
import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBTrailerResponse;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.adithyaupadhya.uimodule.applicationfont.RobotoTextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MovieDetailsActivity extends AbstractDetailsActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static final String SHARE_SUBJECT = "MOVIE DETAILS";
    private TMDBMoviesResponse.Results results;
    private RecyclerView recyclerView;
    private RobotoTextView textViewBanner;


    public static void startActivityIntent(Context context, TMDBMoviesResponse.Results results, int... intentFlags) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra(AppIntentConstants.MOVIE_DETAILS, results);
        for (int flag : intentFlags)
            intent.addFlags(flag);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_tvseries_details);


        results = getIntent().getParcelableExtra(AppIntentConstants.MOVIE_DETAILS);

        // Log.d("MMVOLLEY", results.backdrop_path + " " + results.poster_path);
        super.initializeActivityItems(results.title, results.backdrop_path != null ? results.backdrop_path : results.poster_path,
                ((float) NetworkConstants.backdropDim[1]) / NetworkConstants.backdropDim[0]);

        establishNetworkCall();

        if (Utils.isRecordFavoriteItem(this, DBConstants.MOVIE_TABLE_URI, DBConstants.MOVIE_ID, results.id)) {
            super.isFavoriteItem = true;
            super.mActionButton.setImageResource(R.drawable.vector_dislike);
        }

        mActionButton.setOnClickListener(this);

        bindMovieData(findViewById(R.id.rooLinearLayout));

    }


    private void bindMovieData(View view) {
        ((TextView) view.findViewById(R.id.textViewMovieTitle)).setText(results.title);
        ((TextView) view.findViewById(R.id.textViewDescription)).setText(Utils.toString(results.overview));
        ((TextView) view.findViewById(R.id.textViewMovieGenre)).setText(APIConstants.getInstance().getMovieGenreList(results.genre_ids, this));
        ((TextView) view.findViewById(R.id.textViewLanguage)).setText(results.original_language.toUpperCase());
        ((TextView) view.findViewById(R.id.textViewReleaseDate)).setText(results.release_date);
        ((TextView) view.findViewById(R.id.textViewVotes)).setText(Utils.toString(results.vote_count));
        ((TextView) view.findViewById(R.id.textViewVoteAverage)).setText(APIConstants.getFormattedDecimal(results.vote_average));

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        //recyclerView.startNestedScroll(RecyclerView.HORIZONTAL);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new MoviesSimilarAdapter(this));

        textViewBanner = (RobotoTextView) view.findViewById(R.id.textViewBanner);
    }

    @Override
    public void establishNetworkCall() {
        VolleySingleton.getInstance(this)
                .getVolleyRequestQueue()
                .add(new CustomJsonObjectRequest(
                        Request.Method.GET,
                        NetworkConstants.MOVIE_SIMILAR_CREDITS_VIDEOS_URL.replaceFirst("movie_id", results.id.toString()),
                        this, this, this));

    }

    private void addRecyclerViewAdapter(List<TMDBMoviesResponse.Results> results) {
        recyclerView.setVisibility(View.VISIBLE);
        ((MoviesSimilarAdapter) (recyclerView.getAdapter())).setSimilarMoviesResponse(results);
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

        return "MOVIE TITLE\n" + results.title +
                "\n\nGENRE\n" + APIConstants.getInstance().getMovieGenreList(results.genre_ids, this) +
                "\n\nMOVIE CAST\n" + ((TextView) findViewById(R.id.textViewMovieCast)).getText() +
                "\n\nRELEASE DATE\n" + results.release_date +
                "\n\nLANGUAGE\n" + results.original_language +
                "\n\nTMDB AVG VOTE\n" + results.vote_average +
                "\n\nTMDB VOTES\n" + results.vote_count;
    }


    @Override
    public void onClick(MaterialDialog dialog, DialogAction which) {
        //  ADD THE FAVORITE TO DB.
        Utils.insertDeleteFromMovieTable(this, results, isFavoriteItem);
        isFavoriteItem = !isFavoriteItem;
        mActionButton.setImageResource(isFavoriteItem ? R.drawable.vector_dislike : R.drawable.vector_favorite);
    }


    //  HANDLING NETWORK CALLS

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        super.showNetworkErrorSnackbar();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        TextView textViewMovieCast = (TextView) findViewById(R.id.textViewMovieCast);
        View shareButton = findViewById(R.id.share_button);
        Button viewTrailer = (Button) findViewById(R.id.view_trailer);

        try {
            TMDBMovieSimilarCreditsVideosResponse response = APIConstants.getInstance().getJacksonObjectMapper().readValue(jsonObject.toString(), TMDBMovieSimilarCreditsVideosResponse.class);

            //  HANDLING MOVIE CASTS
            List<TMDBMovieTVCastResponse.Cast> casts = response.credits.cast;

            StringBuilder sb = new StringBuilder();
            if (casts != null)
                for (int i = 0; i < casts.size() && i < 5; i++) {
                    sb.append(Utils.toString(casts.get(i).name)).append(" as ").append(Utils.toString(casts.get(i).character)).append("\n");
                }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);

            if (textViewMovieCast != null)
                textViewMovieCast.setText(Utils.toString(sb));

            //  HANDLING SIMILAR MOVIES.
            List<TMDBMoviesResponse.Results> similarResults = response.similar.results;

            if (similarResults == null || similarResults.size() == 0) {
                hideRecyclerView();
            } else {
                textViewBanner.setText("SIMILAR CONTENT (" + similarResults.size() + ")");
                addRecyclerViewAdapter(similarResults);
            }

            //  HANDLING TRAILER AND VIDEOS
            List<TMDBTrailerResponse.Results> videoResults = response.videos.results;

            if (videoResults != null)
                for (TMDBTrailerResponse.Results result : videoResults) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
