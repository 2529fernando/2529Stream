package com.codingbeginner.sovary.myapplication;

import android.app.IntentService;
import android.content.Intent;

public class NotificationActionService extends IntentService {

    public NotificationActionService() {
        super("NotificationActionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(action);
                sendBroadcast(broadcastIntent);
            }
        }
    }
}