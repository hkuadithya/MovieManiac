package com.adithyaupadhya.moviemaniac.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppBootReceiver extends BroadcastReceiver {
    public AppBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d("MMVOLLEY", "Boot Receiver, Creating alarm manager instance.");
            AlarmManagerUtils.createAlarmInstance(context);
        }
    }
}
