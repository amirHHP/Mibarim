package com.mibarim.main.RestInterfaces;

import com.mibarim.main.core.Constants;
import com.mibarim.main.models.ApiResponse;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface RegistrationService {

    @POST(Constants.Http.URL_REGISTER_FRAG)
    @FormUrlEncoded
    ApiResponse register(@Field(Constants.Http.PARAM_REG_USERNAME) String mobile);
}
