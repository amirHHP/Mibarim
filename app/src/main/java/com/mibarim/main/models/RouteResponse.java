package com.mibarim.main.models;


import java.io.Serializable;
import java.util.List;


// for the older version of mibarim, not used anymore here

public class RouteResponse implements Serializable {

    public int RouteId;
    public String RouteUId;
    public String SrcAddress;
    public String SrcLatitude;
    public String SrcLongitude;
    public String DstAddress;
    public String DstLatitude;
    public String DstLongitude;
    public int AccompanyCount;
    public boolean IsDrive;
    public String TimingString;
    public String DateString;
    public String PricingString;
    public String CarString;
    public int SuggestCount;
    public String RouteRequestState;
    public int NewSuggestCount;
    public boolean Sat;
    public boolean Sun;
    public boolean Mon;
    public boolean Tue;
    public boolean Wed;
    public boolean Thu;
    public boolean Fri;
//    public List<BriefRouteModel> SuggestRoutes;

}
