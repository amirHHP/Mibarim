package com.mibarim.main.models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Alireza on 9/16/2017.
 */

public class RatingModel implements Serializable {
    public String UserUId;
    public String Name;
    public String Family;
    public String RateDescription;
    public int Rate;
    public int Presence; // 0 not present and 1 present obviously!
    public String ImageId;
    public long RateId;

    public void setRateId(long rateId) {
        RateId = rateId;
    }

    public long getRateId() {

        return RateId;
    }
    //    public RatingBar ratingBar;

    public void setImageBitmap(Bitmap imageBitmap) {
        ImageBitmap = imageBitmap;
    }

    public Bitmap getImageBitmap() {

        return ImageBitmap;
    }

    public Bitmap ImageBitmap;

    public RatingModel(String userUId, String name, String family, String rateDescription, int rate, int presence, String imageId) {
        UserUId = userUId;
        Name = name;
        Family = family;
        RateDescription = rateDescription;
        Rate = rate;
        Presence = presence;
        ImageId = imageId;
    }

    public void setUserUId(String userUId) {
        UserUId = userUId;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setFamily(String family) {
        Family = family;
    }

    public void setRateDescription(String rateDescription) {
        RateDescription = rateDescription;
    }

    public void setRate(int rate) {
        Rate = rate;
    }

    public void setPresence(int presence) {
        Presence = presence;
    }

    public void setImageId(String imageId) {
        ImageId = imageId;
    }

    public String getUserUId() {

        return UserUId;
    }

    public String getName() {
        return Name;
    }

    public String getFamily() {
        return Family;
    }

    public String getRateDescription() {
        return RateDescription;
    }

    public int getRate() {
        return Rate;
    }

    public int getPresence() {
        return Presence;
    }

    public String getImageId() {
        return ImageId;
    }

}
