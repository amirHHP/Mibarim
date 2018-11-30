package com.mibarim.main.ui.activities.worker;

import com.mibarim.main.models.ApiResponse;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by mohammad hossein on 24/01/2018.
 */

public interface workerServiceInterface {

    String URL_WORKER_SERVICES = "/GetCurrentRoutes";
    String URL_REQUEST_SERVICES = "/AcceptCurrentRoutes";

    @POST(workerServiceInterface.URL_WORKER_SERVICES)
    ApiResponse getWorkerService(@Body workerModel workerModel,@Header("authorization") String authorization);

    @POST(workerServiceInterface.URL_REQUEST_SERVICES)
    @FormUrlEncoded
    ApiResponse requestServices(@Field("worker_Id") int worker_Id ,@Header("authorization") String authorization);
}
