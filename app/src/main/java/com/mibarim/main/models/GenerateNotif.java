package com.mibarim.main.models;

import android.content.Context;
import android.content.SharedPreferences;


import com.mibarim.main.MobileModel;
import com.mibarim.main.core.Constants;
import com.mibarim.main.services.NotificationInterface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by mohammad hossein on 30/11/2017.
 */

public class GenerateNotif {
    private RestAdapter adapter;
    private List<String> json;

    public GenerateNotif() {
        json = new ArrayList<>();
        adapter = new RestAdapter.Builder()
                .setEndpoint(Constants.Http.URL_BASE)
                .build();
    }
    public NotifModel Notification(MobileModel model){
        ApiResponse response = adapter.create(NotificationInterface.class).getNotification(model);
        json = response.Messages;
        NotifModel notifModel = new NotifModel();
        try {
            JSONObject mainObject = new JSONObject(json.get(0));
            notifModel.setTitle(mainObject.getString("Title"));
            notifModel.setBody(mainObject.getString("Body"));
        }catch (Exception e) {

        }
        return  notifModel;

    }
}