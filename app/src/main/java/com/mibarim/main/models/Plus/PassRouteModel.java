package com.mibarim.main.models.Plus;


import java.io.Serializable;

/**
 * Created by Hamed on 4/6/2016.
 */
public class PassRouteModel implements Serializable {
    public int TripId;
    public int TripState; //
    public String Name;
    public String Family;
    public String MobileNo;
    public String UserImageId;
    public String  TimingString;
    public String  PricingString;
    public long  Price;
    public String  CarString;
    public String  CarPlate;
    public String SrcAddress;
    public String SrcLink;
    public String SrcDistance;
    public String SrcLatitude;
    public String SrcLongitude;
    public String DstAddress;
    public String DstLink;
    public String DstDistance;
    public String DstLatitude;
    public String DstLongitude;
    public int EmptySeats;
    public int CarSeats; // number of other passengers
    public boolean IsVerified;
    public boolean IsBooked;
}
