package com.adithyaupadhya.newtorkmodule.volley.pojos;

import java.util.List;

/**
 * Created by adithya.upadhya on 23-04-2016.
 */
public class TMDBTrailerResponse {
    public List<Results> results;
    public static class Results{
        public String key;
        public String site;
        public String type;
    }
}
