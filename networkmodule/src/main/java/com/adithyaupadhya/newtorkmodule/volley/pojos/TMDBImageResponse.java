package com.adithyaupadhya.newtorkmodule.volley.pojos;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

/**
 * Created by adithya.upadhya on 20-06-2016.
 */
public class TMDBImageResponse {
    public List<Backdrop> backdrops;

    @JsonSetter("profiles")
    public void setProfiles(List<Backdrop> profiles) {
        backdrops = profiles;
    }

    public static class Backdrop {
        public String file_path;
    }
}
