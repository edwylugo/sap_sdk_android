package com.company.mysapcpsdkproject.viewmodel.listtechnicalequipmentbatch;

import android.app.Application;
import android.os.Parcelable;

import com.company.mysapcpsdkproject.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.ipmcontainer.ListTechnicalEquipmentBatch;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainerMetadata.EntitySets;

/*
 * Represents View model for ListTechnicalEquipmentBatch
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class ListTechnicalEquipmentBatchViewModel extends EntityViewModel<ListTechnicalEquipmentBatch> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public ListTechnicalEquipmentBatchViewModel(Application application) {
        super(application, EntitySets.listTechnicalEquipmentBatchs, ListTechnicalEquipmentBatch.id);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public ListTechnicalEquipmentBatchViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.listTechnicalEquipmentBatchs, ListTechnicalEquipmentBatch.id, navigationPropertyName, entityData);
    }
}
