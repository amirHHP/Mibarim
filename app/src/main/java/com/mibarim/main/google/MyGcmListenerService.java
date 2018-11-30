/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mibarim.main.google;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.mibarim.main.R;
import com.mibarim.main.core.Constants;
import com.mibarim.main.ui.activities.SplashActivity;

public class
        MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        int tabNum=0;
        int requestCodeNum=0;
        int notificationIdNum=0;
        String title = data.getString("title");
        String body = data.getString("body");
        String action = data.getString("action");
        String requestCode = data.getString("requestCode");
        String notificationId = data.getString("notificationId");
        String tab = data.getString("tab");
        String message = data.getString("message");
        String link = data.getString("link");
        try {
            tabNum = Integer.parseInt(tab);
            requestCodeNum = Integer.parseInt(requestCode);
            notificationIdNum = Integer.parseInt(notificationId);
        } catch(NumberFormatException nfe) {

        }
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "body: " + body);
        showNotification(title,body,action,tabNum,requestCodeNum,notificationIdNum,link);
/*        if (message != null && !message.equals("")) {
            sendNotification(message);
        } else {
            showNotification(title,body,action,tabNum,requestCodeNum,notificationIdNum,link);
        }*/

    }


    private void showNotification(String title, String body, String action, int tab, int requestCode, int notificationId, String link) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.GlobalConstants.URL, link);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_circle_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    /*private void sendNotification(String message) {
        if (message.equals("SuggestRoute")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selectedTab", MainTabs.Route.toInt());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle("پیشنهاد جدید")
                    .setContentText("شما هم مسیر جدیدی در پیشنهادات خود دارید")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());
            //startActivity(intent);
        } else if (message.equals("RideShareRequest")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selectedTab", MainTabs.Message.toInt());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle("پیشنهاد هم سفری")
                    .setContentText("پیشنهادی برای هم سفری با شما در پیام ها ارسال شده است")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(1 *//* ID of notification *//*, notificationBuilder.build());
        } else if (message.equals("RideShareAccepted")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selectedTab", MainTabs.Message.toInt());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 2 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle("درخواست هم سفری قبول شد")
                    .setContentText("می توانید با کاربر قرار ملاقات بگذارید")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(2 *//* ID of notification *//*, notificationBuilder.build());
        } else if (message.equals("NewMessage")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selectedTab", MainTabs.Message.toInt());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 3 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle("پیام جدید")
                    .setContentText("پیام جدید از طرف هم سفر شما ارسال شده است")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(3 *//* ID of notification *//*, notificationBuilder.build());
        } else if (message.equals("NewTrip")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selectedTab", MainTabs.Route.toInt());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 4 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle("سفر تایید شد")
                    .setContentText("قبل از آغاز سفر به شما اطلاع داده خواهد شد")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(4 *//* ID of notification *//*, notificationBuilder.build());
        } else if (message.equals("StartTrip")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selectedTab", MainTabs.Route.toInt());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 4 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle("زمان آغاز سفر")
                    .setContentText("لطفا برنامه می بریم را باز کنید")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(4 *//* ID of notification *//*, notificationBuilder.build());
        } else if (message.equals("NewDriver")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selectedTab", MainTabs.Message.toInt());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 5 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle("راننده جدید")
                    .setContentText("راننده جدید ثبت شد")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(5 *//* ID of notification *//*, notificationBuilder.build());
        } else if (message.equals("NewEvent")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selectedTab", 1);//MainTabs.Event.toInt()
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 6 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle("رویداد جدید")
                    .setContentText("رویداد جدید ثبت شده است")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(6 *//* ID of notification *//*, notificationBuilder.build());
        } else if (message.equals("GiftInvite")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selectedTab", MainTabs.Profile.toInt());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 7 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle("دعوت از دوستان")
                    .setContentText("کد تخفیف دعوت از دوستان برای شما فعال شد")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(7 *//* ID of notification *//*, notificationBuilder.build());
        }

    }
*/
}
