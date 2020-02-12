package com.company.mysapcpsdkproject.fcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.company.mysapcpsdkproject.R;
import com.company.mysapcpsdkproject.app.SAPWizardApplication;

import com.google.firebase.messaging.RemoteMessage;

import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler;
import com.sap.cloud.mobile.odata.json.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.company.mysapcpsdkproject.app.SAPWizardLifecycleObserver;
import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler;


/**
 * This class is registered by the application to receive FCM notifications.
 *
 */
public class PushNotificationBroadcastReceiver extends BroadcastReceiver {


	private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationBroadcastReceiver.class);

    private final SAPWizardLifecycleObserver sapWizardLifecycleObserver;

    private final SAPWizardApplication sapWizardApplication;

    public PushNotificationBroadcastReceiver(SAPWizardApplication sapWizardApplication, SAPWizardLifecycleObserver sapWizardLifecycleObserver) {
		this.sapWizardLifecycleObserver = sapWizardLifecycleObserver;
        this.sapWizardApplication = sapWizardApplication;
	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		String intentAction = intent.getAction();
		if (intentAction.equals(RemoteNotificationConfig.PUSH_NOTIFICATION)) {
			// The following section is a sample for handling push messages. This could be customized
			// according to the needs of the concrete application.
			// ---------------------start of suggested customization--------------------------
			final String notificationID = intent.getStringExtra(SAPFirebaseMessagingService.NOTIFICATION_ID_EXTRA);
            final RemoteMessage notificationMessage = intent.getParcelableExtra(SAPFirebaseMessagingService.NOTIFICATION_DATA);
            String textMsg = context.getResources().getString(R.string.push_text);
            String notificationTitle = context.getResources().getString(R.string.push_message);
            JSONObject jsonPayload = null;
            if (notificationMessage != null) {
                jsonPayload = new JSONObject(notificationMessage.getData());
                try {
                    notificationTitle = jsonPayload.getString("title");
                } catch (JSONException e) {
                   LOGGER.debug("Notification title is not present in the payload");
                }
                try {
                   textMsg  = jsonPayload.getString("alert");
                } catch (JSONException e) {
                   LOGGER.debug("Notification alert is not present in the payload");
                }
            }
			if (sapWizardLifecycleObserver.isAppInBackground()) {
				// background
				Intent pushActivityStarter = new Intent(context.getApplicationContext(), PushNotificationActivity.class);
                pushActivityStarter.putExtra(SAPFirebaseMessagingService.NOTIFICATION_ID_EXTRA, notificationID);
                pushActivityStarter.putExtra(SAPFirebaseMessagingService.NOTIFICATION_DATA, notificationMessage);
				NotificationUtilities notUtils = new NotificationUtilities(context.getApplicationContext());
                notUtils.showNotificationMessage(notificationTitle, textMsg, pushActivityStarter);
			} else {
				// foreground
				Activity foregroundActivity = AppLifecycleCallbackHandler.getInstance().getActivity();
                foregroundActivity.getIntent().putExtra(SAPFirebaseMessagingService.NOTIFICATION_ID_EXTRA, notificationID);
                foregroundActivity.getIntent().putExtra(SAPFirebaseMessagingService.NOTIFICATION_DATA, notificationMessage);
                PushNotificationActivity.presentPushMessage(foregroundActivity, notificationID);
			}
			LOGGER.debug(textMsg);
			// ------------------end of suggested customization------------------------------
		}
	}
}
