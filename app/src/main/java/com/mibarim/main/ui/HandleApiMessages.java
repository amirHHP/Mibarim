package com.mibarim.main.ui;

import android.app.Activity;

import com.mibarim.main.R;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.MessageResponse;
import com.mibarim.main.util.Toaster;

/**
 * Created by Hamed on 3/1/2016.
 */
public class HandleApiMessages {
    protected ApiResponse _apiResponse;
    protected Activity _activity;

    public HandleApiMessages(Activity activity, ApiResponse response){
        _activity = activity;
        _apiResponse = response;
    }
    public void showMessages() {
        showErrors();
        showWarnings();
        showInfos();
    }

    protected void showErrors() {
        if (_apiResponse.Errors!=null && _apiResponse.Errors.size() > 0) {
            for (MessageResponse error : _apiResponse.Errors) {
                Toaster.showLong(_activity, error.Message);
            }
        }
    }
    protected void showInfos() {
        if (_apiResponse.Infos!=null && _apiResponse.Infos.size() > 0) {
            for (MessageResponse info : _apiResponse.Infos) {
                Toaster.showLong(_activity, info.Message);
            }
        }
    }
    protected void showWarnings() {
        if (_apiResponse.Warnings!=null &&_apiResponse.Warnings.size() > 0) {
            for (MessageResponse warn : _apiResponse.Warnings) {
                Toaster.showLong(_activity, warn.Message);
            }
        }
    }
}
