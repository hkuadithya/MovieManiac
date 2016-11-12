package com.adithyaupadhya.newtorkmodule.volley.pojos;

import java.util.List;

/**
 * Created by adithya.upadhya on 19-01-2016.
 */

public class TMDBGenreResponse {
    public List<Genres> genres;

    public static class Genres {
        public Integer id;
        public String name;
    }
}
