package com.mibarim.main.core;

import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.ImageResponse;
import com.mibarim.main.models.TokenResponse;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * User service for connecting the the REST API and
 * getting the users.
 */
public interface UserService {

//    @GET(Constants.Http.URL_USERS_FRAG)
//    UsersWrapper getUsers();

    /**
     * The {@link retrofit.http.Query} values will be transform into query string paramters
     * via Retrofit
     *
     * @param mobile    The users email
     * @param password The users password
     * @return A login response.
     * request.AddParameter("username", loginmodel.Loginmobile);
     * request.AddParameter("password", loginmodel.Pass);
     * request.AddParameter("grant_type", "password");
     * request.AddParameter("response_type", "token");
     */
    @FormUrlEncoded
    @POST(Constants.Http.URL_AUTH_FRAG)
    TokenResponse authenticate(@Field(Constants.Http.PARAM_USERNAME) String mobile,
                      @Field(Constants.Http.PARAM_PASSWORD) String password,
                      @Field(Constants.Http.PARAM_GRANT_TYPE) String grant_type,
                      @Field(Constants.Http.PARAM_RESPONSE_TYPE) String response_type);

    @FormUrlEncoded
    @POST(Constants.Http.URL_MOBILE_CONFIRM_FRAG)
    boolean validateMobile(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                           @Field("Mobile") String mobile);
    @FormUrlEncoded
    @POST(Constants.Http.URL_MOBILE_SEND_SMS)
    boolean sendValidateSms(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                           @Field("Mobile") String mobile,
                            @Field("SendCounter") int Count);
    @FormUrlEncoded
    @POST(Constants.Http.URL_MOBILE_CONFIRM_SMS)
    ApiResponse validateMobileSms(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                  @Field("Mobile") String mobile,
                                  @Field("ValidationCode") String vCode);


    ImageResponse GetImageById(String token, String imageId);
}
