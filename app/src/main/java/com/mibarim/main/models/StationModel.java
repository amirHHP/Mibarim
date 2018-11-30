package com.mibarim.main.models;

import java.io.Serializable;

/**
 * Created by Alireza on 10/8/2017.
 */

public class StationModel implements Serializable {
    public String Name;
    public String StLat;
    public String StLng;
    public long MainStationId;

    public void setName(String name) {
        Name = name;
    }

    public void setStLat(String stLat) {
        StLat = stLat;
    }

    public void setStLng(String stLng) {
        StLng = stLng;
    }

    public void setMainStationId(long mainStationId) {
        MainStationId = mainStationId;
    }
}
