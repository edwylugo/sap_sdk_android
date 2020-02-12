package com.company.mysapcpsdkproject.offline;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.company.mysapcpsdkproject.R;
import com.company.mysapcpsdkproject.mdui.EntitySetListActivity;
import com.sap.cloud.mobile.odata.core.Action0;
import com.sap.cloud.mobile.odata.core.Action1;
import com.sap.cloud.mobile.odata.offline.OfflineODataException;
import com.sap.cloud.mobile.odata.offline.OfflineODataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Android service providing offline OData synchronization: open, upload and downlaod
 * Operation is executed in foreground mode to maximize resiliency
 * Note that there is only one instance of this service and offline operations can only be
 * executed one at a time.
 *
 * OfflineODataProvider sync operations are non blocking and a new thread will be started.
 */
public class OfflineODataSyncService extends Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineODataSyncService.class);

    private final Object lock = new Object();
    private boolean isExecuting = false;
    private Action0 callerSuccessHandler;
    private Action1<OfflineODataException> callerFailureHandler;

    private static final int NOTIFICATION_ID = 1;
    private static final String OFFLINE_SYNC_CHANNEL_ID = "Offline OData Channel";

    /*
     * This is the object that receives interactions from clients.
     */
    private final IBinder binder = new LocalBinder();

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends android.os.Binder {
        public OfflineODataSyncService getService() {
            return OfflineODataSyncService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        LOGGER.debug("onStartCommand action:" + action);
        return START_NOT_STICKY;
    }

    /**
     * Internal completion handlers
     * Route to handlers provided by caller when some housekeeping is done
     */
    Action0 internalSuccessHandler = () -> {
        synchronized(lock) {
            LOGGER.debug("Offline sync operation success");
            isExecuting = false;
            stopForeground(true);
        }
        callSuccessHandler();
    };

    Action1<OfflineODataException> internalFailureHandler = (exception) -> {
        synchronized(lock) {
            LOGGER.debug("Offline sync operation failed. Exception:" + exception.getMessage());
            isExecuting = false;
            stopForeground(true);
        }
        callFailureHandler(exception);
    };

    /**
     * Test and set the execution in progress flag
     * In addition, it will start foreground mode
     * This makes sure that we have only one outstanding offline sync operation at a given time
     * @return true if there is active operation and false otherwise
     */
    private boolean testAndSetExecutionStatus() {
        synchronized (lock) {
            if (isExecuting) {
                return false;
            } else {
                isExecuting = true;
                LOGGER.debug("Offline sync starting in foreground mode");
                startForeground(NOTIFICATION_ID, createNotification());
                return true;
            }
        }
    }

    /**
     * Open offline data store. Initial open will incur download of data specified in the defining requests
     * @param provider
     * @param successHandler
     * @param failureHandler
     */
    public void openStore(@NonNull OfflineODataProvider provider,
            @Nullable final Action0 successHandler,
            @Nullable final Action1<OfflineODataException> failureHandler) {
        if (testAndSetExecutionStatus()) {
            callerSuccessHandler = successHandler;
            callerFailureHandler = failureHandler;
            LOGGER.debug("Opening offline store");
            provider.open(internalSuccessHandler, internalFailureHandler);
            return;
        }
        failureHandler.call(new OfflineODataException(0, "An offline sync operation is already in progress"));
    }

    /**
     * Download any new changes since last time this is invoked
     * @param provider
     * @param successHandler
     * @param failureHandler
     */
    public void downloadStore(@NonNull OfflineODataProvider provider,
            @Nullable final Action0 successHandler,
            @Nullable final Action1<OfflineODataException> failureHandler) {
        if (testAndSetExecutionStatus()) {
            callerSuccessHandler = successHandler;
            callerFailureHandler = failureHandler;
            LOGGER.debug("Downloading offline store");
            provider.download(internalSuccessHandler, internalFailureHandler);
            return;
        }
        failureHandler.call(new OfflineODataException(0, "An offline sync operation is already in progress"));
    }

    /**
     * Upload local changes to OData service
     * @param provider
     * @param successHandler
     * @param failureHandler
     */
    public void uploadStore(@NonNull OfflineODataProvider provider,
            @Nullable final Action0 successHandler,
            @Nullable final Action1<OfflineODataException> failureHandler) {
        if (testAndSetExecutionStatus()) {
            callerSuccessHandler = successHandler;
            callerFailureHandler = failureHandler;
            LOGGER.debug("Uploading offline store");
            provider.upload(internalSuccessHandler, internalFailureHandler);
            return;
        }
        failureHandler.call(new OfflineODataException(0, "An offline sync operation is already in progress"));
    }

    private void callSuccessHandler() {
        if (callerSuccessHandler != null) {
            callerSuccessHandler.call();
        }
    }

    private void callFailureHandler(@NonNull OfflineODataException exception) {
        if (callerFailureHandler != null) {
            callerFailureHandler.call(exception);
        }
    }

    /**
     * Creates persistent notification to put service into foreground mode
     * @return notification
     */
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, OFFLINE_SYNC_CHANNEL_ID);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Syncing Data.");
        builder.setStyle(bigTextStyle);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.ic_sync);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(OFFLINE_SYNC_CHANNEL_ID);
        }
        builder.setProgress(100, 0, true);

        // Clicking the notification will return to the app
        Intent intent = new Intent(this, EntitySetListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setFullScreenIntent(pendingIntent, false);
        return builder.build();
    }

    /**
     * To send notification, Oreo and later versions (API 26+) require a notification channel
     */
    private void createNotificationChannel() {
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Offline OData Sync";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(OFFLINE_SYNC_CHANNEL_ID, name, importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
