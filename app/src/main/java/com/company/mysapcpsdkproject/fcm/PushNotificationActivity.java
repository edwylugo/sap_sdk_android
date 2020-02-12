package com.company.mysapcpsdkproject.fcm;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.company.mysapcpsdkproject.R;
import com.company.mysapcpsdkproject.app.SAPWizardApplication;
import com.company.mysapcpsdkproject.logon.LogonActivity;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity which serves as a transparent background for push message dialogs. Using an activity helps
 * to show the dialog on top of the current activity stack.
 */
public class PushNotificationActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String notificationId = getIntent().getStringExtra(SAPFirebaseMessagingService.NOTIFICATION_ID_EXTRA);
        final RemoteMessage notificationMessage = (RemoteMessage) (getIntent().getParcelableExtra(SAPFirebaseMessagingService.NOTIFICATION_DATA));
        if( notificationMessage != null) {
            NotificationUtilities.processRemoteMessage(notificationMessage);
        }

        if(isTaskRoot()) {
            // if the stack were empty, then open the LogonActivity, which is the entry point
            // for the whole application
            Intent startLogonActivity = new Intent(this, LogonActivity.class);
            startLogonActivity.putExtra(SAPFirebaseMessagingService.NOTIFICATION_ID_EXTRA, notificationId);
            startLogonActivity.putExtra(SAPFirebaseMessagingService.NOTIFICATION_DATA,notificationMessage);
            startActivity(startLogonActivity);
            finish();
        } else {
            String textMsg = getResources().getString(R.string.push_text);
            String notificationTitle = getResources().getString(R.string.push_message);
            JSONObject jsonPayload = null;
            if (notificationMessage != null) {
                jsonPayload = new JSONObject(notificationMessage.getData());
                try {
                    notificationTitle = jsonPayload.getString("title");
                } catch (JSONException e) {
                }
                try {
                    textMsg = jsonPayload.getString("alert");
                } catch (JSONException e) {
                }
            }
            PushNotificationDialog frag = PushNotificationDialog.newInstance(notificationId, notificationTitle, textMsg, (jsonPayload!=null) ? jsonPayload.toString() : "Empty Notification Data");
            frag.show(getSupportFragmentManager(), "dialogue");
        }
    }

    /**
     * This inner class provide support for a Fragment dialogue that can stay over current activity
     * and respond properly to the device rotation.
     */
    public static class PushNotificationDialog extends DialogFragment {

        /*
         * Create a new instance for the dialogue
         */
        public static PushNotificationDialog newInstance(String notificationId, String title, String pushMessage, String notificationPayload) {
            Bundle instanceBundle = new Bundle();
            PushNotificationDialog thisInstance = new PushNotificationDialog();
            if(notificationId!=null) {
                instanceBundle.putString("notificationId", notificationId);
            }
            instanceBundle.putString("title", title);
            instanceBundle.putString("pushMessage", pushMessage);
            instanceBundle.putString("notificationPayload", notificationPayload);
            thisInstance.setArguments(instanceBundle);
            thisInstance.setCancelable(false);
            return thisInstance;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                    .setIcon(R.drawable.graphic_airplane)
                    .setTitle(getArguments().getString("title"))
                    .setMessage(getArguments().getString("pushMessage"))
                    .setOnDismissListener(dialogInterface -> {})
                    .setOnCancelListener(dialogInterface -> { })
                    .setPositiveButton(R.string.ok,
                            (dialog, whichButton) -> {
                                String notificationId = getArguments().getString("notificationId");
                                if( notificationId != null ) {
                                    SAPWizardApplication sapWizardApplication = (SAPWizardApplication)getActivity().getApplication();
                                    PushNotificationUtilities pushUtils = new PushNotificationUtilities(getActivity(),  sapWizardApplication.getErrorHandler(), sapWizardApplication.getSettingsParameters());
                                    pushUtils.setPushMessageStatus(notificationId, PushNotificationUtilities.NotificationStatus.consumed);
                                }
                                getActivity().finish();
                            }
                    )
                    .setNegativeButton(R.string.cancel,
                            (dialog, whichButton) -> {
                                getActivity().finish();
                            }
                    )
                    .create();
        }

    }

    /**
     * Utility method for presenting the messages on an alert dialog.
     *
     * THIS CAN BE A PLACE TO CUSTOMIZE PUSH MESSAGE HANDLING.
     *
     * @param activity activity (Activity) for the alert dialog
     * @param notificationId push notification id, unique id of the push message
     */
    public static void presentPushMessage(Activity activity, String notificationId) {
        RemoteMessage notificationMessage = activity.getIntent().getParcelableExtra(SAPFirebaseMessagingService.NOTIFICATION_DATA);
        Intent intent = new Intent(activity.getApplicationContext(), PushNotificationActivity.class);
        intent.putExtra(SAPFirebaseMessagingService.NOTIFICATION_ID_EXTRA, notificationId);
        intent.putExtra(SAPFirebaseMessagingService.NOTIFICATION_DATA, notificationMessage);
        activity.startActivity(intent);
    }
}
