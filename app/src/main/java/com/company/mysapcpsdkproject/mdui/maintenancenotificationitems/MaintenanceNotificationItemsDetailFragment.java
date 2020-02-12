package com.company.mysapcpsdkproject.mdui.maintenancenotificationitems;

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
import com.company.mysapcpsdkproject.databinding.FragmentMaintenancenotificationitemsDetailBinding;
import com.company.mysapcpsdkproject.mdui.BundleKeys;
import com.company.mysapcpsdkproject.mdui.InterfacedFragment;
import com.company.mysapcpsdkproject.mdui.UIConstants;
import com.company.mysapcpsdkproject.mdui.EntityKeyUtil;
import com.company.mysapcpsdkproject.repository.OperationResult;
import com.company.mysapcpsdkproject.viewmodel.maintenancenotificationitem.MaintenanceNotificationItemViewModel;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainerMetadata.EntitySets;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceNotificationItem;
import com.sap.cloud.mobile.fiori.object.ObjectHeader;
import com.sap.cloud.mobile.odata.DataValue;
import com.sap.cloud.mobile.odata.DataValueList;
import com.sap.cloud.mobile.odata.EntityKey;
import com.company.mysapcpsdkproject.mdui.inspectioncatalogcodes.InspectionCatalogCodesActivity;
import com.company.mysapcpsdkproject.mdui.inspectioncatalogcodegroups.InspectionCatalogCodeGroupsActivity;
import com.company.mysapcpsdkproject.mdui.maintenancenotifications.MaintenanceNotificationsActivity;
import com.company.mysapcpsdkproject.mdui.maintenancenotificationcauses.MaintenanceNotificationCausesActivity;
/**
 * A fragment representing a single MaintenanceNotificationItem detail screen.
 * This fragment is contained in an MaintenanceNotificationItemsActivity.
 */
public class MaintenanceNotificationItemsDetailFragment extends InterfacedFragment<MaintenanceNotificationItem> {

    /** Generated data binding class based on layout file */
    private FragmentMaintenancenotificationitemsDetailBinding binding;

    /** MaintenanceNotificationItem entity to be displayed */
    private MaintenanceNotificationItem maintenanceNotificationItemEntity = null;

    /** Fiori ObjectHeader component used when entity is to be displayed on phone */
    private ObjectHeader objectHeader;

    /** View model of the entity type that the displayed entity belongs to */
    private MaintenanceNotificationItemViewModel viewModel;

    /** Error handler to display message should error occurs */
    private ErrorHandler errorHandler;

    /** Arguments: MaintenanceNotificationItem for display */
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
        viewModel = ViewModelProviders.of(currentActivity).get(MaintenanceNotificationItemViewModel.class);
        viewModel.getDeleteResult().observe(this, result -> {
            onDeleteComplete(result);
        });
        viewModel.getSelectedEntity().observe(this, entity -> {
            maintenanceNotificationItemEntity = entity;
            binding.setMaintenanceNotificationItem(entity);
            setupObjectHeader();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_item:
                listener.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, maintenanceNotificationItemEntity);
                return true;
            case R.id.delete_item:
                listener.onFragmentStateChange(UIConstants.EVENT_ASK_DELETE_CONFIRMATION,null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onNavigationClickedToInspectionCatalogCodes_InspectionCatalogCodeDetails(View v) {
        Intent intent = new Intent(this.currentActivity, InspectionCatalogCodesActivity.class);
        intent.putExtra("parent", maintenanceNotificationItemEntity);
        intent.putExtra("navigation", "InspectionCatalogCodeDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToInspectionCatalogCodeGroups_InspectionCatalogCodeGroupDetails(View v) {
        Intent intent = new Intent(this.currentActivity, InspectionCatalogCodeGroupsActivity.class);
        intent.putExtra("parent", maintenanceNotificationItemEntity);
        intent.putExtra("navigation", "InspectionCatalogCodeGroupDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToMaintenanceNotifications_MaintenanceNotificationDetails(View v) {
        Intent intent = new Intent(this.currentActivity, MaintenanceNotificationsActivity.class);
        intent.putExtra("parent", maintenanceNotificationItemEntity);
        intent.putExtra("navigation", "MaintenanceNotificationDetails");
        startActivity(intent);
    }

    public void onNavigationClickedToMaintenanceNotificationCauses_MaintenanceNotificationCauseDetails(View v) {
        Intent intent = new Intent(this.currentActivity, MaintenanceNotificationCausesActivity.class);
        intent.putExtra("parent", maintenanceNotificationItemEntity);
        intent.putExtra("navigation", "MaintenanceNotificationCauseDetails");
        startActivity(intent);
    }


    /** Completion callback for delete operation */
    private void onDeleteComplete(@NonNull OperationResult<MaintenanceNotificationItem> result) {
        if( progressBar != null ) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        viewModel.removeAllSelected(); //to make sure the 'action mode' not activated in the list
        Exception ex = result.getError();
        if (ex != null) {
            handleError(ex);
            return;
        }
        listener.onFragmentStateChange(UIConstants.EVENT_DELETION_COMPLETED, maintenanceNotificationItemEntity);
    }

    /**
     * Set detail image of ObjectHeader.
     * When the entity does not provides picture, set the first character of the masterProperty.
     */
    private void setDetailImage(@NonNull ObjectHeader objectHeader, @NonNull MaintenanceNotificationItem maintenanceNotificationItemEntity) {
        viewModel.downloadMedia(maintenanceNotificationItemEntity, media -> {
            objectHeader.prepareDetailImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
            Drawable image = new BitmapDrawable(currentActivity.getResources(), BitmapFactory.decodeByteArray(media, 0, media.length));
            objectHeader.setDetailImage(image);
        }, error -> {
            if (maintenanceNotificationItemEntity.getDataValue(MaintenanceNotificationItem.assembly) != null && !maintenanceNotificationItemEntity.getDataValue(MaintenanceNotificationItem.assembly).toString().isEmpty()) {
                objectHeader.setDetailImageCharacter(maintenanceNotificationItemEntity.getDataValue(MaintenanceNotificationItem.assembly).toString().substring(0, 1));
            } else {
                objectHeader.setDetailImageCharacter("?");
            }
        });
    }

    /**
     * Setup ObjectHeader with an instance of MaintenanceNotificationItem
     */
    private void setupObjectHeader() {
        Toolbar secondToolbar = currentActivity.findViewById(R.id.secondaryToolbar);
        if (secondToolbar != null) {
            secondToolbar.setTitle(maintenanceNotificationItemEntity.getEntityType().getLocalName());
        } else {
            currentActivity.setTitle(maintenanceNotificationItemEntity.getEntityType().getLocalName());
        }

        // Object Header is not available in tablet mode
        objectHeader = currentActivity.findViewById(R.id.objectHeader);
        if (objectHeader != null) {
            // Use of getDataValue() avoids the knowledge of what data type the master property is.
            // This is a convenience for wizard generated code. Normally, developer will use the proxy class
            // get<Property>() method and add code to convert to string
            DataValue dataValue = maintenanceNotificationItemEntity.getDataValue(MaintenanceNotificationItem.assembly);
            if (dataValue != null) {
                objectHeader.setHeadline(dataValue.toString());
            } else {
                objectHeader.setHeadline(null);
            }
            // EntityKey in string format: '{"key":value,"key2":value2}'
            objectHeader.setSubheadline(EntityKeyUtil.getOptionalEntityKey(maintenanceNotificationItemEntity));
            objectHeader.setTag("#tag1", 0);
            objectHeader.setTag("#tag3", 2);
            objectHeader.setTag("#tag2", 1);

            objectHeader.setBody("You can set the header body text here.");
            objectHeader.setFootnote("You can set the header footnote here.");
            objectHeader.setDescription("You can add a detailed item description here.");

            setDetailImage(objectHeader, maintenanceNotificationItemEntity);
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
        binding = FragmentMaintenancenotificationitemsDetailBinding.inflate(inflater, container, false);
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
