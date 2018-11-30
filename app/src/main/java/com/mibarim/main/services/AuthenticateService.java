
package com.mibarim.main.services;

import com.mibarim.main.core.UserService;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.TokenResponse;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Bootstrap API service
 */
public class
        AuthenticateService {

    private RestAdapter restAdapter;

    /**
     * Create bootstrap service
     * Default CTOR
     */
    public AuthenticateService() {
    }

    /**
     * Create bootstrap service
     *
     * @param restAdapter The RestAdapter that allows HTTP Communication.
     */
    public AuthenticateService(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    private UserService getUserService() {
        return getRestAdapter().create(UserService.class);
    }

    private RestAdapter getRestAdapter() {
        return restAdapter;
    }

    public TokenResponse authenticate(String mobile, String password,String grantType,String responseType) {
        return getUserService().authenticate(mobile, password, grantType, responseType);
    }

    public boolean confirmMobile(String authToken,String mobile) {
        return getUserService().validateMobile("Bearer " + authToken, mobile);
    }

    public ApiResponse confirmMobileSms(String authToken, String mobile, String vCode) {
        return getUserService().validateMobileSms("Bearer " + authToken, mobile, vCode);
    }
    public boolean sendValidateSms(String authToken,String mobile,int count) {
        return getUserService().sendValidateSms("Bearer " + authToken, mobile,count);
    }

}