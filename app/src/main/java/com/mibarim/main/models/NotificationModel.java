package com.mibarim.main.models;

import java.io.Serializable;

/**
 * Created by Hamed on 5/7/2016.
 */
public class NotificationModel implements Serializable {
    public Long SuggestRouteRequestId;
    public Long MessageRouteRequestId;
    public boolean IsNewRouteSuggest;
    public boolean IsNewMessage;
}
