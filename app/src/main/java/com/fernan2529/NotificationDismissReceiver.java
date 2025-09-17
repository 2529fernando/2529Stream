package com.fernan2529;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

public class NotificationDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notifId = intent.getIntExtra("notifId", -1);
        if (notifId != -1) {
            NotificationManagerCompat.from(context).cancel(notifId);
        }
    }
}
