package com.just.justbikev2.Remote;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClientAdmin {
    private static Retrofit retrofit = null;
    private static Retrofit instance = null;

    public static Retrofit getInstance(){
        return instance == null?
                new Retrofit.Builder().baseUrl("https://maps.googleapis.com/")
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                        .build() : instance;
    }

    public static Retrofit getClient(String baseUrl){
        if(retrofit == null)
            retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create()).build();
        return retrofit;
    }
}
