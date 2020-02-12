package com.company.mysapcpsdkproject.viewmodel.inspectioncatalogcode;

import android.app.Application;
import android.os.Parcelable;

import com.company.mysapcpsdkproject.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.ipmcontainer.InspectionCatalogCode;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainerMetadata.EntitySets;

/*
 * Represents View model for InspectionCatalogCode
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class InspectionCatalogCodeViewModel extends EntityViewModel<InspectionCatalogCode> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public InspectionCatalogCodeViewModel(Application application) {
        super(application, EntitySets.inspectionCatalogCodes, InspectionCatalogCode.dataRecordDeleted);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public InspectionCatalogCodeViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.inspectionCatalogCodes, InspectionCatalogCode.dataRecordDeleted, navigationPropertyName, entityData);
    }
}
