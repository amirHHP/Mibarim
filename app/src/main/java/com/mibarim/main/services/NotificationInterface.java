package com.mibarim.main.services;

import com.mibarim.main.MobileModel;
import com.mibarim.main.models.ApiResponse;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by mohammad hossein on 30/11/2017.
 */

public interface NotificationInterface {
    String URL_GET_NOTIFICATION = "/GetUserNotification";

    @POST(NotificationInterface.URL_GET_NOTIFICATION)
    ApiResponse getNotification(@Body MobileModel testing);


}

