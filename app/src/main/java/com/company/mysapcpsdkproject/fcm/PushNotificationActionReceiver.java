package com.company.mysapcpsdkproject.fcm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.company.mysapcpsdkproject.R;

public class PushNotificationActionReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(context.getApplicationContext().getResources().getString(R.string.cancel))) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int id = intent.getIntExtra(RemoteNotificationConfig.CURRENT_NOTIFICATION_ID,-1);
            if (id >= 0) {
                notificationManager.cancel(id);
            }
        }
    }
}
