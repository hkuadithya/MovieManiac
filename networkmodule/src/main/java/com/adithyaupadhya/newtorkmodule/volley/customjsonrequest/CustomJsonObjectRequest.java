package com.adithyaupadhya.newtorkmodule.volley.customjsonrequest;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by adithya.upadhya on 01-03-2016.
 */
public class CustomJsonObjectRequest extends JsonObjectRequest {
    public CustomJsonObjectRequest(int method, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(3000, 2, 1.0F));
    }

    public CustomJsonObjectRequest(int method, String url, Object tag, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        super.setTag(tag);
        setRetryPolicy(new DefaultRetryPolicy(3000, 2, 1.0F));
    }

    @Override
    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }

}
