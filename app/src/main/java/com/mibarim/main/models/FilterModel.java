package com.mibarim.main.models;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Alireza on 11/20/2017.
 */

public class FilterModel implements Serializable {
    public long FilterId;
    public long SrcStationId;
    public String SrcStation;
    public String SrcStLat;
    public String SrcStLng;
    public long DstStationId;
    public String DstStation;
    public String DstStLat;
    public String DstStLng;
    public String TimeString;
    public Boolean IsActive;

//    public Calendar Time;
    public int TimeHour;
    public int TimeMinute;
    public Boolean IsAlert;

}
