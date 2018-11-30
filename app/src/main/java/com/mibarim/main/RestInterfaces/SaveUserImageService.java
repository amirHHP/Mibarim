package com.mibarim.main.RestInterfaces;

import com.mibarim.main.core.Constants;
import com.mibarim.main.models.ApiResponse;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

//import com.squareup.okhttp.RequestBody;

/**
 * Created by Hamed on 3/10/2016.
 */
public interface SaveUserImageService {

    @POST(Constants.Http.URL_SEND_IMAGE)
    @FormUrlEncoded
    ApiResponse saveImage(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                          @Field("Base64Image") String Base64Image, @Field("ImageType") int i);

    @POST(Constants.Http.URL_SET_PERSON_IMAGE)
    @Multipart
    ApiResponse saveUserImage(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                              @Part("UserPic") TypedFile pic);

    @POST(Constants.Http.URL_SET_LICENSE_IMAGE)
    @Multipart
    ApiResponse saveLicenseImage(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                              @Part("LicensePic") TypedFile pic);

    @POST(Constants.Http.URL_SET_CAR_IMAGE)
    @Multipart
    ApiResponse saveCarImage(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                 @Part("CarPic") TypedFile pic);

    @POST(Constants.Http.URL_SET_CAR_BCK_IMAGE)
    @Multipart
    ApiResponse saveCarBckImage(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Part("CarPic") TypedFile pic);


    @POST(Constants.Http.URL_SET_NATIONAL_CARD_IMAGE)
    @Multipart
    ApiResponse SaveNationalCardImage(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Part("NationalCardPic") TypedFile pic);

    @POST(Constants.Http.URL_SET_BANK_CARD_IMAGE)
    @Multipart
    ApiResponse SaveBankCardImage(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                      @Part("BankCardPic") TypedFile pic);

}
