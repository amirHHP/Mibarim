package com.mibarim.main.services;

import com.mibarim.main.RestInterfaces.RegistrationService;
import com.mibarim.main.models.ApiResponse;

import retrofit.RestAdapter;

public class RegisterService {
    private RestAdapter restAdapter;

    public RegisterService(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    public RestAdapter getRestAdapter() {
        return restAdapter;
    }

    private RegistrationService getRegistration() {

        return getRestAdapter().create(RegistrationService.class);
    }

    public ApiResponse register(String regMobile) {
        ApiResponse res=getRegistration().register(regMobile);
        return res;
    }
}

