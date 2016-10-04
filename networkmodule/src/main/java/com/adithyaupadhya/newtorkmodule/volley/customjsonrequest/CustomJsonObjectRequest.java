package com.adithyaupadhya.newtorkmodule.volley.customjsonrequest;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by adithya.upadhya on 01-03-2016.
 */
public class CustomJsonObjectRequest extends JsonObjectRequest {

    public CustomJsonObjectRequest(int method, String url, Object tag, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        super.setTag(tag);
        setRetryPolicy(new DefaultRetryPolicy(3000, 2, 1.0F));
    }

    @Override
    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }

    /*@Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            Cache.Entry cache = HttpHeaderParser.parseCacheHeaders(response);

            cache.softTtl = 3600 * 1000; // Cache ttl = 1 hour.

            return Response.success(new JSONObject(jsonString), cache);

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }*/
}
