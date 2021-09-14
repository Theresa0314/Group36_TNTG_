package com.mobdeve.group36.Data.firebase;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface API{
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA3RUjeQw:APA91bG_YT2o_gT5WmihQ7VOWpxSioHuCb5kFKyVsrQAESwfBmTJvW3w39aTAgiK6gFwzXXNbecVmuYcUICOOScqrkavklHtkpaxgKiOMIYj9SPtN3uXrSRN4sMqnrDa1hqfQrbS8RBn"
            }
    )

    @POST("fcm/send")
    Call<UserResponse> sendNotification(@Body MessageSent body);
}
