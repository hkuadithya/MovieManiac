package com.adithyaupadhya.newtorkmodule.volley.retrofit;

import com.adithyaupadhya.newtorkmodule.volley.constants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.constants.NetworkConstants;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBCelebrityBiodataResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBCelebrityResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBGenericGameResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBGenericSearchResults;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBGenreResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBImageResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBMovieRecosCreditsVideosResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBMoviesResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBTVRecosCreditsVideosResponse;
import com.adithyaupadhya.newtorkmodule.volley.pojos.TMDBTVSeriesResponse;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by adithya.upadhya on 07-10-2016.
 */
public class RetrofitClient implements Interceptor {

    private static final RetrofitClient mRetrofitClientInstance = new RetrofitClient();
    private APIClient mApiClient;
    private OkHttpClient mOkHttpClient;

    private RetrofitClient() {

        mOkHttpClient = new OkHttpClient
                .Builder()
//                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(this)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkConstants.NETWORK_BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(APIConstants.getInstance().getObjectMapper()))
                .client(mOkHttpClient)
                .build();

        mApiClient = retrofit.create(APIClient.class);
    }


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        HttpUrl url = request.url()
                .newBuilder()
                .addQueryParameter("api_key", NetworkConstants.API_KEY)
                .build();

        request = request.newBuilder()
                .url(url)
                .build();

        return chain.proceed(request);

    }


    public static RetrofitClient getInstance() {
        return mRetrofitClientInstance;
    }


    public APIClient getNetworkClient() {
        return mApiClient;
    }

    public void cancelAllRequests() {
        mOkHttpClient.dispatcher().cancelAll();
    }

    public interface APIClient {

        // MOVIES       --------------------------------------------------------------------------------------

        @GET("movie/upcoming")
        Call<TMDBMoviesResponse> getUpcomingMovies(@Query("page") int page);

        @GET("movie/popular")
        Call<TMDBMoviesResponse> getPopularMovies(@Query("page") int page);

        @GET("movie/{movie_id}?append_to_response=recommendations,credits,videos")
        Call<TMDBMovieRecosCreditsVideosResponse> getMovieDetails(@Path("movie_id") int movieId);

        @GET("genre/movie/list")
        Call<TMDBGenreResponse> getMovieGenreList();

        @GET("search/movie")
        Call<TMDBMoviesResponse> getMovieSearchResults(@Query("query") String queryString, @Query("page") int pageNumber);

        @GET("search/movie")
        Call<TMDBGenericSearchResults> getMovieSearchSuggestions(@Query("query") String queryString, @Query("page") int pageNumber);

        @GET("movie/{movie_id}/images")
        Call<TMDBImageResponse> getMovieImages(@Path("movie_id") int movieId);


        // TV SERIES    --------------------------------------------------------------------------------------

        @GET("tv/on_the_air")
        Call<TMDBTVSeriesResponse> getOnTheAirTVSeries(@Query("page") int page);

        @GET("tv/top_rated")
        Call<TMDBTVSeriesResponse> getTopRatedTVSeries(@Query("page") int page);

        @GET("tv/{tv_id}?append_to_response=recommendations,credits,videos")
        Call<TMDBTVRecosCreditsVideosResponse> getTVSeriesDetails(@Path("tv_id") int tvId);

        @GET("genre/tv/list")
        Call<TMDBGenreResponse> getTVGenreList();

        @GET("search/tv")
        Call<TMDBTVSeriesResponse> getTVSearchResults(@Query("query") String queryString, @Query("page") int pageNumber);

        @GET("search/tv")
        Call<TMDBGenericSearchResults> getTVSeriesSearchSuggestions(@Query("query") String queryString, @Query("page") int pageNumber);

        @GET("tv/{tv_id}/images")
        Call<TMDBImageResponse> getTVSeriesImages(@Path("tv_id") int tvId);


        // CELEBRITIES  --------------------------------------------------------------------------------------

        @GET("person/popular")
        Call<TMDBCelebrityResponse> getPopularCelebrities(@Query("page") int page);

        @GET("person/{celeb_id}?append_to_response=movie_credits,tv_credits")
        Call<TMDBCelebrityBiodataResponse> getCelebrityDetails(@Path("celeb_id") int celebId);

        @GET("search/person")
        Call<TMDBCelebrityResponse> getCelebritySearchResults(@Query("query") String queryString, @Query("page") int pageNumber);

        @GET("search/person")
        Call<TMDBGenericSearchResults> getCelebritySearchSuggestions(@Query("query") String queryString, @Query("page") int pageNumber);

        @GET("person/{celeb_id}/images")
        Call<TMDBImageResponse> getCelebrityImages(@Path("celeb_id") int celebId);


        // GAMES        --------------------------------------------------------------------------------------

        @GET("movie/popular")
        Call<TMDBGenericGameResponse> getGameMovies(@Query("page") int page);

        @GET("tv/popular")
        Call<TMDBGenericGameResponse> getGameTVSeries(@Query("page") int page);

        @GET("person/popular")
        Call<TMDBGenericGameResponse> getGameCelebrities(@Query("page") int page);

    }
}
