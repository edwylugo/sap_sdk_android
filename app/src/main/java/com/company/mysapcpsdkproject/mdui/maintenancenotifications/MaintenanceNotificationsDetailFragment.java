package com.company.mysapcpsdkproject.mdui.maintenancenotifications;

import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.sap.cloud.mobile.odata.DataValueList;
import com.sap.cloud.mobile.odata.EntityKey;
import com.company.mysapcpsdkproject.R;
import com.company.mysapcpsdkproject.app.ErrorHandler;
import com.company.mysapcpsdkproject.app.ErrorMessage;
import com.company.mysapcpsdkproject.app.SAPWizardApplication;
import com.company.mysapcpsdkproject.databinding.FragmentMaintenancenotificationsDetailBinding;
import com.company.mysapcpsdkproject.mdui.BundleKeys;
import com.company.mysapcpsdkproject.mdui.InterfacedFragment;
import com.company.mysapcpsdkproject.mdui.UIConstants;
import com.company.mysapcpsdkproject.mdui.EntityKeyUtil;
import com.company.mysapcpsdkproject.repository.OperationResult;
import com.company.mysapcpsdkproject.viewmodel.maintenancenotification.MaintenanceNotificationViewModel;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainerMetadata.EntitySets;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceNotification;
import com.sap.cloud.mobile.fiori.object.ObjectHeader;
import com.sap.cloud.mobile.odata.DataValue;
import com.sap.cloud.mobile.odata.DataValueList;
import com.sap.cloud.mobile.odata.EntityKey;
import com.company.mysapcpsdkproject.mdui.listtechnicalequipments.ListTechnicalEquipmentsActivity;
import com.company.mysapcpsdkproject.mdui.localinstallstructures.LocalInstallStructuresActivity;
import com.company.mysapcpsdkproject.mdui.longtextnotifications.LongTextNotificationsActivity;
import com.company.mysapcpsdkproject.mdui.equipments.EquipmentsActivity;
import com.company.mysapcpsdkproject.mdui.maintenancenotificationitems.MaintenanceNotificationItemsActivity;
import com.company.mysapcpsdkproject.mdui.notificationtypetexts.NotificationTypeTextsActivity;
import com.company.mysapcpsdkproject.mdui.workcenters.WorkCentersActivity;
/**
 * A fragment representing a single MaintenanceNotification detail screen.
 * This fragment is contained in an MaintenanceNotificationsActivity.
 */
public class MaintenanceNotificationsDetailFragment extends InterfacedFragment<MaintenanceNotification> {

    /** Generated data binding class based on layout file */
    private FragmentMaintenancenotificationsDetailBinding binding;

    /** MaintenanceNotification entity to be displayed */
    private MaintenanceNotification maintenanceNotificationEntity = null;

    /** Fiori ObjectHeader component used when entity is to be displayed on phone */
    private ObjectHeader objectHeader;

    /** View model of the entity type that the displayed entity belongs to */
    private MaintenanceNotificationViewModel viewModel;

    /** Error handler to display message should error occurs */
    private ErrorHandler errorHandler;

    /** Arguments: MaintenanceNotification for display */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = R.menu.itemlist_view_options;
        errorHandler = ((SAPWizardApplication) currentActivity.getApplication()).getErrorHandler();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return setupDataBinding(inflater, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(currentActivity).get(MaintenanceNotificationViewModel.class);
        viewModel.getDeleteResult().observe(this, result -> {
            onDeleteComplete(result);
        });
        viewModel.getSelectedEntity().observe(this, entity -> {
            maintenanceNotificationEntity = entity;
            binding.setMaintenanceNotification(entity);
            setupObjectHeader();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_item:
                listener.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, maintenanceNotificationEntity);
                return true;
            case R.id.delete_item:
                listener.onFragmentStateChange(UIConstants.EVENT_ASK_DELETE_CONFIRMATION,null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onNavigationClickedToListTechnicalEquipments_ListTechnicalEquipmentDetails(View v) {
        Intent intent = new Intent(this.currentActivity, ListTechnicalEquipmentsActivity.class);
        intent.putExtra("parent", maintenanceNotificationEntity);
        intent.putExtra("navigation", "ListTechnicalEquipmentDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToLocalInstallStructures_LocalInstallStructureDetails(View v) {
        Intent intent = new Intent(this.currentActivity, LocalInstallStructuresActivity.class);
        intent.putExtra("parent", maintenanceNotificationEntity);
        intent.putExtra("navigation", "LocalInstallStructureDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToLongTextNotifications_LongTextNotificationDetails(View v) {
        Intent intent = new Intent(this.currentActivity, LongTextNotificationsActivity.class);
        intent.putExtra("parent", maintenanceNotificationEntity);
        intent.putExtra("navigation", "LongTextNotificationDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToEquipments_EquipmentDetails(View v) {
        Intent intent = new Intent(this.currentActivity, EquipmentsActivity.class);
        intent.putExtra("parent", maintenanceNotificationEntity);
        intent.putExtra("navigation", "EquipmentDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToMaintenanceNotificationItems_MaintenanceNotificationItemDetails(View v) {
        Intent intent = new Intent(this.currentActivity, MaintenanceNotificationItemsActivity.class);
        intent.putExtra("parent", maintenanceNotificationEntity);
        intent.putExtra("navigation", "MaintenanceNotificationItemDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToNotificationTypeTexts_NotificationTypeTextDetails(View v) {
        Intent intent = new Intent(this.currentActivity, NotificationTypeTextsActivity.class);
        intent.putExtra("parent", maintenanceNotificationEntity);
        intent.putExtra("navigation", "NotificationTypeTextDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToWorkCenters_WorkCenterDetails(View v) {
        Intent intent = new Intent(this.currentActivity, WorkCentersActivity.class);
        intent.putExtra("parent", maintenanceNotificationEntity);
        intent.putExtra("navigation", "WorkCenterDetails");
        startActivity(intent);
    }


    /** Completion callback for delete operation */
    private void onDeleteComplete(@NonNull OperationResult<MaintenanceNotification> result) {
        if( progressBar != null ) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        viewModel.removeAllSelected(); //to make sure the 'action mode' not activated in the list
        Exception ex = result.getError();
        if (ex != null) {
            handleError(ex);
            return;
        }
        listener.onFragmentStateChange(UIConstants.EVENT_DELETION_COMPLETED, maintenanceNotificationEntity);
    }

    /**
     * Set detail image of ObjectHeader.
     * When the entity does not provides picture, set the first character of the masterProperty.
     */
    private void setDetailImage(@NonNull ObjectHeader objectHeader, @NonNull MaintenanceNotification maintenanceNotificationEntity) {
        viewModel.downloadMedia(maintenanceNotificationEntity, media -> {
            objectHeader.prepareDetailImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
            Drawable image = new BitmapDrawable(currentActivity.getResources(), BitmapFactory.decodeByteArray(media, 0, media.length));
            objectHeader.setDetailImage(image);
        }, error -> {
            if (maintenanceNotificationEntity.getDataValue(MaintenanceNotification.assembly) != null && !maintenanceNotificationEntity.getDataValue(MaintenanceNotification.assembly).toString().isEmpty()) {
                objectHeader.setDetailImageCharacter(maintenanceNotificationEntity.getDataValue(MaintenanceNotification.assembly).toString().substring(0, 1));
            } else {
                objectHeader.setDetailImageCharacter("?");
            }
        });
    }

    /**
     * Setup ObjectHeader with an instance of MaintenanceNotification
     */
    private void setupObjectHeader() {
        Toolbar secondToolbar = currentActivity.findViewById(R.id.secondaryToolbar);
        if (secondToolbar != null) {
            secondToolbar.setTitle(maintenanceNotificationEntity.getEntityType().getLocalName());
        } else {
            currentActivity.setTitle(maintenanceNotificationEntity.getEntityType().getLocalName());
        }

        // Object Header is not available in tablet mode
        objectHeader = currentActivity.findViewById(R.id.objectHeader);
        if (objectHeader != null) {
            // Use of getDataValue() avoids the knowledge of what data type the master property is.
            // This is a convenience for wizard generated code. Normally, developer will use the proxy class
            // get<Property>() method and add code to convert to string
            DataValue dataValue = maintenanceNotificationEntity.getDataValue(MaintenanceNotification.assembly);
            if (dataValue != null) {
                objectHeader.setHeadline(dataValue.toString());
            } else {
                objectHeader.setHeadline(null);
            }
            // EntityKey in string format: '{"key":value,"key2":value2}'
            objectHeader.setSubheadline(EntityKeyUtil.getOptionalEntityKey(maintenanceNotificationEntity));
            objectHeader.setTag("#tag1", 0);
            objectHeader.setTag("#tag3", 2);
            objectHeader.setTag("#tag2", 1);

            objectHeader.setBody("You can set the header body text here.");
            objectHeader.setFootnote("You can set the header footnote here.");
            objectHeader.setDescription("You can add a detailed item description here.");

            setDetailImage(objectHeader, maintenanceNotificationEntity);
            objectHeader.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Set up databinding for this view
     *
     * @param inflater - layout inflater from onCreateView
     * @param container - view group from onCreateView
     * @return view - rootView from generated databinding code
     */
    private View setupDataBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentMaintenancenotificationsDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        binding.setHandler(this);
        return rootView;
    }

    /**
     * Notify user of error encountered during operation execution
     *
     * @param ex - exception encountered
     */
    private void handleError(Exception ex) {
        ErrorMessage errorMessage;
        errorMessage = new ErrorMessage(currentActivity.getResources().getString(R.string.delete_failed),
                currentActivity.getResources().getString(R.string.delete_failed_detail), ex, false);
        errorHandler.sendErrorMessage(errorMessage);
    }
}
