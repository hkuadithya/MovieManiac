package com.adithyaupadhya.newtorkmodule.volley.retrofit.networkwrappers;

import android.support.v4.app.Fragment;

import com.adithyaupadhya.newtorkmodule.volley.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by adithya.upadhya on 05-11-2016.
 */

public abstract class NetworkFragment<APIResponseClass> extends Fragment implements Callback<APIResponseClass> {

    protected abstract void onNetworkResponse(Call<APIResponseClass> call, Response<APIResponseClass> response);

    protected abstract void onNetworkFailure(Call<APIResponseClass> call, Throwable t);

    @Override
    public final void onResponse(Call<APIResponseClass> call, Response<APIResponseClass> response) {
        if (response != null && response.code() == 200)
            onNetworkResponse(call, response);
        else
            this.onFailure(call, null);
    }

    @Override
    public void onFailure(Call<APIResponseClass> call, Throwable t) {
        boolean isCancelled = t != null && t.getCause() != null && t.getCause().getMessage() != null
                && t.getCause().getMessage().equalsIgnoreCase("Canceled");

        if (call != null && (!call.isCanceled() || !isCancelled)) {
            onNetworkFailure(call, t);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RetrofitClient.getInstance().cancelAllRequests();
    }
}
