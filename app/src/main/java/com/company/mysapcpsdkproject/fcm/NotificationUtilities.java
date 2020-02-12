package com.company.mysapcpsdkproject.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.RemoteMessage;
import android.text.TextUtils;


import com.company.mysapcpsdkproject.R;

/**
 * This class contains necessary functions required to show the remote notification
 * (with title, message, image and timestamp) in notification tray.
 */
public class NotificationUtilities {

    private static final String PUSH_CHANNEL = "my_push_channel";

    private Context context;
    private String negativeButton;

    /**
     * id to handle the notifications in the notification tray.
     * This is declared static intentionally so that increment of this variable is shared with multiple background notifications.
     * Each time a background notification is received, this value will be incremented to keep a unique id for the intents
     */
    private static int notificationIdStart = 100;

    public NotificationUtilities(Context context) {
        this.context = context;
        negativeButton = context.getApplicationContext().getResources().getString(R.string.cancel);
    }

    public void showNotificationMessage(final String title, final String message, Intent intent) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        // status bar icon
        final int smallIcon = R.mipmap.ic_statusbar;

        // notification bar icon
        final int largeIcon = R.mipmap.ic_launcher;
        intent.putExtra(RemoteNotificationConfig.CURRENT_NOTIFICATION_ID,notificationIdStart );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent resultPendingIntent =
           PendingIntent.getActivity(
                 context,
                 notificationIdStart,
                 intent,
                 PendingIntent.FLAG_UPDATE_CURRENT
            );

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // versions >= 26 we need to have notification channel, as well
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // user-visible name of the channel.
            CharSequence name = "push-channel";
            // user-visible description of the channel.
            String description = "notification channel for push messages";
            // importance
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(PUSH_CHANNEL, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            // sets the notification light color for notifications posted to this
            // channel, if the device supports this feature
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, PUSH_CHANNEL);

        showSmallNotification(notificationBuilder, smallIcon, largeIcon, title, message, resultPendingIntent);
    }


    private void showSmallNotification(NotificationCompat.Builder notificationBuilder, int smallIcon, int largeIcon, String title, String message, PendingIntent resultPendingIntent) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Intent intentCancel = new Intent(context, PushNotificationActionReceiver.class);
        intentCancel.setAction(negativeButton);
        intentCancel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentCancel.putExtra(RemoteNotificationConfig.CURRENT_NOTIFICATION_ID,notificationIdStart );
        //This Intent will be called when Cancel button from notification will be clicked by user.
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, notificationIdStart, intentCancel, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = notificationBuilder
                .setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                .setTicker(title)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(inboxStyle)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentText(message)
                .addAction(R.drawable.ic_close_black_24dp, negativeButton, pendingIntentCancel)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationIdStart++, notification);
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    /**
     * Process the remote message first before giving it to the receiver to handle, if the message is from
     * firebase, put the 'title' and 'body' from notification into the 'data', then the receiver can handle
     * the message like it's from the mobile service API.
     */
    static void processRemoteMessage(RemoteMessage message) {
        if (message.getData().isEmpty() && message.getNotification() != null) {
            if(message.getNotification().getTitle() != null) {
                message.getData().put("title", message.getNotification().getTitle());
            }
            message.getData().put("alert", message.getNotification().getBody());
        }
    }
}
