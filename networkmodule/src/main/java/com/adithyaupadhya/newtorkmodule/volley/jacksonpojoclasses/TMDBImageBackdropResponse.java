package com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses;

import java.util.List;

/**
 * Created by adithya.upadhya on 20-06-2016.
 */
public class TMDBImageBackdropResponse {
    public List<Backdrop> backdrops;

    public static class Backdrop {
        public String file_path;
    }
}
