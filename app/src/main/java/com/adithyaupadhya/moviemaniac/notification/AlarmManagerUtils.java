package com.adithyaupadhya.moviemaniac.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by adithya.upadhya on 15-09-2016.
 */
public class AlarmManagerUtils {

    private static AlarmManagerUtils singletonInstance;

    private static final String RECEIVER_ACTION_NAME = "com.adithyaupadhya.moviemaniac.SET_NOTIFICATION_ALARM";
    private static final int PENDING_INTENT_ID = 1993;

    public static void createAlarmInstance(Context context) {
        if (singletonInstance == null) {
            singletonInstance = new AlarmManagerUtils(context);
        }

    }

    private AlarmManagerUtils(Context context) {

        Intent intent = new Intent(context, NotificationIntentService.class);

        intent.setAction(RECEIVER_ACTION_NAME);

        if (PendingIntent.getService(context, PENDING_INTENT_ID, intent, PendingIntent.FLAG_NO_CREATE) == null) {

            Log.d("MMVOLLEY", "Initializing Alarm Manager pending intent broadcast null");

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            PendingIntent pendingIntent = PendingIntent.getService(context, PENDING_INTENT_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 300 * 1000, pendingIntent);

        } else {
            Log.d("MMVOLLEY", "Alarm Manager already initialized, alarm already set.");
        }
    }
}
