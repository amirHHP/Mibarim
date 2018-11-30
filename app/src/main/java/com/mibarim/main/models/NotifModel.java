package com.mibarim.main.models;


import com.mibarim.main.MobileModel;

import java.io.Serializable;

/**
 * Created by mohammad hossein on 30/11/2017.
 */

public class NotifModel implements Serializable {

    public String Title;
    public String EncodedTitle;
    public String Body;
    public String EncodedBody;
    public String Url;
    public String Action;
    public int RequestCode;
    public long NotificationId;
    public int Tab;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getEncodedTitle() {
        return EncodedTitle;
    }

    public void setEncodedTitle(String encodedTitle) {
        EncodedTitle = encodedTitle;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getEncodedBody() {
        return EncodedBody;
    }

    public void setEncodedBody(String encodedBody) {
        EncodedBody = encodedBody;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public int getRequestCode() {
        return RequestCode;
    }

    public void setRequestCode(int requestCode) {
        RequestCode = requestCode;
    }

    public long getNotificationId() {
        return NotificationId;
    }

    public void setNotificationId(long notificationId) {
        NotificationId = notificationId;
    }

    public int getTab() {
        return Tab;
    }

    public void setTab(int tab) {
        Tab = tab;
    }

    public void getNotif(MobileModel model){
        NotifModel notifModel;
        notifModel = new GenerateNotif().Notification(model);

        setTitle(notifModel.Title);
        setBody(notifModel.Body);

    }
}

