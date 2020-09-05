package com.lifeassistance.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallTaskProgressService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        context.startService(new Intent(context, TaskProgressService.class));
    }
}
