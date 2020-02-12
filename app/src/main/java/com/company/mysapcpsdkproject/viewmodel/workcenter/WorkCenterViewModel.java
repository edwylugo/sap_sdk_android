package com.company.mysapcpsdkproject.viewmodel.workcenter;

import android.app.Application;
import android.os.Parcelable;

import com.company.mysapcpsdkproject.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.ipmcontainer.WorkCenter;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainerMetadata.EntitySets;

/*
 * Represents View model for WorkCenter
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class WorkCenterViewModel extends EntityViewModel<WorkCenter> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public WorkCenterViewModel(Application application) {
        super(application, EntitySets.workCenters, WorkCenter.languageKey);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public WorkCenterViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.workCenters, WorkCenter.languageKey, navigationPropertyName, entityData);
    }
}
