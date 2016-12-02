package com.adithyaupadhya.newtorkmodule.volley.constants;

/**
 * Created by adithya.upadhya on 17-01-2016.
 */
public interface NetworkConstants {
    // Please obtain your own private TMDb api key through registration.
    String API_KEY = "";

    String NETWORK_BASE_URL = "http://api.themoviedb.org/3/";

    //  URL'S PERTAINING TO IMAGES
    String IMG_SIMILAR_ITEMS_POSTER_URL = "http://image.tmdb.org/t/p/w92";
    String IMG_BASE_POSTER_URL = "http://image.tmdb.org/t/p/w154";
    String IMG_BASE_DIALOG_POSTER_URL = "http://image.tmdb.org/t/p/w300";
    String IMG_BASE_BACKDROP_URL = "http://image.tmdb.org/t/p/w342";
    String FACEBOOK_PROFILE_PIC_URL = "https://graph.facebook.com/v2.6/userid/picture?type=normal";

    String IMG_GAME_POSTER_URL = "http://image.tmdb.org/t/p/w185";
    String IMG_GAME_BACKDROP_URL = "http://image.tmdb.org/t/p/w342";

    // WIDTH AND HEIGHT DIMENSIONS OF THE IMAGES: (FORMAT: WIDTH X HEIGHT).
    int[] backdropDim = {300, 169};     // aspect ratio.

    // FB AND GOOGLE URL
    String GOOGLE_PLUS_URL = "http://play.google.com/store/apps/details?id=com.adithyaupadhya.moviemaniac";
    String FACEBOOK_PAGE_URL = "https://www.facebook.com/Movie-Maniac-App-1738984046368507/";
}
