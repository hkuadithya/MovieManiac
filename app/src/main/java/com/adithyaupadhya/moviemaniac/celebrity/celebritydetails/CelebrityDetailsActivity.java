package com.adithyaupadhya.moviemaniac.celebrity.celebritydetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.adithyaupadhya.database.DBConstants;
import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.AbstractDetailsActivity;
import com.adithyaupadhya.moviemaniac.base.Utils;
import com.adithyaupadhya.newtorkmodule.volley.constants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBCelebrityBiodataResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBCelebrityResponse;
import com.adithyaupadhya.newtorkmodule.volley.retrofit.RetrofitClient;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CelebrityDetailsActivity extends AbstractDetailsActivity<TMDBCelebrityBiodataResponse> {
    private static final String SHARE_SUBJECT = "CELEBRITY DETAILS";
    private TMDBCelebrityBiodataResponse biodataResponse;
    private TMDBCelebrityResponse.Results results;

    public static void startActivityIntent(Context context, TMDBCelebrityResponse.Results results) {
        Intent intent = new Intent(context, CelebrityDetailsActivity.class);
        intent.putExtra(AppIntentConstants.CELEB_DETAILS, results);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celebrity_details);

        results = getIntent().getParcelableExtra(AppIntentConstants.CELEB_DETAILS);
        establishNetworkCall();
        super.initializeActivityItems(results.name, results.profile_path, 0.8f);


        if (Utils.isRecordFavoriteItem(this, DBConstants.CELEBRITY_TABLE_URI, DBConstants.CELEBRITY_ID, results.id)) {
            super.isFavoriteItem = true;
            super.mActionButton.setImageResource(R.drawable.vector_dislike);
        }

        mActionButton.setOnClickListener(this);

        //mActionButton.setVisibility(View.VISIBLE);
    }


    private void bindCelebrityData(View view, TMDBCelebrityBiodataResponse results) {
        this.biodataResponse = results;
        view.findViewById(R.id.share_button).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.textViewCelebrityName)).setText(results.name);
        ((TextView) view.findViewById(R.id.textViewCelebrityBiography)).setText(Utils.preventStringCut(results.biography));
        ((TextView) view.findViewById(R.id.textViewBirthplace)).setText(Utils.toString(results.place_of_birth));
        ((TextView) view.findViewById(R.id.textViewBirthday)).setText(Utils.toString(results.birthday));

        if (results.also_known_as != null && results.also_known_as.size() > 0) {
            view.findViewById(R.id.alsoKnownAs).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.textViewAlias)).setText(TextUtils.join(", ", results.also_known_as));
        }
        if (results.deathday != null && !results.deathday.equals("")) {
            view.findViewById(R.id.dateOfDeath).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.textViewDod)).setText(results.deathday);
        }

        StringBuilder stringBuilder = new StringBuilder();
        List<TMDBCelebrityBiodataResponse.Credits.Cast> credits = results.movie_credits.cast;
        if (credits != null)
            for (int i = 0; i < credits.size() && i < 5; i++) {
                if (i == 0) stringBuilder.append("MOVIES");
                stringBuilder.append("\n    ").append(credits.get(i).title);
            }

        credits = results.tv_credits.cast;

        if (credits != null)
            for (int i = 0; i < credits.size() && i < 5; i++) {
                if (i == 0) stringBuilder.append("\n\nTV SERIES");
                stringBuilder.append("\n    ").append(credits.get(i).title);
            }

        ((TextView) (view.findViewById(R.id.textViewCredits))).setText(Utils.preventStringCut(stringBuilder));
    }

    @Override
    public void establishNetworkCall() {
        RetrofitClient.getInstance()
                .getNetworkClient()
                .getCelebrityDetails(results.id)
                .enqueue(this);
    }

    @Override
    public void onNetworkResponse(Call<TMDBCelebrityBiodataResponse> call, Response<TMDBCelebrityBiodataResponse> response) {
        bindCelebrityData(findViewById(R.id.rootLinearLayout), response.body());
    }

    @Override
    public String retrieveShareSubject() {
        return SHARE_SUBJECT;
    }

    @Override
    public String retrieveShareBody() {
        return "CELEBRITY NAME\n" + biodataResponse.name +
                "\n\nBIOGRAPHY\n" + biodataResponse.biography +
                "\n\nCREDITS\n" + ((TextView) findViewById(R.id.textViewCredits)).getText() +
                "\n\nPLACE OF BIRTH\n" + biodataResponse.place_of_birth +
                "\n\nDATE OF BIRTH\n" + biodataResponse.birthday +
                (TextUtils.isEmpty(biodataResponse.deathday) ?
                        "" : "\n\nDATE OF DEATH\n" + biodataResponse.deathday);
    }


    @Override
    public void onClick(MaterialDialog dialog, DialogAction which) {
        Utils.insertDeleteFromCelebritiesTable(this, results, isFavoriteItem);
        isFavoriteItem = !isFavoriteItem;
        mActionButton.setImageResource(isFavoriteItem ? R.drawable.vector_dislike : R.drawable.vector_favorite);
    }

}
