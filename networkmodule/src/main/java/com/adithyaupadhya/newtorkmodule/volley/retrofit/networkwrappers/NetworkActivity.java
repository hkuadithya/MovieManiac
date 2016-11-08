package com.adithyaupadhya.newtorkmodule.volley.retrofit.networkwrappers;

import android.support.v7.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by adithya.upadhya on 05-11-2016.
 */

public abstract class NetworkActivity<APIResponseClass> extends AppCompatActivity implements Callback<APIResponseClass> {

    public abstract void onNetworkResponse(Call<APIResponseClass> call, Response<APIResponseClass> response);

    @Override
    public final void onResponse(Call<APIResponseClass> call, Response<APIResponseClass> response) {
        if (response != null && response.code() == 200)
            onNetworkResponse(call, response);
        else
            this.onFailure(call, null);
    }
}
