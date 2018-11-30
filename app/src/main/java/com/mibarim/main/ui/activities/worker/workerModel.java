package com.mibarim.main.ui.activities.worker;

import java.io.Serializable;

/**
 * Created by mohammad hossein on 22/01/2018.
 */

public class workerModel implements Serializable {
    public int worker_Id;
    public String worker_origin;
    public String worker_price;
    public String worker_destination;
    public String worker_time;
    public String worker_car_type;

    public int getWorker_Id() {
        return worker_Id;
    }

    public void setWorker_Id(int worker_Id) {
        this.worker_Id = worker_Id;
    }

    public String getWorker_origin() {
        return worker_origin;
    }

    public void setWorker_origin(String worker_origin) {
        this.worker_origin = worker_origin;
    }

    public String getWorker_price() {
        return worker_price;
    }

    public void setWorker_price(String worker_price) {
        this.worker_price = worker_price;
    }

    public String getWorker_destination() {
        return worker_destination;
    }

    public void setWorker_destination(String worker_destination) {
        this.worker_destination = worker_destination;
    }

    public String getWorker_time() {
        return worker_time;
    }

    public void setWorker_time(String worker_time) {
        this.worker_time = worker_time;
    }

    public String getWorker_car_type() {
        return worker_car_type;
    }

    public void setWorker_car_type(String worker_car_type) {
        this.worker_car_type = worker_car_type;
    }
}
