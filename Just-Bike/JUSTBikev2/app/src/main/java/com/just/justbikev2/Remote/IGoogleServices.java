package com.just.justbikev2.Remote;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleServices {
    @GET
    Call<JsonObject> getAddress(@Url String url);

}
