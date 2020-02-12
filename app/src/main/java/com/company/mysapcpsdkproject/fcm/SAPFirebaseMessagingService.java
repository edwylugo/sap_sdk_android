package com.company.mysapcpsdkproject.fcm;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Parcelable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.company.mysapcpsdkproject.R;
import com.company.mysapcpsdkproject.app.ErrorMessage;
import com.company.mysapcpsdkproject.app.SAPWizardApplication;

import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This service is responsible for handling the FCM remote notifications.
 */
public class SAPFirebaseMessagingService extends FirebaseMessagingService {

    public static final String NOTIFICATION_ID_EXTRA = "NotificationID";
    public static final String NOTIFICATION_DATA = "NotificationData";

    private static final Logger LOGGER = LoggerFactory.getLogger(SAPFirebaseMessagingService.class);

    private NotificationUtilities notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage != null) {
            LOGGER.debug("From: " + remoteMessage.getFrom());
            NotificationUtilities.processRemoteMessage(remoteMessage);
            String notificationID = remoteMessage.getData().get("mobileservices.notificationId");
            SAPWizardApplication sapWizardApplication = (SAPWizardApplication)getApplication();
            if (notificationID != null && !notificationID.isEmpty()) {
                PushNotificationUtilities pushUtils = new PushNotificationUtilities(this.getBaseContext().getApplicationContext(), sapWizardApplication.getErrorHandler(), sapWizardApplication.getSettingsParameters());
                pushUtils.setPushMessageStatus(notificationID, PushNotificationUtilities.NotificationStatus.received);
            }

            handleNotification(remoteMessage, notificationID);
        } else {
            LOGGER.error("null message arrived!");
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        LOGGER.debug("Push token:" + token);
        if (token != null && !token.isEmpty()) {
            // send to SAP backend if already registered
            SAPWizardApplication sapWizardApplication = (SAPWizardApplication)getApplication();
            if (sapWizardApplication.isOnboarded() ) {
                PushNotificationUtilities pushNotificationUtilities = new PushNotificationUtilities(this.getBaseContext(), sapWizardApplication.getErrorHandler(), sapWizardApplication.getSettingsParameters());
                pushNotificationUtilities.sendRegistrationToServer(token);
            }
        }
    }

    /**
     * Handles the notification.
     * @param notificationData The remote message from firebase.
     * @param notificationId The notification ID, it might be null if the message is from firebase console.
     */
    private void handleNotification(RemoteMessage notificationData, String notificationId) {
        // app is in foreground, broadcast the push message
        Intent pushNotification = new Intent(RemoteNotificationConfig.PUSH_NOTIFICATION);
        pushNotification.putExtra(NOTIFICATION_DATA, (Parcelable) notificationData);
        pushNotification.putExtra(NOTIFICATION_ID_EXTRA, notificationId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }
}
