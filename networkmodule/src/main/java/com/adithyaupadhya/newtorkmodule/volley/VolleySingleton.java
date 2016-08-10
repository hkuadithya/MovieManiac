package com.adithyaupadhya.newtorkmodule.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by adithya.upadhya on 17-01-2016.
 */
public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;

    private VolleySingleton(Context applicationContext) {
        mRequestQueue = Volley.newRequestQueue(applicationContext);
    }

    public static void initInstance(Context context) {
        if (mInstance == null)
            mInstance = new VolleySingleton(context.getApplicationContext());
    }

    public static VolleySingleton getInstance(Context context) {
        return mInstance != null ? mInstance : (mInstance = new VolleySingleton(context.getApplicationContext()));
    }

    public RequestQueue getVolleyRequestQueue() {
        return mRequestQueue;
    }

}
