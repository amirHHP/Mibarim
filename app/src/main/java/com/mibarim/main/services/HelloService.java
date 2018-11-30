package com.mibarim.main.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.os.Process;


import com.mibarim.main.MobileModel;
import com.mibarim.main.R;
import com.mibarim.main.models.NotifModel;
import com.mibarim.main.ui.activities.MainActivity;
import com.mibarim.main.util.SafeAsyncTask;

/**
 * Created by mohammad hossein on 30/11/2017.
 */

public class HelloService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            sendRequest();

            stopSelf(msg.arg1);
        }

        public void sendRequest() {

            new SafeAsyncTask<Boolean>() {

                @Override
                public Boolean call() throws Exception {

                    SharedPreferences sharedPreferences = getSharedPreferences("com.mibarim.main", Context.MODE_PRIVATE);
                    final String number = sharedPreferences.getString("UserMobile", "");
                    MobileModel testing = new MobileModel();
                    testing.setMobile(number, MobileModel.NotificationType.NotifForFilter);

                    NotifModel notifModel = new NotifModel();
                    notifModel.getNotif(testing);
                    String title = notifModel.getTitle();
                    String body = notifModel.getBody();

                    setNotif(title, body);

                    return true;
                }

                @Override
                protected void onFinally() throws RuntimeException {
                    super.onFinally();
                }

                @Override
                protected void onException(Exception e) throws RuntimeException {
                    super.onException(e);
                }

                @Override
                protected void onSuccess(Boolean aBoolean) throws Exception {
                    super.onSuccess(aBoolean);
                }
            }.execute();
        }

        public void setNotif(String title, String body) {
            if (title != null && body != null) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.mipmap.ic_circle_logo)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(body))
                                .setContentIntent(pIntent)
                                .setSound(soundUri);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());

                /*boolean isScreenOn;

                PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH)
                    isScreenOn = pm.isScreenOn();
                else
                    isScreenOn = pm.isInteractive();

                if (!isScreenOn) {
                    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
                    wl.acquire(2000);

                    wl.release();
                }*/

            }


        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!haveNetworkConnection())
            stopSelf();
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet

            return true;
        } else {
            return false;
        }
    }

}
