package com.mibarim.main.models.enums;

/**
 * Created by Hamed on 3/8/2016.
 */
public enum TripStates {

    Scheduled("Scheduled", 1),
    CanceledByUser("CanceledByUser", 5),
    InPreTripTime("InPreTripTime", 10),
    InTripTime("InTripTime", 15),
    InRiding("InRiding", 20),
    PassengerCall("PassengerCall", 25),
    InDriving("InDriving", 40),
    InRanking("InRanking", 45);

    private String stringValue;
    private int intValue;

    private TripStates(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public int toInt(){return intValue;}
}


