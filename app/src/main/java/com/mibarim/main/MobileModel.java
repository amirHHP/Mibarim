package com.mibarim.main;


import java.io.Serializable;

/**
 * Created by mohammad hossein on 30/11/2017.
 */

public class MobileModel implements Serializable {
    public enum NotificationType
    {
        NotifForFilter(20),
        NotifForDriver(21);

        private int value;

        NotificationType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private String mobile;
    private int NotifType;

    public int getNotifType() {
        return NotifType;
    }

    public void setNotifType(int notifType) {
        NotifType = notifType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile,NotificationType status) {
        this.mobile = mobile;
        setNotifType(status.getValue());
    }
}

