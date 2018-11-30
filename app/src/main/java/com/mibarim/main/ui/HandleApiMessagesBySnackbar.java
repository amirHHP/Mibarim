package com.mibarim.main.ui;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.MessageResponse;

/**
 * Created by Hamed on 3/1/2016.
 */
public class HandleApiMessagesBySnackbar {
    protected ApiResponse _apiResponse;
    protected View _view;

    public HandleApiMessagesBySnackbar(View view, ApiResponse response){
        _view = view;
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
                Snackbar.make(_view,error.Message,Snackbar.LENGTH_LONG).show();
                //Toaster.showLong(_activity, error.Message, R.drawable.toast_error);
            }
        }
    }
    protected void showInfos() {
        if (_apiResponse.Infos!=null && _apiResponse.Infos.size() > 0) {
            for (MessageResponse info : _apiResponse.Infos) {
                Snackbar.make(_view,info.Message,Snackbar.LENGTH_LONG).show();
                //Toaster.showLong(_activity, info.Message,R.drawable.toast_info);
            }
        }
    }
    protected void showWarnings() {
        if (_apiResponse.Warnings!=null &&_apiResponse.Warnings.size() > 0) {
            for (MessageResponse warn : _apiResponse.Warnings) {
                Snackbar.make(_view,warn.Message,Snackbar.LENGTH_LONG).show();
                //Toaster.showLong(_activity, warn.Message, R.drawable.toast_warn);
            }
        }
    }
}
