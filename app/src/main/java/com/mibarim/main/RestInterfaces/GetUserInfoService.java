package com.mibarim.main.RestInterfaces;

import com.mibarim.main.core.Constants;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.ImageResponse;
import com.mibarim.main.models.InviteModel;
import com.mibarim.main.models.Plus.PaymentDetailModel;
import com.mibarim.main.models.ScoreModel;
import com.mibarim.main.models.UserInfoModel;
import com.mibarim.main.models.UserInitialModel;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by Hamed on 3/10/2016.
 */
public interface GetUserInfoService {

    @POST(Constants.Http.URL_SET_PERSON_INFO)
    @FormUrlEncoded
    ApiResponse SaveUserPersonalInfo(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                     @Field("Name") String name,
                                     @Field("Family") String family,
                                     @Field("NationalCode") String nationalCode,
                                     @Field("Gender") String gender,
                                     @Field("Email") String email,
                                     @Field("Code") String code);

    @POST(Constants.Http.URL_REGISTER_USER)
    @FormUrlEncoded
    ApiResponse registerUser(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Field("Name") String name,
                             @Field("Family") String family,
                             @Field("NationalCode") String nationalCode,
                             @Field("Gender") String gender,
                             @Field("Email") String email,
                             @Field("Code") String code);

    @POST(Constants.Http.URL_GET_USER_INFO)
    @FormUrlEncoded
    UserInfoModel getUserInfo(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                              @Field("UserId") String Id);

    @POST(Constants.Http.URL_SET_LICENSE_INFO)
    @FormUrlEncoded
    ApiResponse SaveLicenseInfo(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                @Field("LicenseNo") String licenseNo);

    @POST(Constants.Http.URL_SET_CAR_INFO)
    @FormUrlEncoded
    ApiResponse SaveCarInfo(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                            @Field("CarType") String carType,
                            @Field("CarColor") String carColor,
                            @Field("CarPlateNo") String carPlateNo
    );

    @POST(Constants.Http.URL_SET_BANK_INFO)
    @FormUrlEncoded
    ApiResponse SaveBankInfo(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Field("BankName") String bankName,
                             @Field("BankAccountNo") String bankAccountNo,
                             @Field("BankShaba") String bankShaba
    );


    @POST(Constants.Http.URL_SET_USER_INFO)
    @FormUrlEncoded
    ApiResponse SaveUserInfo(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Field("Name") String name,
                             @Field("Family") String family,
                             @Field("NationalCode") String nationalCode,
                             @Field("Gender") String gender,
                             @Field("Email") String email,
                             @Field("Code") String code,
                             @Field("CarType") String carType,
                             @Field("CarPlateNo") String carPlateNo,
                             @Field("carColor") String CarColor,
                             @Field("BankShaba") String BankShaba,
                             @Field("BankName") String BankName,
                             @Field("BankAccountNo") String BankAccount
    );


    @POST(Constants.Http.URL_GET_VERSION)
    @FormUrlEncoded
    ApiResponse GetVersion(@Field("UserId") String Id);

    @POST(Constants.Http.URL_GET_TRIP_ID)
    @FormUrlEncoded
    ApiResponse GetTripId(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                          @Field("UserId") String Id);

    @POST(Constants.Http.URL_SET_PERSON_Email)
    @FormUrlEncoded
    ApiResponse SaveUserEmailInfo(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                  @Field("Email") String email);

    @POST(Constants.Http.URL_SET_GOOGLE_TOKEN)
    @FormUrlEncoded
    ApiResponse SaveGoogleToken(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                @Field("Token") String googletoken,
                                @Field("OneSignalToken") String oneSignaltoken
                                );

    @POST(Constants.Http.URL_GET_IMAGE)
    @FormUrlEncoded
    ImageResponse GetImageById(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                               @Field("ImageId") String imageId);

    @POST(Constants.Http.URL_SET_DISCOUNT)
    @FormUrlEncoded
    ApiResponse submitDiscount(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                               @Field("DiscountCode") String discountCode,
                               @Field("ChargeAmount") long chargeAmount,
                               @Field("SeatPrice") long seatPrice);

    @POST(Constants.Http.URL_GET_DISCOUNT)
    @FormUrlEncoded
    ApiResponse getDiscounts(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Field("UserId") String userId);

    @POST(Constants.Http.URL_GET_WITHDRAW)
    @FormUrlEncoded
    ApiResponse getWithdrawRequests(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                    @Field("UserId") String userId);

    @POST(Constants.Http.URL_SET_ABOUT_ME)
    @FormUrlEncoded
    ApiResponse saveAboutMe(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                            @Field("Desc") String desc);

    @POST(Constants.Http.URL_SET_WITHDRAW)
    @FormUrlEncoded
    ApiResponse submitWithdrawRequest(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                      @Field("WithdrawAmount") String withdrawAmount);

    @POST(Constants.Http.URL_TOGGLE_CONTACT)
    @FormUrlEncoded
    ApiResponse toggleContactState(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                   @Field("ContactId") String contactId,
                                   @Field("State") boolean state
    );

    @POST(Constants.Http.URL_GET_INVITE)
    @FormUrlEncoded
    InviteModel getInvite(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                          @Field("UserId") String userId);

    @POST(Constants.Http.URL_SEND_FEEDBACK)
    @FormUrlEncoded
    ApiResponse sendFeedback(@Field("Name") String mobile,
                             @Field("Email") String email,
                             @Field("Text") String txt);

    @POST(Constants.Http.URL_GET_INITIAL_INFO)
    @FormUrlEncoded
    UserInitialModel getUserInitialInfo(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                        @Field("UserId") String userId);

    @POST(Constants.Http.URL_GET_SCORE)
    @FormUrlEncoded
    ScoreModel getUserScores(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken, @Field("Name") String fake);

    @POST(Constants.Http.URL_SEND_AMOUNT)
    @FormUrlEncoded
    PaymentDetailModel sendChargeAmount(@Field("Mobile") String mobile, @Field("ChargeAmount") String amount);

    /*@POST(Constants.Http.URL_GET_RATE)
    @FormUrlEncoded
    ApiResponse getRatings(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Field("TripId") long tripId);*/

    @POST(Constants.Http.URL_GET_RATINGS)
    @FormUrlEncoded
    ApiResponse getRatings(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                           @Field("Name") String name);

    @POST(Constants.Http.URL_SET_RATINGS)
    @FormUrlEncoded
    ApiResponse setRatings(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                           @Field("RatingsList") String ratingsList);

}
