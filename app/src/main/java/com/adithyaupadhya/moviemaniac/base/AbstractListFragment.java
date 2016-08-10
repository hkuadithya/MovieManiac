package com.adithyaupadhya.moviemaniac.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import com.adithyaupadhya.moviemaniac.base.interfaces.OnLoadMoreListener;
import com.adithyaupadhya.newtorkmodule.volley.VolleySingleton;
import com.adithyaupadhya.newtorkmodule.volley.customjsonrequest.CustomJsonObjectRequest;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.APIConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by adithya.upadhya on 21-01-2016.
 */
public abstract class AbstractListFragment extends Fragment implements AbstractTabFragment.OnFabSnackBarClickListener,
        Response.Listener<JSONObject>,
        Response.ErrorListener,
        OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {

    private String mNetworkUrl;
    private RequestQueue mRequestQueue;
    private ObjectMapper mObjectMapper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = VolleySingleton.getInstance(getContext()).getVolleyRequestQueue();
        mObjectMapper = APIConstants.getInstance().getJacksonObjectMapper();
        mNetworkUrl = getArguments().getString(AppIntentConstants.BUNDLE_URL, null);

    }

    protected void volleyJsonObjectRequest(int pageNumber, AbstractListFragment fragment) {

        mRequestQueue.add(new CustomJsonObjectRequest(Request.Method.GET, mNetworkUrl + pageNumber, this, fragment, fragment));
    }


    protected Object jsonPojoDeserialization(JSONObject jsonObject, Class genericClass) throws IOException {
        return mObjectMapper.readValue(jsonObject.toString(), genericClass);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll(this);
    }
}
