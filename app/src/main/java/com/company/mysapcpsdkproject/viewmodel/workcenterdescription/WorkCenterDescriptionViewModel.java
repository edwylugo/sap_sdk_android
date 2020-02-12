package com.company.mysapcpsdkproject.viewmodel.workcenterdescription;

import android.app.Application;
import android.os.Parcelable;

import com.company.mysapcpsdkproject.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.ipmcontainer.WorkCenterDescription;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainerMetadata.EntitySets;

/*
 * Represents View model for WorkCenterDescription
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class WorkCenterDescriptionViewModel extends EntityViewModel<WorkCenterDescription> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public WorkCenterDescriptionViewModel(Application application) {
        super(application, EntitySets.workCenterDescriptions, WorkCenterDescription.categoryForResource);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public WorkCenterDescriptionViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.workCenterDescriptions, WorkCenterDescription.categoryForResource, navigationPropertyName, entityData);
    }
}
