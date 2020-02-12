package com.company.mysapcpsdkproject.mdui.masterdatabatchs;

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
import com.company.mysapcpsdkproject.databinding.FragmentMasterdatabatchsDetailBinding;
import com.company.mysapcpsdkproject.mdui.BundleKeys;
import com.company.mysapcpsdkproject.mdui.InterfacedFragment;
import com.company.mysapcpsdkproject.mdui.UIConstants;
import com.company.mysapcpsdkproject.mdui.EntityKeyUtil;
import com.company.mysapcpsdkproject.repository.OperationResult;
import com.company.mysapcpsdkproject.viewmodel.masterdatabatch.MasterDataBatchViewModel;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainerMetadata.EntitySets;
import com.sap.cloud.android.odata.ipmcontainer.MasterDataBatch;
import com.sap.cloud.mobile.fiori.object.ObjectHeader;
import com.sap.cloud.mobile.odata.DataValue;
import com.sap.cloud.mobile.odata.DataValueList;
import com.sap.cloud.mobile.odata.EntityKey;
import com.company.mysapcpsdkproject.mdui.catalogtypes.CatalogTypesActivity;
import com.company.mysapcpsdkproject.mdui.causeofconfirmations.CauseOfConfirmationsActivity;
import com.company.mysapcpsdkproject.mdui.employees.EmployeesActivity;
import com.company.mysapcpsdkproject.mdui.equipments.EquipmentsActivity;
import com.company.mysapcpsdkproject.mdui.combinationorderactivitys.CombinationOrderActivitysActivity;
import com.company.mysapcpsdkproject.mdui.inspectioncatalogcodes.InspectionCatalogCodesActivity;
import com.company.mysapcpsdkproject.mdui.inspectioncatalogcodegroups.InspectionCatalogCodeGroupsActivity;
import com.company.mysapcpsdkproject.mdui.listtechnicalequipments.ListTechnicalEquipmentsActivity;
import com.company.mysapcpsdkproject.mdui.localinstallstructures.LocalInstallStructuresActivity;
import com.company.mysapcpsdkproject.mdui.maintenancecontrolparameters.MaintenanceControlParametersActivity;
import com.company.mysapcpsdkproject.mdui.maintenanceactivitytypes.MaintenanceActivityTypesActivity;
import com.company.mysapcpsdkproject.mdui.materialdescriptions.MaterialDescriptionsActivity;
import com.company.mysapcpsdkproject.mdui.ordertypetexts.OrderTypeTextsActivity;
import com.company.mysapcpsdkproject.mdui.prioritytexts.PriorityTextsActivity;
import com.company.mysapcpsdkproject.mdui.notificationtypetexts.NotificationTypeTextsActivity;
import com.company.mysapcpsdkproject.mdui.workcenters.WorkCentersActivity;
/**
 * A fragment representing a single MasterDataBatch detail screen.
 * This fragment is contained in an MasterDataBatchsActivity.
 */
public class MasterDataBatchsDetailFragment extends InterfacedFragment<MasterDataBatch> {

    /** Generated data binding class based on layout file */
    private FragmentMasterdatabatchsDetailBinding binding;

    /** MasterDataBatch entity to be displayed */
    private MasterDataBatch masterDataBatchEntity = null;

    /** Fiori ObjectHeader component used when entity is to be displayed on phone */
    private ObjectHeader objectHeader;

    /** View model of the entity type that the displayed entity belongs to */
    private MasterDataBatchViewModel viewModel;

    /** Error handler to display message should error occurs */
    private ErrorHandler errorHandler;

    /** Arguments: MasterDataBatch for display */
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
        viewModel = ViewModelProviders.of(currentActivity).get(MasterDataBatchViewModel.class);
        viewModel.getDeleteResult().observe(this, result -> {
            onDeleteComplete(result);
        });
        viewModel.getSelectedEntity().observe(this, entity -> {
            masterDataBatchEntity = entity;
            binding.setMasterDataBatch(entity);
            setupObjectHeader();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_item:
                listener.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, masterDataBatchEntity);
                return true;
            case R.id.delete_item:
                listener.onFragmentStateChange(UIConstants.EVENT_ASK_DELETE_CONFIRMATION,null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onNavigationClickedToCatalogTypes_CatalogTypeDetails(View v) {
        Intent intent = new Intent(this.currentActivity, CatalogTypesActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "CatalogTypeDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToCauseOfConfirmations_CauseOfConfirmationDetails(View v) {
        Intent intent = new Intent(this.currentActivity, CauseOfConfirmationsActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "CauseOfConfirmationDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToEmployees_EmployeeDetails(View v) {
        Intent intent = new Intent(this.currentActivity, EmployeesActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "EmployeeDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToEquipments_EquipmentDetails(View v) {
        Intent intent = new Intent(this.currentActivity, EquipmentsActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "EquipmentDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToCombinationOrderActivitys_CombinationOrderActivityDetails(View v) {
        Intent intent = new Intent(this.currentActivity, CombinationOrderActivitysActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "CombinationOrderActivityDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToInspectionCatalogCodes_InspectionCatalogCodeDetails(View v) {
        Intent intent = new Intent(this.currentActivity, InspectionCatalogCodesActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "InspectionCatalogCodeDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToInspectionCatalogCodeGroups_InspectionCatalogCodeGroupDetails(View v) {
        Intent intent = new Intent(this.currentActivity, InspectionCatalogCodeGroupsActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "InspectionCatalogCodeGroupDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToListTechnicalEquipments_ListTechnicalEquipmentDetails(View v) {
        Intent intent = new Intent(this.currentActivity, ListTechnicalEquipmentsActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "ListTechnicalEquipmentDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToLocalInstallStructures_LocalInstallStructureDetails(View v) {
        Intent intent = new Intent(this.currentActivity, LocalInstallStructuresActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "LocalInstallStructureDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToMaintenanceControlParameters_MaintenanceControlParameterDetails(View v) {
        Intent intent = new Intent(this.currentActivity, MaintenanceControlParametersActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "MaintenanceControlParameterDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToMaintenanceActivityTypes_MaintenanceActivityTypeDetails(View v) {
        Intent intent = new Intent(this.currentActivity, MaintenanceActivityTypesActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "MaintenanceActivityTypeDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToMaterialDescriptions_MaterialDescriptionDetails(View v) {
        Intent intent = new Intent(this.currentActivity, MaterialDescriptionsActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "MaterialDescriptionDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToOrderTypeTexts_OrderTypeTextDetails(View v) {
        Intent intent = new Intent(this.currentActivity, OrderTypeTextsActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "OrderTypeTextDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToPriorityTexts_PriorityTextDetails(View v) {
        Intent intent = new Intent(this.currentActivity, PriorityTextsActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "PriorityTextDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToNotificationTypeTexts_NotificationTypeTextDetails(View v) {
        Intent intent = new Intent(this.currentActivity, NotificationTypeTextsActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "NotificationTypeTextDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToWorkCenters_WorkCenterDetails(View v) {
        Intent intent = new Intent(this.currentActivity, WorkCentersActivity.class);
        intent.putExtra("parent", masterDataBatchEntity);
        intent.putExtra("navigation", "WorkCenterDetails");
        startActivity(intent);
    }


    /** Completion callback for delete operation */
    private void onDeleteComplete(@NonNull OperationResult<MasterDataBatch> result) {
        if( progressBar != null ) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        viewModel.removeAllSelected(); //to make sure the 'action mode' not activated in the list
        Exception ex = result.getError();
        if (ex != null) {
            handleError(ex);
            return;
        }
        listener.onFragmentStateChange(UIConstants.EVENT_DELETION_COMPLETED, masterDataBatchEntity);
    }

    /**
     * Set detail image of ObjectHeader.
     * When the entity does not provides picture, set the first character of the masterProperty.
     */
    private void setDetailImage(@NonNull ObjectHeader objectHeader, @NonNull MasterDataBatch masterDataBatchEntity) {
        viewModel.downloadMedia(masterDataBatchEntity, media -> {
            objectHeader.prepareDetailImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
            Drawable image = new BitmapDrawable(currentActivity.getResources(), BitmapFactory.decodeByteArray(media, 0, media.length));
            objectHeader.setDetailImage(image);
        }, error -> {
            if (masterDataBatchEntity.getDataValue(MasterDataBatch.id) != null && !masterDataBatchEntity.getDataValue(MasterDataBatch.id).toString().isEmpty()) {
                objectHeader.setDetailImageCharacter(masterDataBatchEntity.getDataValue(MasterDataBatch.id).toString().substring(0, 1));
            } else {
                objectHeader.setDetailImageCharacter("?");
            }
        });
    }

    /**
     * Setup ObjectHeader with an instance of MasterDataBatch
     */
    private void setupObjectHeader() {
        Toolbar secondToolbar = currentActivity.findViewById(R.id.secondaryToolbar);
        if (secondToolbar != null) {
            secondToolbar.setTitle(masterDataBatchEntity.getEntityType().getLocalName());
        } else {
            currentActivity.setTitle(masterDataBatchEntity.getEntityType().getLocalName());
        }

        // Object Header is not available in tablet mode
        objectHeader = currentActivity.findViewById(R.id.objectHeader);
        if (objectHeader != null) {
            // Use of getDataValue() avoids the knowledge of what data type the master property is.
            // This is a convenience for wizard generated code. Normally, developer will use the proxy class
            // get<Property>() method and add code to convert to string
            DataValue dataValue = masterDataBatchEntity.getDataValue(MasterDataBatch.id);
            if (dataValue != null) {
                objectHeader.setHeadline(dataValue.toString());
            } else {
                objectHeader.setHeadline(null);
            }
            // EntityKey in string format: '{"key":value,"key2":value2}'
            objectHeader.setSubheadline(EntityKeyUtil.getOptionalEntityKey(masterDataBatchEntity));
            objectHeader.setTag("#tag1", 0);
            objectHeader.setTag("#tag3", 2);
            objectHeader.setTag("#tag2", 1);

            objectHeader.setBody("You can set the header body text here.");
            objectHeader.setFootnote("You can set the header footnote here.");
            objectHeader.setDescription("You can add a detailed item description here.");

            setDetailImage(objectHeader, masterDataBatchEntity);
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
        binding = FragmentMasterdatabatchsDetailBinding.inflate(inflater, container, false);
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
