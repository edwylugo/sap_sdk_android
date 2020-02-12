package com.company.mysapcpsdkproject.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.company.mysapcpsdkproject.R;
import com.company.mysapcpsdkproject.app.ConfigurationData;
import com.company.mysapcpsdkproject.app.ErrorHandler;
import com.company.mysapcpsdkproject.app.ErrorMessage;
import com.company.mysapcpsdkproject.app.SAPWizardApplication;
import com.company.mysapcpsdkproject.offline.OfflineODataSyncService;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainer;
import com.sap.cloud.mobile.foundation.common.ClientProvider;
import com.sap.cloud.mobile.foundation.common.EncryptionUtil;
import com.sap.cloud.mobile.odata.core.Action0;
import com.sap.cloud.mobile.odata.core.Action1;
import com.sap.cloud.mobile.odata.core.AndroidSystem;
import com.sap.cloud.mobile.odata.offline.OfflineODataDefiningQuery;
import com.sap.cloud.mobile.odata.offline.OfflineODataException;
import com.sap.cloud.mobile.odata.offline.OfflineODataParameters;
import com.sap.cloud.mobile.odata.offline.OfflineODataProvider;
import java.net.URL;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the Mobile Application backed by an OData service for offline use
 */
public class SAPServiceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SAPServiceManager.class);

    /*
     * Name of the offline data file on the application file space
     */
    private static final String OFFLINE_DATASTORE = "OfflineDataStore";

    /*
     *
     */
    private static String OFFLINE_DATASTORE_ENCRYPTION_KEY_ALIAS = "Offline_DataStore_EncryptionKey_Alias";

    /*
     * Configuration data from Config Provider
     */
    private final ConfigurationData configurationData;

    /*
     * Offline line OData Provider
     */
    private OfflineODataProvider provider;

    /*
     * OData service for interacting with local OData Provider
     */

    private ipmContainer ipmContainer;

    /*
     * Service root
     */
    private String serviceRoot;

    /*
     * Application context for use by OfflineProvider
     */
    private Context context;

    /*
     * Error Handler to report initialization errors
     */
    private ErrorHandler errorHandler;

    /*
     * SAPWizardApplication
     */
    SAPWizardApplication application;

    /*
     * Connection ID of Mobile Application
     */
    public static final String CONNECTION_ID_IPMCONTAINER = "br.com.itsgroup.ipm.stihl";

    public SAPServiceManager(ConfigurationData configurationData, Context context,
            ErrorHandler errorHandler) {
        this.configurationData = configurationData;
        this.context = context;
        this.errorHandler = errorHandler;
        application = (SAPWizardApplication)context;
    }

    /**
     * Retrieve OData service proxy to interact with local offline OData provider
     * @return an instance of ipmContainer
     */
    public String getServiceRoot() {
        if (serviceRoot == null) {
            serviceRoot = configurationData.getServiceUrl() +  CONNECTION_ID_IPMCONTAINER + "/";
        }
        return serviceRoot;
    }

    public ipmContainer getipmContainer() {
        if (ipmContainer == null) {
            throw new IllegalStateException("SAPServiceManager was not initialized");
        }
        return ipmContainer;
    }

    /**
     * This call can only be made when the user is authenticated (if required) as it depends
     * on application store for encryption keys and ClientProvider
     * @return OfflineODataProvider
     */
    public OfflineODataProvider retrieveProvider() {
        if (provider == null) {
            initializeOffline(false);
        }
        return provider;
    }

    /*
     * Create OfflineODataProvider
     * This is a blocking call, no data will be transferred until open, download, upload
     * @param forReset true when initializing the offline provider for reset purpose. This is because reset can occur
     *                 when we have not been onboarded and have no configuration data
     */
    private void initializeOffline(boolean forReset) {
        AndroidSystem.setContext(context);
        String serviceUrl = configurationData.getServiceUrl();
        if (serviceUrl == null) {
            if (forReset) {
                serviceUrl = "http://localhost/";
            } else {
                LOGGER.error("ServerURL is null when attempting to create offline provider");
            }
        }
        try {
            URL url = new URL(serviceUrl  + CONNECTION_ID_IPMCONTAINER);
            OfflineODataParameters offlineODataParameters = new OfflineODataParameters();
            offlineODataParameters.setEnableRepeatableRequests(true);
            offlineODataParameters.setStoreName(OFFLINE_DATASTORE);
            byte[] encryptionKeyBytes = EncryptionUtil.getEncryptionKey(OFFLINE_DATASTORE_ENCRYPTION_KEY_ALIAS);
            String key = Base64.encodeToString(encryptionKeyBytes, Base64.NO_WRAP);
            offlineODataParameters.setStoreEncryptionKey(key);
            Arrays.fill(encryptionKeyBytes, (byte)0 );

            provider = new OfflineODataProvider(url, offlineODataParameters, ClientProvider.get(), null, null);

            OfflineODataDefiningQuery actionEventssQuery = new OfflineODataDefiningQuery("ActionEventss", "ActionEventss", false);
            provider.addDefiningQuery(actionEventssQuery);
            OfflineODataDefiningQuery catalogTypeBatchssQuery = new OfflineODataDefiningQuery("CatalogTypeBatchss", "CatalogTypeBatchss", false);
            provider.addDefiningQuery(catalogTypeBatchssQuery);
            OfflineODataDefiningQuery catalogTypesQuery = new OfflineODataDefiningQuery("CatalogTypes", "CatalogTypes", false);
            provider.addDefiningQuery(catalogTypesQuery);
            OfflineODataDefiningQuery causeOfConfirmationsQuery = new OfflineODataDefiningQuery("CauseOfConfirmations", "CauseOfConfirmations", false);
            provider.addDefiningQuery(causeOfConfirmationsQuery);
            OfflineODataDefiningQuery combinationOrderActivitysQuery = new OfflineODataDefiningQuery("CombinationOrderActivitys", "CombinationOrderActivitys", false);
            provider.addDefiningQuery(combinationOrderActivitysQuery);
            OfflineODataDefiningQuery configDataBatchsQuery = new OfflineODataDefiningQuery("ConfigDataBatchs", "ConfigDataBatchs", false);
            provider.addDefiningQuery(configDataBatchsQuery);
            OfflineODataDefiningQuery confirmationTextsQuery = new OfflineODataDefiningQuery("ConfirmationTexts", "ConfirmationTexts", false);
            provider.addDefiningQuery(confirmationTextsQuery);
            OfflineODataDefiningQuery confirmationsQuery = new OfflineODataDefiningQuery("Confirmations", "Confirmations", false);
            provider.addDefiningQuery(confirmationsQuery);
            OfflineODataDefiningQuery deletedEntitysQuery = new OfflineODataDefiningQuery("DeletedEntitys", "DeletedEntitys", false);
            provider.addDefiningQuery(deletedEntitysQuery);
            OfflineODataDefiningQuery employeesQuery = new OfflineODataDefiningQuery("Employees", "Employees", false);
            provider.addDefiningQuery(employeesQuery);
            OfflineODataDefiningQuery enrollOrderTypesQuery = new OfflineODataDefiningQuery("EnrollOrderTypes", "EnrollOrderTypes", false);
            provider.addDefiningQuery(enrollOrderTypesQuery);
            OfflineODataDefiningQuery equipmentBatchssQuery = new OfflineODataDefiningQuery("EquipmentBatchss", "EquipmentBatchss", false);
            provider.addDefiningQuery(equipmentBatchssQuery);
            OfflineODataDefiningQuery equipmentsQuery = new OfflineODataDefiningQuery("Equipments", "Equipments", false);
            provider.addDefiningQuery(equipmentsQuery);
            OfflineODataDefiningQuery inspectionCatalogCodeGroupsQuery = new OfflineODataDefiningQuery("InspectionCatalogCodeGroups", "InspectionCatalogCodeGroups", false);
            provider.addDefiningQuery(inspectionCatalogCodeGroupsQuery);
            OfflineODataDefiningQuery inspectionCatalogCodesQuery = new OfflineODataDefiningQuery("InspectionCatalogCodes", "InspectionCatalogCodes", false);
            provider.addDefiningQuery(inspectionCatalogCodesQuery);
            OfflineODataDefiningQuery inspectionCatalogsQuery = new OfflineODataDefiningQuery("InspectionCatalogs", "InspectionCatalogs", false);
            provider.addDefiningQuery(inspectionCatalogsQuery);
            OfflineODataDefiningQuery listTechnicalEquipmentBatchsQuery = new OfflineODataDefiningQuery("ListTechnicalEquipmentBatchs", "ListTechnicalEquipmentBatchs", false);
            provider.addDefiningQuery(listTechnicalEquipmentBatchsQuery);
            OfflineODataDefiningQuery listTechnicalEquipmentsQuery = new OfflineODataDefiningQuery("ListTechnicalEquipments", "ListTechnicalEquipments", false);
            provider.addDefiningQuery(listTechnicalEquipmentsQuery);
            OfflineODataDefiningQuery localInstallStructuresQuery = new OfflineODataDefiningQuery("LocalInstallStructures", "LocalInstallStructures", false);
            provider.addDefiningQuery(localInstallStructuresQuery);
            OfflineODataDefiningQuery logSapsQuery = new OfflineODataDefiningQuery("LogSaps", "LogSaps", false);
            provider.addDefiningQuery(logSapsQuery);
            OfflineODataDefiningQuery longTextNotificationBatchsQuery = new OfflineODataDefiningQuery("LongTextNotificationBatchs", "LongTextNotificationBatchs", false);
            provider.addDefiningQuery(longTextNotificationBatchsQuery);
            OfflineODataDefiningQuery longTextNotificationsQuery = new OfflineODataDefiningQuery("LongTextNotifications", "LongTextNotifications", false);
            provider.addDefiningQuery(longTextNotificationsQuery);
            OfflineODataDefiningQuery longTextOrderBatchsQuery = new OfflineODataDefiningQuery("LongTextOrderBatchs", "LongTextOrderBatchs", false);
            provider.addDefiningQuery(longTextOrderBatchsQuery);
            OfflineODataDefiningQuery longTextOrdersQuery = new OfflineODataDefiningQuery("LongTextOrders", "LongTextOrders", false);
            provider.addDefiningQuery(longTextOrdersQuery);
            OfflineODataDefiningQuery maintenanceActivityTypesQuery = new OfflineODataDefiningQuery("MaintenanceActivityTypes", "MaintenanceActivityTypes", false);
            provider.addDefiningQuery(maintenanceActivityTypesQuery);
            OfflineODataDefiningQuery maintenanceControlParametersQuery = new OfflineODataDefiningQuery("MaintenanceControlParameters", "MaintenanceControlParameters", false);
            provider.addDefiningQuery(maintenanceControlParametersQuery);
            OfflineODataDefiningQuery maintenanceNotificationBatchsQuery = new OfflineODataDefiningQuery("MaintenanceNotificationBatchs", "MaintenanceNotificationBatchs", false);
            provider.addDefiningQuery(maintenanceNotificationBatchsQuery);
            OfflineODataDefiningQuery maintenanceNotificationCausesQuery = new OfflineODataDefiningQuery("MaintenanceNotificationCauses", "MaintenanceNotificationCauses", false);
            provider.addDefiningQuery(maintenanceNotificationCausesQuery);
            OfflineODataDefiningQuery maintenanceNotificationItemsQuery = new OfflineODataDefiningQuery("MaintenanceNotificationItems", "MaintenanceNotificationItems", false);
            provider.addDefiningQuery(maintenanceNotificationItemsQuery);
            OfflineODataDefiningQuery maintenanceNotificationsQuery = new OfflineODataDefiningQuery("MaintenanceNotifications", "MaintenanceNotifications", false);
            provider.addDefiningQuery(maintenanceNotificationsQuery);
            OfflineODataDefiningQuery maintenanceOrderBatchsQuery = new OfflineODataDefiningQuery("MaintenanceOrderBatchs", "MaintenanceOrderBatchs", false);
            provider.addDefiningQuery(maintenanceOrderBatchsQuery);
            OfflineODataDefiningQuery maintenanceOrderComponentsQuery = new OfflineODataDefiningQuery("MaintenanceOrderComponents", "MaintenanceOrderComponents", false);
            provider.addDefiningQuery(maintenanceOrderComponentsQuery);
            OfflineODataDefiningQuery maintenanceOrderOperationsQuery = new OfflineODataDefiningQuery("MaintenanceOrderOperations", "MaintenanceOrderOperations", false);
            provider.addDefiningQuery(maintenanceOrderOperationsQuery);
            OfflineODataDefiningQuery maintenanceOrderPriorityTextAssociationsQuery = new OfflineODataDefiningQuery("MaintenanceOrderPriorityTextAssociations", "MaintenanceOrderPriorityTextAssociations", false);
            provider.addDefiningQuery(maintenanceOrderPriorityTextAssociationsQuery);
            OfflineODataDefiningQuery maintenanceOrdersQuery = new OfflineODataDefiningQuery("MaintenanceOrders", "MaintenanceOrders", false);
            provider.addDefiningQuery(maintenanceOrdersQuery);
            OfflineODataDefiningQuery masterDataBatchsQuery = new OfflineODataDefiningQuery("MasterDataBatchs", "MasterDataBatchs", false);
            provider.addDefiningQuery(masterDataBatchsQuery);
            OfflineODataDefiningQuery materialDescriptionsQuery = new OfflineODataDefiningQuery("MaterialDescriptions", "MaterialDescriptions", false);
            provider.addDefiningQuery(materialDescriptionsQuery);
            OfflineODataDefiningQuery notificationTypeTextsQuery = new OfflineODataDefiningQuery("NotificationTypeTexts", "NotificationTypeTexts", false);
            provider.addDefiningQuery(notificationTypeTextsQuery);
            OfflineODataDefiningQuery operationMaterialsQuery = new OfflineODataDefiningQuery("OperationMaterials", "OperationMaterials", false);
            provider.addDefiningQuery(operationMaterialsQuery);
            OfflineODataDefiningQuery orderRuleTypeDataBatchsQuery = new OfflineODataDefiningQuery("OrderRuleTypeDataBatchs", "OrderRuleTypeDataBatchs", false);
            provider.addDefiningQuery(orderRuleTypeDataBatchsQuery);
            OfflineODataDefiningQuery orderRuleTypesQuery = new OfflineODataDefiningQuery("OrderRuleTypes", "OrderRuleTypes", false);
            provider.addDefiningQuery(orderRuleTypesQuery);
            OfflineODataDefiningQuery orderTypeTextsQuery = new OfflineODataDefiningQuery("OrderTypeTexts", "OrderTypeTexts", false);
            provider.addDefiningQuery(orderTypeTextsQuery);
            OfflineODataDefiningQuery parameterssQuery = new OfflineODataDefiningQuery("Parameterss", "Parameterss", false);
            provider.addDefiningQuery(parameterssQuery);
            OfflineODataDefiningQuery priorityTextsQuery = new OfflineODataDefiningQuery("PriorityTexts", "PriorityTexts", false);
            provider.addDefiningQuery(priorityTextsQuery);
            OfflineODataDefiningQuery usersQuery = new OfflineODataDefiningQuery("Users", "Users", false);
            provider.addDefiningQuery(usersQuery);
            OfflineODataDefiningQuery workCenterDescriptionsQuery = new OfflineODataDefiningQuery("WorkCenterDescriptions", "WorkCenterDescriptions", false);
            provider.addDefiningQuery(workCenterDescriptionsQuery);
            OfflineODataDefiningQuery workCentersQuery = new OfflineODataDefiningQuery("WorkCenters", "WorkCenters", false);
            provider.addDefiningQuery(workCentersQuery);

            ipmContainer = new ipmContainer(provider);
        } catch (Exception e) {
            LOGGER.error("Exception encountered setting up offline store: " + e.getMessage());
            Resources res = context.getResources();
            ErrorMessage errorMessage = new ErrorMessage(res.getString(R.string.offline_provider_error),
                    res.getString(R.string.offline_provider_error_detail));
            errorHandler.sendErrorMessage(errorMessage);
        }
    }

    /**
     * Synchronize local offline data store with Server
     * Upload - local changes
     * Download - server changes
     * @param syncService
     * @param syncSuccessHandler
     * @param syncFailureHandler
     */
    public void synchronize(OfflineODataSyncService syncService,
        Action0 syncSuccessHandler, Action1<OfflineODataException> syncFailureHandler) {
        syncService.uploadStore(provider,
            () -> {
                syncService.downloadStore(provider,
                    () -> {
                        application.getRepositoryFactory().reset();
                        syncSuccessHandler.call();
                    },
                    error -> {
                        application.getRepositoryFactory().reset();
                        LOGGER.error("Exception encountered uploading from local store: " + error.getMessage());
                        syncFailureHandler.call(error);
                    });
            },
            error -> {
                application.getRepositoryFactory().reset();
                LOGGER.error("Exception encountered downloading to local store: " + error.getMessage());
                syncFailureHandler.call(error);
            });
    }

    /*
     * Close and remove offline data store
     */
    public void reset() {
        try {
            if (provider == null) {
                initializeOffline(true);
            }
            provider.clear();
            provider = null;
        } catch (OfflineODataException e) {
            LOGGER.error("Unable to reset Offline Data Store. Encountered exception: " + e.getMessage());
            Resources res = context.getResources();
            ErrorMessage errorMessage = new ErrorMessage(res.getString(R.string.offline_reset_store_error),
                res.getString(R.string.offline_reset_store_error_detail));
            errorHandler.sendErrorMessage(errorMessage);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().clear().commit();
    }

}
