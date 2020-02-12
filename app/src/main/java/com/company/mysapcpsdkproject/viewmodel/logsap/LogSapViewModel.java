package com.company.mysapcpsdkproject.viewmodel.logsap;

import android.app.Application;
import android.os.Parcelable;

import com.company.mysapcpsdkproject.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.ipmcontainer.LogSap;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainerMetadata.EntitySets;

/*
 * Represents View model for LogSap
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class LogSapViewModel extends EntityViewModel<LogSap> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public LogSapViewModel(Application application) {
        super(application, EntitySets.logSaps, LogSap.actionEventsType);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public LogSapViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.logSaps, LogSap.actionEventsType, navigationPropertyName, entityData);
    }
}
