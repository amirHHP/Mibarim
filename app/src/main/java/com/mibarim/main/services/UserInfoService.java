package com.mibarim.main.services;

import com.mibarim.main.RestInterfaces.GetUserInfoService;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.ImageResponse;
import com.mibarim.main.models.InviteModel;
import com.mibarim.main.models.Plus.PaymentDetailModel;
import com.mibarim.main.models.ScoreModel;
import com.mibarim.main.models.UserInfoModel;
import com.mibarim.main.models.UserInitialModel;

import retrofit.RestAdapter;

/**
 * Created by Hamed on 3/10/2016.
 */
public class UserInfoService {

    private RestAdapter restAdapter;

    public UserInfoService(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    public RestAdapter getRestAdapter() {
        return restAdapter;
    }

    private GetUserInfoService getService() {
        return getRestAdapter().create(GetUserInfoService.class);
    }


    public ApiResponse registerUser(String authToken, UserInfoModel personalInfoModel) {
        ApiResponse res = getService().registerUser("Bearer " + authToken,
                personalInfoModel.Name,
                personalInfoModel.Family,
                personalInfoModel.NationalCode,
                personalInfoModel.Gender,
                personalInfoModel.Email,
                personalInfoModel.Code
        );
        return res;
    }

    public UserInfoModel getUserInfo(String authToken) {
        UserInfoModel res = getService().getUserInfo("Bearer " + authToken, "7");
        return res;
    }

    public UserInitialModel getUserInitialInfo(String authToken) {
        UserInitialModel res = getService().getUserInitialInfo("Bearer " + authToken, "7");
        return res;
    }


    public ApiResponse AppVersion() {
        ApiResponse res = getService().GetVersion("7");
        return res;
    }


    public ApiResponse SaveGoogleToken(String authToken,String googletoken,String oneSignaltoken) {
        ApiResponse res = getService().SaveGoogleToken("Bearer " + authToken,googletoken,oneSignaltoken);
        return res;
    }

    public ImageResponse GetImageById(String token, String imageId) {
        ImageResponse res = getService().GetImageById("Bearer " + token, imageId);
        return res;
    }

    public InviteModel getInvite(String authToken) {
        InviteModel res = getService().getInvite("Bearer " + authToken, "7");
        return res;
    }

    public ApiResponse sendFeedback(String mobile, String txt) {
        ApiResponse res = getService().sendFeedback(mobile, "mobile@mibarim.com", txt);
        return res;
    }

    public ScoreModel getUserScores(String authToken) {
        ScoreModel res = getService().getUserScores("Bearer " + authToken,"1");
        return res;

    }

    public PaymentDetailModel sendChargeAmount(String mobileNumber, String amount){
        return getService().sendChargeAmount(mobileNumber, amount);
    }

    public ApiResponse submitDiscount(String authToken, String discountCode, long chargeAmount, long seatPrice) {
        ApiResponse res = getService().submitDiscount("Bearer " + authToken, discountCode, chargeAmount, seatPrice);
        return res;
    }

    /*public ApiResponse getRatings(String authToken, int tripId) {
        ApiResponse res=getService().getRatings("Bearer " + authToken, tripId);
        return res;
    }*/

    public ApiResponse getRatings(String authToken, String name) {
        ApiResponse res = getService().getRatings("Bearer " + authToken, name);
        return res;
    }

    public ApiResponse setRatings(String authToken, String list) {
        ApiResponse res = getService().setRatings("Bearer " + authToken, list);
        return res;
    }

}
