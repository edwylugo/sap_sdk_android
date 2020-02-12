package com.company.mysapcpsdkproject.logon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.company.mysapcpsdkproject.offline.OfflineODataSyncService;
import com.sap.cloud.mobile.odata.core.Action0;
import androidx.appcompat.app.AppCompatActivity;
import com.company.mysapcpsdkproject.service.SAPServiceManager;
import com.company.mysapcpsdkproject.R;
import com.company.mysapcpsdkproject.app.ConfigurationData;
import com.company.mysapcpsdkproject.app.ErrorHandler;
import com.company.mysapcpsdkproject.app.ErrorMessage;
import com.company.mysapcpsdkproject.app.SAPWizardApplication;
import com.company.mysapcpsdkproject.fcm.SAPFirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.company.mysapcpsdkproject.mdui.EntitySetListActivity;

import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler;
import com.sap.cloud.mobile.foundation.common.EncryptionError;
import com.sap.cloud.mobile.foundation.securestore.OpenFailureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sap.cloud.mobile.foundation.logging.Logging;
import ch.qos.logback.classic.Level;
import com.sap.cloud.mobile.onboarding.launchscreen.LaunchScreenSettings;
import com.sap.cloud.mobile.onboarding.passcode.EnterPasscodeActivity;
import com.sap.cloud.mobile.onboarding.passcode.EnterPasscodeSettings;
import com.sap.cloud.mobile.onboarding.passcode.PasscodePolicy;
import com.sap.cloud.mobile.onboarding.passcode.SetPasscodeSettings;
import com.sap.cloud.mobile.onboarding.passcode.SetPasscodeActivity;
import com.sap.cloud.mobile.onboarding.utility.OnboardingType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LogonActivity extends AppCompatActivity {

    public static final String IS_RESUMING_KEY = "isResuming";
    public static final String IS_AWAITING_RESULT_KEY = "isAwaitingResult";
    private static final int LAUNCH_SCREEN = 100;
    private static final int SET_PASSCODE = 200;
    private static final int ENTER_PASSCODE = 300;
    private static final int ENTITYSET_LIST = 400;
    private static final int ASK_USAGE_PERMISSION = 600;

    private static final String sharedPreferenceKey = "OfflineODataStoreInitialized";
    private static final Logger LOGGER = LoggerFactory.getLogger(LogonActivity.class);

    private boolean isResuming = false;
    private boolean isAwaitingResult = false;

    private SAPServiceManager sapServiceManager;

    private SecureStoreManager secureStoreManager;

    private ClientPolicyManager clientPolicyManager;

    private ErrorHandler errorHandler;

    private ConfigurationData configurationData;
    /*
     * Android Bound Service to handle offline synchronization operations. Service runs in foreground mode to maximize
     * resiliency.
     */
    private OfflineODataSyncService offlineSyncService;

    /*
     * Flag to indicate that current activity is bound to the Offline Sync Service
     */
    private boolean isBound;

    /*
     * Service connection object callbacks when service is bound or lost
     */
    private ServiceConnection serviceConnection;

    /*
     * Indicates that initial opening and downloading of offline data store
     */
    ProgressBar initialOpenProgressBar;

    /*
     * Advise user of initial open delay
     */
    TextView initialOpenStatus;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isAwaitingResult = false;

        switch (requestCode) {
            case LAUNCH_SCREEN:
                switch (resultCode) {
                    case RESULT_OK:
                        setPasscode();
                        break;
                    case CONTEXT_IGNORE_SECURITY:
                        finishLogonActivity();
                        break;
                    case RESULT_CANCELED:
                        finish();
                        break;
                    default:
                        startLaunchScreen();
                        break;
                }
                break;
            case SET_PASSCODE:
                switch (resultCode) {
                    case RESULT_OK:
                        startConsentScreen();
                        break;
                    case RESULT_CANCELED:
                        startLaunchScreen();
                        break;
                    case SetPasscodeActivity.POLICY_CANCELLED:
                        LOGGER.error("Resetting the app after the passcode policy couldn't be retrieved.");
                        ((SAPWizardApplication) getApplication()).resetApp(this);
                        break;
                }
                break;
            case ENTER_PASSCODE:
                switch (resultCode) {
                    case RESULT_OK:
                        finishLogonActivity();
                        break;
                    case RESULT_CANCELED:
                        // clicking back on EnterPasscode screen will quit the application
                        finishAffinity();
                        break;
            }
            break;
            case ENTITYSET_LIST:
                // TODO check if something should be done here?
                break;
            case ASK_USAGE_PERMISSION:
                if (secureStoreManager.isPasscodePolicyEnabled()) {
                    finishLogonActivity();
                } else {
                    startEntitySetListActivity();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_AWAITING_RESULT_KEY, isAwaitingResult);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isAwaitingResult = savedInstanceState.getBoolean(IS_AWAITING_RESULT_KEY);
        }

        sapServiceManager = ((SAPWizardApplication)getApplication()).getSAPServiceManager();
        secureStoreManager = ((SAPWizardApplication)getApplication()).getSecureStoreManager();
        clientPolicyManager = ((SAPWizardApplication)getApplication()).getClientPolicyManager();
        errorHandler = ((SAPWizardApplication)getApplication()).getErrorHandler();
        configurationData = ((SAPWizardApplication)getApplication()).getConfigurationData();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isResuming = bundle.getBoolean(IS_RESUMING_KEY, false);
        }
        FingerprintActionHandlerImpl.setDisableOnCancel(false);
        setContentView(R.layout.activity_logon);
        if (isAwaitingResult) {
            return;
        }
        // Initialize logging
        Logging.initialize(getApplicationContext(), new Logging.ConfigurationBuilder().initialLevel(Level.WARN).logToConsole(true).build());
        boolean isOnBoarded = ((SAPWizardApplication)getApplication()).isOnboarded();
        if (!isOnBoarded) {
            // create the store for application data (with default passcode)
            try {
                secureStoreManager.openApplicationStore();
                } catch (EncryptionError | OpenFailureException e) {
                    LOGGER.error("Unable to open initial application store with default passcode", e);
            }
            startLaunchScreen();
        } else {
            ((SAPWizardApplication)getApplication()).getUsageUtil().eventBehaviorViewDisplayed("LogonActivity","elementID","onCreate","called");
            // config data must be present
            if(!configurationData.isLoaded()) {
                // Log an error message and reset the Application
                String errorTitle = getResources().getString(R.string.config_data_error_title);
                String errorDetails = getResources().getString(R.string.config_data_corrupted_description);
                ErrorMessage errorMessage = new ErrorMessage(errorTitle, errorDetails, null, false);
                errorHandler.sendErrorMessage(errorMessage);
                ((SAPWizardApplication)getApplication()).resetApp(this);
            } else {
                boolean isUserPasscode = secureStoreManager.isUserPasscodeSet();
                if (isUserPasscode) {
                    // user passcode
                    if (secureStoreManager.isApplicationStoreOpen()) {
                        finishLogonActivity();
                    } else {
                        enterPasscode();
                    }
                } else {
                    // default passcode
                    openApplicationStore();
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.submit(() -> {
                        ClientPolicy clientPolicy = clientPolicyManager.getClientPolicy(true);
                        boolean isPolicyEnabled = clientPolicy.isPasscodePolicyEnabled();
                        boolean isLogPolicyEnabled = clientPolicy.isLogEnabled();
                        clientPolicyManager.initializeLoggingWithPolicy(isLogPolicyEnabled);
                        secureStoreManager.setIsPasscodePolicyEnabled(isPolicyEnabled);
                        boolean isDefaultEnabled = clientPolicyManager.getClientPolicy(false).getPasscodePolicy().isSkipEnabled();
                        if (isPolicyEnabled && !isDefaultEnabled) {
                            LogonActivity.this.runOnUiThread(() -> {
                                Activity activity = AppLifecycleCallbackHandler.getInstance().getActivity();
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
                                Resources res = LogonActivity.this.getResources();
                                alertBuilder.setTitle(res.getString(R.string.passcode_required));
                                alertBuilder.setMessage(res.getString(R.string.passcode_required_detail));
                                alertBuilder.setPositiveButton(res.getString(R.string.ok), null);
                                alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        Intent intent = new Intent(activity, SetPasscodeActivity.class);
                                        SetPasscodeSettings setPasscodeSettings = new SetPasscodeSettings();
                                        setPasscodeSettings.saveToIntent(intent);
                                        isAwaitingResult = true;
                                        LogonActivity.this.startActivityForResult(intent, SET_PASSCODE);
                                    }
                                });
                            });
                        }
                    });
                    executorService.shutdown();
                    finishLogonActivity();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            offlineSyncService = null;
        }
    }

    private void startLaunchScreen() {
        Intent welcome = new Intent(this, com.sap.cloud.mobile.onboarding.launchscreen.LaunchScreenActivity.class);
        LaunchScreenSettings launchScreenSettings = new LaunchScreenSettings();
        launchScreenSettings.setDemoAvailable(false);
        launchScreenSettings.setLaunchScreenHeadline(getString(R.string.welcome_screen_headline_label));
        launchScreenSettings.setWelcomeScreenType(OnboardingType.STANDARD_ONBOARDING);
        launchScreenSettings.setLaunchScreenTitles(new String[]{getString(R.string.application_name)});
        launchScreenSettings.setLaunchScreenImages(new int[]{R.drawable.ic_android_white});
        launchScreenSettings.setLaunchScreenDescriptions(new String[]{getString(R.string.welcome_screen_detail_label)});
        launchScreenSettings.setLaunchScreenPrimaryButton(getString(R.string.welcome_screen_primary_button_label));
        launchScreenSettings.saveToIntent(welcome);
        isAwaitingResult = true;
        startActivityForResult(welcome, LAUNCH_SCREEN);
    }

    private void setPasscode() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            ClientPolicy clientPolicy = clientPolicyManager.getClientPolicy(true);
            PasscodePolicy passcodePolicy = clientPolicy.getPasscodePolicy();
            // isPolicyEnabled defaults to true, because the error message informing the user the
            // passcode policy couldn't be retrieved is shown on the set passcode screen.
            boolean isPolicyEnabled = passcodePolicy != null ? clientPolicy.isPasscodePolicyEnabled() : true;
            boolean isLogPolicyEnabled = clientPolicy.isLogEnabled();
            clientPolicyManager.initializeLoggingWithPolicy(isLogPolicyEnabled);;
            secureStoreManager.setIsPasscodePolicyEnabled(isPolicyEnabled);

            if (isPolicyEnabled) {
                Intent i = new Intent(LogonActivity.this, SetPasscodeActivity.class);
                SetPasscodeSettings setPasscodeSettings = new SetPasscodeSettings();
                setPasscodeSettings.setSkipButtonText(getString(R.string.skip_passcode));
                setPasscodeSettings.saveToIntent(i);
                isAwaitingResult = true;
                startActivityForResult(i, SET_PASSCODE);
            } else {
                openApplicationStore();
                ((SAPWizardApplication)getApplication()).setIsOnboarded(true);
                startConsentScreen();
            }
        });
        executorService.shutdown();
    }
    private void startConsentScreen() {
        Intent i = new Intent(LogonActivity.this, AskUsagePermissionActivity.class);
        isAwaitingResult = true;
        startActivityForResult(i, ASK_USAGE_PERMISSION);
    }

    private void enterPasscode() {
        // if retry limit is reached, then EnterPasscode screen is opened in disabled mode, i.e. only
        // reset is possible
        int currentRetryCount = secureStoreManager.getWithPasscodePolicyStore(
                passcodePolicyStore -> passcodePolicyStore.getInt(ClientPolicyManager.KEY_RETRY_COUNT)
        );
        int retryLimit = clientPolicyManager.getClientPolicy(false).getPasscodePolicy().getRetryLimit();
        if (retryLimit <= currentRetryCount) {
            // only reset is allowed
            Intent enterPasscodeIntent = new Intent(this, EnterPasscodeActivity.class);
            EnterPasscodeSettings enterPasscodeSettings = new EnterPasscodeSettings();
            enterPasscodeSettings.setFinalDisabled(true);
            enterPasscodeSettings.saveToIntent(enterPasscodeIntent);
            isAwaitingResult = true;
            startActivityForResult(enterPasscodeIntent, ENTER_PASSCODE);
        } else {
            // client policy is refreshed now in UnlockActivity
            Intent unlockIntent = new Intent(this, UnlockActivity.class);
            isAwaitingResult = true;
            startActivityForResult(unlockIntent, ENTER_PASSCODE);
        }
    }

    private void finishLogonActivity() {
        if (isResuming) {
            LOGGER.debug("finishing LogonActivity since app is resuming.");
            finish();
        } else {
            LOGGER.debug("Starting entity set list activity since app is starting for first time.");
            startEntitySetListActivity();
        }
    }

    private void startEntitySetListActivity() {
        openODataStore(() -> {
            Intent intent = new Intent(LogonActivity.this, EntitySetListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // if it were started from a push message, then forward the extras to the next activity
            String notificationID = getIntent().getStringExtra(SAPFirebaseMessagingService.NOTIFICATION_ID_EXTRA);
            RemoteMessage remoteMessage = getIntent().getParcelableExtra(SAPFirebaseMessagingService.NOTIFICATION_DATA);
            if (notificationID != null) {
                intent.putExtra(SAPFirebaseMessagingService.NOTIFICATION_ID_EXTRA, notificationID);
                intent.putExtra(SAPFirebaseMessagingService.NOTIFICATION_DATA,remoteMessage);
            }
            startActivityForResult(intent, ENTITYSET_LIST);
        });
    }

    private void openApplicationStore() {
        try {
            secureStoreManager.openApplicationStore();
        } catch(EncryptionError | OpenFailureException e) {
            String errorTitle = getResources().getString(R.string.secure_store_error);
            String errorDetails = getResources().getString(R.string.secure_store_open_default_error_detail);
            ErrorMessage errorMessage = new ErrorMessage(errorTitle, errorDetails, e, false);
            errorHandler.sendErrorMessage(errorMessage);
        }
    }

    /**
     * Open offline data store by connection to Offline Sync Service and if successful proceeds with
     * actual open. When bind to service, callbacks will occur on functions in the service connection
     * object for connected and disconnected events.
     * @param openDoneHandler - handler to invoke when offline data store is opened successfully
     */
    private void openODataStore(Action0 openDoneHandler) {
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                offlineSyncService = ((OfflineODataSyncService.LocalBinder)service).getService();
                isBound = true;
                open(openDoneHandler);
            }

            public void onServiceDisconnected(ComponentName className) {
                offlineSyncService = null;
                isBound = false;
            }
        };

        if (bindService(new Intent(this, OfflineODataSyncService.class),
                serviceConnection, Context.BIND_AUTO_CREATE)) {
        } else {
            unbindService(serviceConnection);
            String errorTitle = getResources().getString(R.string.bind_error);
            String errorDetails = getResources().getString(R.string.bind_error_detail);
            ErrorMessage errorMessage = new ErrorMessage(errorTitle, errorDetails, null, false);
            errorHandler.sendErrorMessage(errorMessage);
            LOGGER.error("Bind service encountered failure.");
        }
    }

    /**
     * Performs actual open by calling Offline Sync Service.
     * Display progress and text to advise users of lengthy initial open and wait for completion
     * @param openDoneHandler - handle to call when offline data store is opened successfully
     */
    private void open(Action0 openDoneHandler) {
        boolean showProgress = checkAndSetupShowProgress();
        offlineSyncService.openStore(sapServiceManager.retrieveProvider(),
            () -> {
                if (showProgress) {
                    LogonActivity.this.runOnUiThread(() -> {
                        initialOpenStatus.setVisibility(View.INVISIBLE);
                        initialOpenProgressBar.setVisibility(View.INVISIBLE);
                    });
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(sharedPreferenceKey, true);
                    editor.commit();
                }
                openDoneHandler.call();},
            error -> {
                if (showProgress) {
                    LogonActivity.this.runOnUiThread(() -> {
                        initialOpenStatus.setVisibility(View.INVISIBLE);
                        initialOpenProgressBar.setVisibility(View.INVISIBLE);
                    });
                }
                String errorTitle = getResources().getString(R.string.offline_initial_open_error);
                String errorDetails = getResources().getString(R.string.offline_initial_open_error_detail);
                ErrorMessage errorMessage = new ErrorMessage(errorTitle, errorDetails, error, false);
                errorHandler.sendErrorMessage(errorMessage);
            });
    }

    /**
     * Check to see if this is an initial open by looking for a stored flag in shared preferences for this activity
     * @return true if progress and text display should be shown i.e. initial open
     */
    private boolean checkAndSetupShowProgress() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isOfflineStoreInitialized = sharedPreferences.getBoolean(sharedPreferenceKey, false);
        if (!isOfflineStoreInitialized) {
            initialOpenProgressBar = findViewById(R.id.progressbar);
            initialOpenProgressBar.setIndeterminate(true);
            initialOpenProgressBar.setVisibility(View.VISIBLE);

            initialOpenStatus = findViewById(R.id.initial_open_text);
            initialOpenStatus.setVisibility(View.VISIBLE);
            initialOpenStatus.setText(getResources().getString(R.string.initializing_offline_store));
            return true;
        }
        return false;
    }
}
