package com.just.justbikev2.Remote;

import com.just.justbikev2.Model.MyResponse;
import com.just.justbikev2.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAVr2o9wQ:APA91bEcft5vwQDhzGDmLr65AQe4V5uWLj2CqnBL1YrPCpStaXBR1LbbimiH57pCDB87TOFeMMkJzAEQC-x8bvjORcWcVJvKg70X7qtyaf3qQw_7gupaj2oMGGkurr9_1mmA0xyYgkhd"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);   //Observable<FCMResponse>
}
