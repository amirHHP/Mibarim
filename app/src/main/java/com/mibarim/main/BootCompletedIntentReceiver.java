package com.mibarim.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mibarim.main.core.Constants;
import com.mibarim.main.services.HelloService;
import com.mibarim.main.ui.activities.MainActivity;
import com.mibarim.main.ui.activities.RidingActivity;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mohammad hossein on 11/12/2017.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    SharedPreferences PrefGPS;

    @Override
    public void onReceive(Context context, Intent i) {

        PrefGPS = context.getSharedPreferences("taximeter", MODE_PRIVATE);
        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        cur_cal.add(Calendar.SECOND, 10);
        Intent intent = new Intent(context, HelloService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm_manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm_manager.set(AlarmManager.RTC, cur_cal.getTimeInMillis(), pi);
        alarm_manager.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), 30 * 60 * 1000, pi);

    }
}