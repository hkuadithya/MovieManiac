package com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses;

import java.util.List;

/**
 * Created by adithya.upadhya on 23-04-2016.
 */
public class TMDBMovieTVCastResponse {
    public List<Cast> cast;

    public static class Cast {
        public String character;
        public String name;
    }
}
