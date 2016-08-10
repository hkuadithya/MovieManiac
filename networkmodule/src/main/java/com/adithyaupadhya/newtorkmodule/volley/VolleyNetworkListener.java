package com.adithyaupadhya.newtorkmodule.volley;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by adithya.upadhya on 15-07-2016.
 */
public interface VolleyNetworkListener extends Response.Listener<JSONObject>, Response.ErrorListener {
}
