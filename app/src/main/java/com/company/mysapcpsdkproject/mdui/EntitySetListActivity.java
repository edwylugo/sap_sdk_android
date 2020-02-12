package com.company.mysapcpsdkproject.mdui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.IBinder;
import com.company.mysapcpsdkproject.app.ErrorHandler;
import com.company.mysapcpsdkproject.app.ErrorMessage;
import com.company.mysapcpsdkproject.app.SAPWizardApplication;
import com.company.mysapcpsdkproject.offline.OfflineODataSyncService;
import com.company.mysapcpsdkproject.service.SAPServiceManager;
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar;
import com.sap.cloud.mobile.odata.core.Action0;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.company.mysapcpsdkproject.app.SAPWizardApplication;
import com.company.mysapcpsdkproject.app.UsageUtil;
import com.company.mysapcpsdkproject.fcm.PushNotificationActivity;
import com.company.mysapcpsdkproject.fcm.SAPFirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.company.mysapcpsdkproject.mdui.actioneventss.ActionEventssActivity;
import com.company.mysapcpsdkproject.mdui.catalogtypebatchss.CatalogTypeBatchssActivity;
import com.company.mysapcpsdkproject.mdui.catalogtypes.CatalogTypesActivity;
import com.company.mysapcpsdkproject.mdui.causeofconfirmations.CauseOfConfirmationsActivity;
import com.company.mysapcpsdkproject.mdui.combinationorderactivitys.CombinationOrderActivitysActivity;
import com.company.mysapcpsdkproject.mdui.configdatabatchs.ConfigDataBatchsActivity;
import com.company.mysapcpsdkproject.mdui.confirmationtexts.ConfirmationTextsActivity;
import com.company.mysapcpsdkproject.mdui.confirmations.ConfirmationsActivity;
import com.company.mysapcpsdkproject.mdui.deletedentitys.DeletedEntitysActivity;
import com.company.mysapcpsdkproject.mdui.employees.EmployeesActivity;
import com.company.mysapcpsdkproject.mdui.enrollordertypes.EnrollOrderTypesActivity;
import com.company.mysapcpsdkproject.mdui.equipmentbatchss.EquipmentBatchssActivity;
import com.company.mysapcpsdkproject.mdui.equipments.EquipmentsActivity;
import com.company.mysapcpsdkproject.mdui.inspectioncatalogcodegroups.InspectionCatalogCodeGroupsActivity;
import com.company.mysapcpsdkproject.mdui.inspectioncatalogcodes.InspectionCatalogCodesActivity;
import com.company.mysapcpsdkproject.mdui.inspectioncatalogs.InspectionCatalogsActivity;
import com.company.mysapcpsdkproject.mdui.listtechnicalequipmentbatchs.ListTechnicalEquipmentBatchsActivity;
import com.company.mysapcpsdkproject.mdui.listtechnicalequipments.ListTechnicalEquipmentsActivity;
import com.company.mysapcpsdkproject.mdui.localinstallstructures.LocalInstallStructuresActivity;
import com.company.mysapcpsdkproject.mdui.logsaps.LogSapsActivity;
import com.company.mysapcpsdkproject.mdui.longtextnotificationbatchs.LongTextNotificationBatchsActivity;
import com.company.mysapcpsdkproject.mdui.longtextnotifications.LongTextNotificationsActivity;
import com.company.mysapcpsdkproject.mdui.longtextorderbatchs.LongTextOrderBatchsActivity;
import com.company.mysapcpsdkproject.mdui.longtextorders.LongTextOrdersActivity;
import com.company.mysapcpsdkproject.mdui.maintenanceactivitytypes.MaintenanceActivityTypesActivity;
import com.company.mysapcpsdkproject.mdui.maintenancecontrolparameters.MaintenanceControlParametersActivity;
import com.company.mysapcpsdkproject.mdui.maintenancenotificationbatchs.MaintenanceNotificationBatchsActivity;
import com.company.mysapcpsdkproject.mdui.maintenancenotificationcauses.MaintenanceNotificationCausesActivity;
import com.company.mysapcpsdkproject.mdui.maintenancenotificationitems.MaintenanceNotificationItemsActivity;
import com.company.mysapcpsdkproject.mdui.maintenancenotifications.MaintenanceNotificationsActivity;
import com.company.mysapcpsdkproject.mdui.maintenanceorderbatchs.MaintenanceOrderBatchsActivity;
import com.company.mysapcpsdkproject.mdui.maintenanceordercomponents.MaintenanceOrderComponentsActivity;
import com.company.mysapcpsdkproject.mdui.maintenanceorderoperations.MaintenanceOrderOperationsActivity;
import com.company.mysapcpsdkproject.mdui.maintenanceorderprioritytextassociations.MaintenanceOrderPriorityTextAssociationsActivity;
import com.company.mysapcpsdkproject.mdui.maintenanceorders.MaintenanceOrdersActivity;
import com.company.mysapcpsdkproject.mdui.masterdatabatchs.MasterDataBatchsActivity;
import com.company.mysapcpsdkproject.mdui.materialdescriptions.MaterialDescriptionsActivity;
import com.company.mysapcpsdkproject.mdui.notificationtypetexts.NotificationTypeTextsActivity;
import com.company.mysapcpsdkproject.mdui.operationmaterials.OperationMaterialsActivity;
import com.company.mysapcpsdkproject.mdui.orderruletypedatabatchs.OrderRuleTypeDataBatchsActivity;
import com.company.mysapcpsdkproject.mdui.orderruletypes.OrderRuleTypesActivity;
import com.company.mysapcpsdkproject.mdui.ordertypetexts.OrderTypeTextsActivity;
import com.company.mysapcpsdkproject.mdui.parameterss.ParameterssActivity;
import com.company.mysapcpsdkproject.mdui.prioritytexts.PriorityTextsActivity;
import com.company.mysapcpsdkproject.mdui.users.UsersActivity;
import com.company.mysapcpsdkproject.mdui.workcenterdescriptions.WorkCenterDescriptionsActivity;
import com.company.mysapcpsdkproject.mdui.workcenters.WorkCentersActivity;
import com.sap.cloud.mobile.fiori.object.ObjectCell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.company.mysapcpsdkproject.R;


/*
 * An activity to display the list of all entity types from the OData service
 */
public class EntitySetListActivity extends AppCompatActivity {

    private static final int SETTINGS_SCREEN_ITEM = 200;
    private static final int SYNC_ACTION_ITEM = 300;
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitySetListActivity.class);
    private static final int BLUE_ANDROID_ICON = R.drawable.ic_android_blue;
    private static final int WHITE_ANDROID_ICON = R.drawable.ic_android_white;

    public enum EntitySetName {
        ActionEventss("ActionEventss", R.string.eset_actioneventss,BLUE_ANDROID_ICON),
        CatalogTypeBatchss("CatalogTypeBatchss", R.string.eset_catalogtypebatchss,WHITE_ANDROID_ICON),
        CatalogTypes("CatalogTypes", R.string.eset_catalogtypes,BLUE_ANDROID_ICON),
        CauseOfConfirmations("CauseOfConfirmations", R.string.eset_causeofconfirmations,WHITE_ANDROID_ICON),
        CombinationOrderActivitys("CombinationOrderActivitys", R.string.eset_combinationorderactivitys,BLUE_ANDROID_ICON),
        ConfigDataBatchs("ConfigDataBatchs", R.string.eset_configdatabatchs,WHITE_ANDROID_ICON),
        ConfirmationTexts("ConfirmationTexts", R.string.eset_confirmationtexts,BLUE_ANDROID_ICON),
        Confirmations("Confirmations", R.string.eset_confirmations,WHITE_ANDROID_ICON),
        DeletedEntitys("DeletedEntitys", R.string.eset_deletedentitys,BLUE_ANDROID_ICON),
        Employees("Employees", R.string.eset_employees,WHITE_ANDROID_ICON),
        EnrollOrderTypes("EnrollOrderTypes", R.string.eset_enrollordertypes,BLUE_ANDROID_ICON),
        EquipmentBatchss("EquipmentBatchss", R.string.eset_equipmentbatchss,WHITE_ANDROID_ICON),
        Equipments("Equipments", R.string.eset_equipments,BLUE_ANDROID_ICON),
        InspectionCatalogCodeGroups("InspectionCatalogCodeGroups", R.string.eset_inspectioncatalogcodegroups,WHITE_ANDROID_ICON),
        InspectionCatalogCodes("InspectionCatalogCodes", R.string.eset_inspectioncatalogcodes,BLUE_ANDROID_ICON),
        InspectionCatalogs("InspectionCatalogs", R.string.eset_inspectioncatalogs,WHITE_ANDROID_ICON),
        ListTechnicalEquipmentBatchs("ListTechnicalEquipmentBatchs", R.string.eset_listtechnicalequipmentbatchs,BLUE_ANDROID_ICON),
        ListTechnicalEquipments("ListTechnicalEquipments", R.string.eset_listtechnicalequipments,WHITE_ANDROID_ICON),
        LocalInstallStructures("LocalInstallStructures", R.string.eset_localinstallstructures,BLUE_ANDROID_ICON),
        LogSaps("LogSaps", R.string.eset_logsaps,WHITE_ANDROID_ICON),
        LongTextNotificationBatchs("LongTextNotificationBatchs", R.string.eset_longtextnotificationbatchs,BLUE_ANDROID_ICON),
        LongTextNotifications("LongTextNotifications", R.string.eset_longtextnotifications,WHITE_ANDROID_ICON),
        LongTextOrderBatchs("LongTextOrderBatchs", R.string.eset_longtextorderbatchs,BLUE_ANDROID_ICON),
        LongTextOrders("LongTextOrders", R.string.eset_longtextorders,WHITE_ANDROID_ICON),
        MaintenanceActivityTypes("MaintenanceActivityTypes", R.string.eset_maintenanceactivitytypes,BLUE_ANDROID_ICON),
        MaintenanceControlParameters("MaintenanceControlParameters", R.string.eset_maintenancecontrolparameters,WHITE_ANDROID_ICON),
        MaintenanceNotificationBatchs("MaintenanceNotificationBatchs", R.string.eset_maintenancenotificationbatchs,BLUE_ANDROID_ICON),
        MaintenanceNotificationCauses("MaintenanceNotificationCauses", R.string.eset_maintenancenotificationcauses,WHITE_ANDROID_ICON),
        MaintenanceNotificationItems("MaintenanceNotificationItems", R.string.eset_maintenancenotificationitems,BLUE_ANDROID_ICON),
        MaintenanceNotifications("MaintenanceNotifications", R.string.eset_maintenancenotifications,WHITE_ANDROID_ICON),
        MaintenanceOrderBatchs("MaintenanceOrderBatchs", R.string.eset_maintenanceorderbatchs,BLUE_ANDROID_ICON),
        MaintenanceOrderComponents("MaintenanceOrderComponents", R.string.eset_maintenanceordercomponents,WHITE_ANDROID_ICON),
        MaintenanceOrderOperations("MaintenanceOrderOperations", R.string.eset_maintenanceorderoperations,BLUE_ANDROID_ICON),
        MaintenanceOrderPriorityTextAssociations("MaintenanceOrderPriorityTextAssociations", R.string.eset_maintenanceorderprioritytextassociations,WHITE_ANDROID_ICON),
        MaintenanceOrders("MaintenanceOrders", R.string.eset_maintenanceorders,BLUE_ANDROID_ICON),
        MasterDataBatchs("MasterDataBatchs", R.string.eset_masterdatabatchs,WHITE_ANDROID_ICON),
        MaterialDescriptions("MaterialDescriptions", R.string.eset_materialdescriptions,BLUE_ANDROID_ICON),
        NotificationTypeTexts("NotificationTypeTexts", R.string.eset_notificationtypetexts,WHITE_ANDROID_ICON),
        OperationMaterials("OperationMaterials", R.string.eset_operationmaterials,BLUE_ANDROID_ICON),
        OrderRuleTypeDataBatchs("OrderRuleTypeDataBatchs", R.string.eset_orderruletypedatabatchs,WHITE_ANDROID_ICON),
        OrderRuleTypes("OrderRuleTypes", R.string.eset_orderruletypes,BLUE_ANDROID_ICON),
        OrderTypeTexts("OrderTypeTexts", R.string.eset_ordertypetexts,WHITE_ANDROID_ICON),
        Parameterss("Parameterss", R.string.eset_parameterss,BLUE_ANDROID_ICON),
        PriorityTexts("PriorityTexts", R.string.eset_prioritytexts,WHITE_ANDROID_ICON),
        Users("Users", R.string.eset_users,BLUE_ANDROID_ICON),
        WorkCenterDescriptions("WorkCenterDescriptions", R.string.eset_workcenterdescriptions,WHITE_ANDROID_ICON),
        WorkCenters("WorkCenters", R.string.eset_workcenters,BLUE_ANDROID_ICON);

        private int titleId;
        private int iconId;
        private String entitySetName;

        EntitySetName(String name, int titleId, int iconId) {
            this.entitySetName = name;
            this.titleId = titleId;
            this.iconId = iconId;
        }

        public int getTitleId() {
                return this.titleId;
        }

        public String getEntitySetName() {
                return this.entitySetName;
        }
    }

    private final List<String> entitySetNames = new ArrayList<>();
    private final Map<String, EntitySetName> entitySetNameMap = new HashMap<>();
    private UsageUtil usageUtil;

    /*
     * Application Error handler for reporting errors
     */
    ErrorHandler errorHandler;

    /*
     * Android Bound Service to handle offline synchronization operations. Service runs in foreground mode to maximize
     * resiliency.
     */
    private OfflineODataSyncService syncService;

    /*
     * Flag to indicate that current acvtity is bound to the Offline Sync Service
     */
    boolean isBound = false;

    /*
     * Fiori progress bar for busy indication if either update or delete action is clicked upon
     */
    private FioriProgressBar progressBar;

    /*
     * Service connection object callbacks when service is bound or lost
     */
    private ServiceConnection serviceConnection;

    private SAPServiceManager sapServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        errorHandler = ((SAPWizardApplication)getApplication()).getErrorHandler();

        sapServiceManager = ((SAPWizardApplication)getApplication()).getSAPServiceManager();

        setContentView(R.layout.activity_entity_set_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        usageUtil = ((SAPWizardApplication) getApplication()).getUsageUtil();
        usageUtil.eventBehaviorViewDisplayed(EntitySetListActivity.class.getSimpleName(),
                "elementId", "onCreate", "called");

        entitySetNames.clear();
        entitySetNameMap.clear();
        for (EntitySetName entitySet : EntitySetName.values()) {
            String entitySetTitle = getResources().getString(entitySet.getTitleId());
            entitySetNames.add(entitySetTitle);
            entitySetNameMap.put(entitySetTitle, entitySet);
        }

        final ListView listView = findViewById(R.id.entity_list);
        final EntitySetListAdapter adapter = new EntitySetListAdapter(this, R.layout.element_entity_set_list, entitySetNames);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            EntitySetName entitySetName = entitySetNameMap.get(adapter.getItem(position));
            usageUtil.eventBehaviorUserInteraction(EntitySetListActivity.class.getSimpleName(),
                    "position: " + position, "onClicked", entitySetName.getEntitySetName());
            Context context = EntitySetListActivity.this;
            Intent intent;
            switch (entitySetName) {
                case ActionEventss:
                    intent = new Intent(context, ActionEventssActivity.class);
                    break;
                case CatalogTypeBatchss:
                    intent = new Intent(context, CatalogTypeBatchssActivity.class);
                    break;
                case CatalogTypes:
                    intent = new Intent(context, CatalogTypesActivity.class);
                    break;
                case CauseOfConfirmations:
                    intent = new Intent(context, CauseOfConfirmationsActivity.class);
                    break;
                case CombinationOrderActivitys:
                    intent = new Intent(context, CombinationOrderActivitysActivity.class);
                    break;
                case ConfigDataBatchs:
                    intent = new Intent(context, ConfigDataBatchsActivity.class);
                    break;
                case ConfirmationTexts:
                    intent = new Intent(context, ConfirmationTextsActivity.class);
                    break;
                case Confirmations:
                    intent = new Intent(context, ConfirmationsActivity.class);
                    break;
                case DeletedEntitys:
                    intent = new Intent(context, DeletedEntitysActivity.class);
                    break;
                case Employees:
                    intent = new Intent(context, EmployeesActivity.class);
                    break;
                case EnrollOrderTypes:
                    intent = new Intent(context, EnrollOrderTypesActivity.class);
                    break;
                case EquipmentBatchss:
                    intent = new Intent(context, EquipmentBatchssActivity.class);
                    break;
                case Equipments:
                    intent = new Intent(context, EquipmentsActivity.class);
                    break;
                case InspectionCatalogCodeGroups:
                    intent = new Intent(context, InspectionCatalogCodeGroupsActivity.class);
                    break;
                case InspectionCatalogCodes:
                    intent = new Intent(context, InspectionCatalogCodesActivity.class);
                    break;
                case InspectionCatalogs:
                    intent = new Intent(context, InspectionCatalogsActivity.class);
                    break;
                case ListTechnicalEquipmentBatchs:
                    intent = new Intent(context, ListTechnicalEquipmentBatchsActivity.class);
                    break;
                case ListTechnicalEquipments:
                    intent = new Intent(context, ListTechnicalEquipmentsActivity.class);
                    break;
                case LocalInstallStructures:
                    intent = new Intent(context, LocalInstallStructuresActivity.class);
                    break;
                case LogSaps:
                    intent = new Intent(context, LogSapsActivity.class);
                    break;
                case LongTextNotificationBatchs:
                    intent = new Intent(context, LongTextNotificationBatchsActivity.class);
                    break;
                case LongTextNotifications:
                    intent = new Intent(context, LongTextNotificationsActivity.class);
                    break;
                case LongTextOrderBatchs:
                    intent = new Intent(context, LongTextOrderBatchsActivity.class);
                    break;
                case LongTextOrders:
                    intent = new Intent(context, LongTextOrdersActivity.class);
                    break;
                case MaintenanceActivityTypes:
                    intent = new Intent(context, MaintenanceActivityTypesActivity.class);
                    break;
                case MaintenanceControlParameters:
                    intent = new Intent(context, MaintenanceControlParametersActivity.class);
                    break;
                case MaintenanceNotificationBatchs:
                    intent = new Intent(context, MaintenanceNotificationBatchsActivity.class);
                    break;
                case MaintenanceNotificationCauses:
                    intent = new Intent(context, MaintenanceNotificationCausesActivity.class);
                    break;
                case MaintenanceNotificationItems:
                    intent = new Intent(context, MaintenanceNotificationItemsActivity.class);
                    break;
                case MaintenanceNotifications:
                    intent = new Intent(context, MaintenanceNotificationsActivity.class);
                    break;
                case MaintenanceOrderBatchs:
                    intent = new Intent(context, MaintenanceOrderBatchsActivity.class);
                    break;
                case MaintenanceOrderComponents:
                    intent = new Intent(context, MaintenanceOrderComponentsActivity.class);
                    break;
                case MaintenanceOrderOperations:
                    intent = new Intent(context, MaintenanceOrderOperationsActivity.class);
                    break;
                case MaintenanceOrderPriorityTextAssociations:
                    intent = new Intent(context, MaintenanceOrderPriorityTextAssociationsActivity.class);
                    break;
                case MaintenanceOrders:
                    intent = new Intent(context, MaintenanceOrdersActivity.class);
                    break;
                case MasterDataBatchs:
                    intent = new Intent(context, MasterDataBatchsActivity.class);
                    break;
                case MaterialDescriptions:
                    intent = new Intent(context, MaterialDescriptionsActivity.class);
                    break;
                case NotificationTypeTexts:
                    intent = new Intent(context, NotificationTypeTextsActivity.class);
                    break;
                case OperationMaterials:
                    intent = new Intent(context, OperationMaterialsActivity.class);
                    break;
                case OrderRuleTypeDataBatchs:
                    intent = new Intent(context, OrderRuleTypeDataBatchsActivity.class);
                    break;
                case OrderRuleTypes:
                    intent = new Intent(context, OrderRuleTypesActivity.class);
                    break;
                case OrderTypeTexts:
                    intent = new Intent(context, OrderTypeTextsActivity.class);
                    break;
                case Parameterss:
                    intent = new Intent(context, ParameterssActivity.class);
                    break;
                case PriorityTexts:
                    intent = new Intent(context, PriorityTextsActivity.class);
                    break;
                case Users:
                    intent = new Intent(context, UsersActivity.class);
                    break;
                case WorkCenterDescriptions:
                    intent = new Intent(context, WorkCenterDescriptionsActivity.class);
                    break;
                case WorkCenters:
                    intent = new Intent(context, WorkCentersActivity.class);
                    break;
                    default:
                        return;
            }
            context.startActivity(intent);
        });
            
        // if it were started due to a push message, then present it as a dialog
        String notificationId = getIntent().getStringExtra(SAPFirebaseMessagingService.NOTIFICATION_ID_EXTRA);
        RemoteMessage notificationMessage = getIntent().getParcelableExtra(SAPFirebaseMessagingService.NOTIFICATION_DATA);
        if (notificationId != null) {
            PushNotificationActivity.presentPushMessage(this, notificationId);
        }
    }

    public class EntitySetListAdapter extends ArrayAdapter<String> {

        EntitySetListAdapter(@NonNull Context context, int resource, List<String> entitySetNames) {
            super(context, resource, entitySetNames);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            EntitySetName entitySetName = entitySetNameMap.get(getItem(position));
            if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_entity_set_list, parent, false);
            }
            String headLineName = getResources().getString(entitySetName.titleId);
            ObjectCell entitySetCell = convertView.findViewById(R.id.entity_set_name);
            entitySetCell.setHeadline(headLineName);
            entitySetCell.setDetailImage(entitySetName.iconId);
            return convertView;
        }
    }
                
    @Override
    public void onBackPressed() {
            moveTaskToBack(true);
    }
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, SETTINGS_SCREEN_ITEM, 0, R.string.menu_item_settings);
        menu.add(0, SYNC_ACTION_ITEM, 1, R.string.synchronize_action);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LOGGER.debug("onOptionsItemSelected: " + item.getTitle());
        switch (item.getItemId()) {
            case SETTINGS_SCREEN_ITEM:
                LOGGER.debug("settings screen menu item selected.");
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivityForResult(intent, SETTINGS_SCREEN_ITEM);
                return true;

            case SYNC_ACTION_ITEM:
                synchronize(() -> synchronizeConclusion());
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOGGER.debug("EntitySetListActivity::onActivityResult, request code: " + requestCode + " result code: " + resultCode);
        if (requestCode == SETTINGS_SCREEN_ITEM) {
            LOGGER.debug("Calling AppState to retrieve settings after settings screen is closed.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            syncService = null;
        }
    }

    private void synchronize(Action0 syncCompleteHandler) {
        if (progressBar == null) {
            progressBar = getWindow().getDecorView().findViewById(R.id.sync_indeterminate);
        }
        progressBar.setVisibility(View.VISIBLE);
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                syncService = ((OfflineODataSyncService.LocalBinder)service).getService();
                isBound = true;
                sapServiceManager.synchronize(syncService,
                    ()-> EntitySetListActivity.this.runOnUiThread(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        syncCompleteHandler.call();
                    }),
                    error-> EntitySetListActivity.this.runOnUiThread(()-> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Resources res = getResources();
                        ErrorMessage errorMessage = new ErrorMessage(res.getString(R.string.synchronize_failure),
                                res.getString(R.string.synchronize_failure_detail));
                        errorHandler.sendErrorMessage(errorMessage);
                    }));
            }

            public void onServiceDisconnected(ComponentName className) {
                syncService = null;
                isBound = false;
            }
        };

        if (bindService(new Intent(this, OfflineODataSyncService.class),
                serviceConnection, Context.BIND_AUTO_CREATE)) {
        } else {
            unbindService(serviceConnection);
            LOGGER.error("Bind service failure");
        }
    }

    private void synchronizeConclusion() {
        unbindService(serviceConnection);
        isBound = false;
        syncService = null;
    }
}
