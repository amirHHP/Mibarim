package com.mibarim.main.ui.activities.worker;

import android.util.Log;
import android.widget.LinearLayout;

import com.mibarim.main.core.Constants;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.services.NotificationInterface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by mohammad hossein on 24/01/2018.
 */

public class GenerateWorkerService {

    private RestAdapter adapter;
    private List<String> json;
    private List<String> jsonRequest;

    public GenerateWorkerService() {
        json = new ArrayList<>();
        adapter = new RestAdapter.Builder()
                .setEndpoint(Constants.Http.URL_BASE)
                .build();
    }
    public List<workerModel> services(workerModel workerModel,String authorization){
        ApiResponse response = adapter.create(workerServiceInterface.class).getWorkerService(workerModel,authorization);
        json = response.Messages;
        List<workerModel> models = new ArrayList<>();
        try {
            for (int i = 0;i<json.size();i++){
                String help = json.get(i);
                JSONObject jsonObject = new JSONObject(help);
                workerModel model = new workerModel();
                model.setWorker_Id(jsonObject.getInt("worker_Id"));
                model.setWorker_origin(jsonObject.getString("worker_origin"));
                model.setWorker_price(jsonObject.getString("worker_price"));
                model.setWorker_destination(jsonObject.getString("worker_destination"));
                model.setWorker_time(jsonObject.getString("worker_time"));
                model.setWorker_car_type(jsonObject.getString("worker_car_type"));
                models.add(model);
            }

        }catch (Exception e) {

        }
        return  models;

    }

    public void requestServices(int worker_Id,String authorization){
        ApiResponse response = adapter.create(workerServiceInterface.class).requestServices(worker_Id,authorization);
        jsonRequest = response.Messages;



    }
}
