package com.just.justbikev2.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGeoCoordinates {

    @GET("maps/api/geocode/json")
    Call<String> getCode(@Query("address") String address ,@Query("key") String key);

    @GET("maps/api/directions/json")
    Call<String> getDirections(@Query("origin") String origin , @Query("destination") String destination,@Query("key") String key);
}
