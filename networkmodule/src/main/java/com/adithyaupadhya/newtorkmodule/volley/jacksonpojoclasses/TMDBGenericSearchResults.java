package com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

/**
 * Created by adithya.upadhya on 05-10-2016.
 */

public class TMDBGenericSearchResults {
    public List<Results> results;

    public static class Results {
        public String name;

        @JsonSetter("title")
        public void setTitle(String title) {
            this.name = title;
        }
    }
}
