package com.company.mysapcpsdkproject.fcm;

import android.content.Context;
import android.content.res.Resources;

import com.company.mysapcpsdkproject.R;
import com.company.mysapcpsdkproject.app.ErrorHandler;
import com.company.mysapcpsdkproject.app.ErrorMessage;

import com.sap.cloud.mobile.foundation.common.ClientProvider;
import com.sap.cloud.mobile.foundation.common.SettingsParameters;
import com.sap.cloud.mobile.foundation.remotenotification.RemoteNotificationClient;
import com.sap.cloud.mobile.foundation.remotenotification.RemoteNotificationParameters;

import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * This class contains utilities for uploading FCM tokens to the SAP backend.
 */
public class PushNotificationUtilities {

	public enum NotificationStatus{
		received, consumed
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationUtilities.class);
	private RemoteNotificationClient remoteNotificationClient;
  
	private static final String TOKEN_REG_SUCCESS = "Device token successfully registered to CP server";
    private static final String TOKEN_REG_FAILURE = "Failed to register device token to CP server";
    private static final String TOKEN_DEL_SUCCESS = "Device token successfully removed from CP server";
    private static final String TOKEN_DEL_FAILURE = "Failed to remove device token from CP server";
	private static final String NOT_FB_SUCCESS = "Notification is acknowledged to server";
	private static final String NOT_FB_FAILURE = "Failed to acknowledge notification";
	private static final String NOT_CLIENT_ERROR = "Notification client couldn't be created";
	
	public PushNotificationUtilities(Context context, ErrorHandler errorHandler, SettingsParameters settingsParameters) {
	
		if (settingsParameters != null) {
			try {
				this.remoteNotificationClient = new RemoteNotificationClient(ClientProvider.get(), settingsParameters);
			} catch (MalformedURLException e) {
				Resources res = context.getResources();
				ErrorMessage errorMessage = new ErrorMessage(res.getString(R.string.push_error), res.getString(R.string.push_token_upload_failed_detail));
				errorHandler.sendErrorMessage(errorMessage);
			}
		}
	}

	public void sendRegistrationToServer(String token) {

		if (remoteNotificationClient != null) {
			RemoteNotificationParameters parameter = new RemoteNotificationParameters();
			remoteNotificationClient.registerDeviceToken(token, parameter, new RemoteNotificationClient.CallbackListener() {
				@Override
				public void onSuccess() {
					LOGGER.info(TOKEN_REG_SUCCESS);
				}

				@Override
				public void onError(Throwable throwable) {
					LOGGER.error(TOKEN_REG_FAILURE, throwable);
				}
			});
		} else {
				LOGGER.error(NOT_CLIENT_ERROR);
		}
	}
	
	public void removeRegistrationFromServer(PushCallback callback) {
		if (remoteNotificationClient != null) {
			remoteNotificationClient.unregisterDeviceToken(new RemoteNotificationClient.CallbackListener() {
				@Override
				public void onSuccess() {
				    callback.onNotifyResult();
					LOGGER.info(TOKEN_DEL_SUCCESS);
				}

				@Override
				public void onError(Throwable throwable) {
				    callback.onNotifyResult();
					LOGGER.error(TOKEN_DEL_FAILURE, throwable);
				}
			});
		} else {
			LOGGER.error(NOT_CLIENT_ERROR);
		}
    }


	public void setPushMessageStatus(String notificationId, NotificationStatus status) {

		if (remoteNotificationClient != null) {
			remoteNotificationClient.updateNotificationStatus(notificationId, status.name(), new RemoteNotificationClient.CallbackListener() {
				@Override
				public void onSuccess() {
					LOGGER.error(NOT_FB_SUCCESS);
				}

				@Override
				public void onError(Throwable throwable) {
					LOGGER.error(NOT_FB_FAILURE, throwable);
				}
			});
		} else {
			LOGGER.error(NOT_CLIENT_ERROR);
		}
	}

     /**
	 * This is a customization function that can be used to handle the push notification further in
	 * the application.
	 * @param notificationMessage A message string that represent the payload received in the push
	 *                            notification.
	 */
    public void processPushNotificationPayload(String notificationMessage) {
        /*** Fill in this function for custom processing of push notification **/
        try {
            JSONObject notification = new JSONObject(notificationMessage);
            /** your code to process the message **/
        } catch (JSONException e) {
            LOGGER.debug("Error  parsing notification message");
        }
    }
}
