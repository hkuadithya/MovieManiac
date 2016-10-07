package com.adithyaupadhya.newtorkmodule.volley.retrofit;

import com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses.TMDBMoviesResponse;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by adithya.upadhya on 07-10-2016.
 */
public class RetrofitClient {

    private static RetrofitClient mRetrofitClientInstance = new RetrofitClient();
    private APIClient mApiClient;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkConstants.NETWORK_BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(APIConstants.getInstance().getJacksonObjectMapper()))
                .build();

        mApiClient = retrofit.create(APIClient.class);
    }

    public static RetrofitClient getInstance() {
        if (mRetrofitClientInstance == null) {
            mRetrofitClientInstance = new RetrofitClient();
        }
        return mRetrofitClientInstance;
    }

    public APIClient getNetworkClient() {
        return mApiClient;
    }

    private interface APIClient {

        @GET("movie/upcoming")
        Call<TMDBMoviesResponse> getUpcomingMovies(@Query("page") int page, @Query("api_key") String apiKey);

    }
}
