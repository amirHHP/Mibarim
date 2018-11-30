package com.mibarim.main.models;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Alireza on 11/22/2017.
 */

public class FilterTimeModel implements Serializable{
    public long FilterId;
    public int PairPassengers;
    public long Price;
    public String PriceString;
//    public Calendar Time;
    public String TimeString;
    public int TimeHour;
    public int TimeMinute;
    public boolean IsManual;
}
