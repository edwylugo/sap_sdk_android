package com.company.mysapcpsdkproject.viewmodel;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import android.os.Parcelable;

import com.company.mysapcpsdkproject.viewmodel.actionevents.ActionEventsViewModel;
import com.company.mysapcpsdkproject.viewmodel.catalogtypebatchs.CatalogTypeBatchsViewModel;
import com.company.mysapcpsdkproject.viewmodel.catalogtype.CatalogTypeViewModel;
import com.company.mysapcpsdkproject.viewmodel.causeofconfirmation.CauseOfConfirmationViewModel;
import com.company.mysapcpsdkproject.viewmodel.combinationorderactivity.CombinationOrderActivityViewModel;
import com.company.mysapcpsdkproject.viewmodel.configdatabatch.ConfigDataBatchViewModel;
import com.company.mysapcpsdkproject.viewmodel.confirmationtext.ConfirmationTextViewModel;
import com.company.mysapcpsdkproject.viewmodel.confirmation.ConfirmationViewModel;
import com.company.mysapcpsdkproject.viewmodel.deletedentity.DeletedEntityViewModel;
import com.company.mysapcpsdkproject.viewmodel.employee.EmployeeViewModel;
import com.company.mysapcpsdkproject.viewmodel.enrollordertype.EnrollOrderTypeViewModel;
import com.company.mysapcpsdkproject.viewmodel.equipmentbatchs.EquipmentBatchsViewModel;
import com.company.mysapcpsdkproject.viewmodel.equipment.EquipmentViewModel;
import com.company.mysapcpsdkproject.viewmodel.inspectioncatalogcodegroup.InspectionCatalogCodeGroupViewModel;
import com.company.mysapcpsdkproject.viewmodel.inspectioncatalogcode.InspectionCatalogCodeViewModel;
import com.company.mysapcpsdkproject.viewmodel.inspectioncatalog.InspectionCatalogViewModel;
import com.company.mysapcpsdkproject.viewmodel.listtechnicalequipmentbatch.ListTechnicalEquipmentBatchViewModel;
import com.company.mysapcpsdkproject.viewmodel.listtechnicalequipment.ListTechnicalEquipmentViewModel;
import com.company.mysapcpsdkproject.viewmodel.localinstallstructure.LocalInstallStructureViewModel;
import com.company.mysapcpsdkproject.viewmodel.logsap.LogSapViewModel;
import com.company.mysapcpsdkproject.viewmodel.longtextnotificationbatch.LongTextNotificationBatchViewModel;
import com.company.mysapcpsdkproject.viewmodel.longtextnotification.LongTextNotificationViewModel;
import com.company.mysapcpsdkproject.viewmodel.longtextorderbatch.LongTextOrderBatchViewModel;
import com.company.mysapcpsdkproject.viewmodel.longtextorder.LongTextOrderViewModel;
import com.company.mysapcpsdkproject.viewmodel.maintenanceactivitytype.MaintenanceActivityTypeViewModel;
import com.company.mysapcpsdkproject.viewmodel.maintenancecontrolparameter.MaintenanceControlParameterViewModel;
import com.company.mysapcpsdkproject.viewmodel.maintenancenotificationbatch.MaintenanceNotificationBatchViewModel;
import com.company.mysapcpsdkproject.viewmodel.maintenancenotificationcause.MaintenanceNotificationCauseViewModel;
import com.company.mysapcpsdkproject.viewmodel.maintenancenotificationitem.MaintenanceNotificationItemViewModel;
import com.company.mysapcpsdkproject.viewmodel.maintenancenotification.MaintenanceNotificationViewModel;
import com.company.mysapcpsdkproject.viewmodel.maintenanceorderbatch.MaintenanceOrderBatchViewModel;
import com.company.mysapcpsdkproject.viewmodel.maintenanceordercomponent.MaintenanceOrderComponentViewModel;
import com.company.mysapcpsdkproject.viewmodel.maintenanceorderoperation.MaintenanceOrderOperationViewModel;
import com.company.mysapcpsdkproject.viewmodel.maintenanceorderprioritytextassociation.MaintenanceOrderPriorityTextAssociationViewModel;
import com.company.mysapcpsdkproject.viewmodel.maintenanceorder.MaintenanceOrderViewModel;
import com.company.mysapcpsdkproject.viewmodel.masterdatabatch.MasterDataBatchViewModel;
import com.company.mysapcpsdkproject.viewmodel.materialdescription.MaterialDescriptionViewModel;
import com.company.mysapcpsdkproject.viewmodel.notificationtypetext.NotificationTypeTextViewModel;
import com.company.mysapcpsdkproject.viewmodel.operationmaterial.OperationMaterialViewModel;
import com.company.mysapcpsdkproject.viewmodel.orderruletypedatabatch.OrderRuleTypeDataBatchViewModel;
import com.company.mysapcpsdkproject.viewmodel.orderruletype.OrderRuleTypeViewModel;
import com.company.mysapcpsdkproject.viewmodel.ordertypetext.OrderTypeTextViewModel;
import com.company.mysapcpsdkproject.viewmodel.parameters.ParametersViewModel;
import com.company.mysapcpsdkproject.viewmodel.prioritytext.PriorityTextViewModel;
import com.company.mysapcpsdkproject.viewmodel.user.UserViewModel;
import com.company.mysapcpsdkproject.viewmodel.workcenterdescription.WorkCenterDescriptionViewModel;
import com.company.mysapcpsdkproject.viewmodel.workcenter.WorkCenterViewModel;


/**
 * Custom factory class, which can create view models for entity subsets, which are
 * reached from a parent entity through a navigation property.
 */
public class EntityViewModelFactory implements ViewModelProvider.Factory {

	// application class
    private Application application;
	// name of the navigation property
    private String navigationPropertyName;
	// parent entity
    private Parcelable entityData;

	/**
	 * Creates a factory class for entity view models created following a navigation link.
	 *
	 * @param application parent application
	 * @param navigationPropertyName name of the navigation link
	 * @param entityData parent entity
	 */
    public EntityViewModelFactory(Application application, String navigationPropertyName, Parcelable entityData) {
        this.application = application;
        this.navigationPropertyName = navigationPropertyName;
        this.entityData = entityData;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        T retValue = null;
		switch(modelClass.getSimpleName()) {



			case "ActionEventsViewModel":
				retValue = (T) new ActionEventsViewModel(application, navigationPropertyName, entityData);
				break;
			case "CatalogTypeBatchsViewModel":
				retValue = (T) new CatalogTypeBatchsViewModel(application, navigationPropertyName, entityData);
				break;
			case "CatalogTypeViewModel":
				retValue = (T) new CatalogTypeViewModel(application, navigationPropertyName, entityData);
				break;
			case "CauseOfConfirmationViewModel":
				retValue = (T) new CauseOfConfirmationViewModel(application, navigationPropertyName, entityData);
				break;
			case "CombinationOrderActivityViewModel":
				retValue = (T) new CombinationOrderActivityViewModel(application, navigationPropertyName, entityData);
				break;
			case "ConfigDataBatchViewModel":
				retValue = (T) new ConfigDataBatchViewModel(application, navigationPropertyName, entityData);
				break;
			case "ConfirmationTextViewModel":
				retValue = (T) new ConfirmationTextViewModel(application, navigationPropertyName, entityData);
				break;
			case "ConfirmationViewModel":
				retValue = (T) new ConfirmationViewModel(application, navigationPropertyName, entityData);
				break;
			case "DeletedEntityViewModel":
				retValue = (T) new DeletedEntityViewModel(application, navigationPropertyName, entityData);
				break;
			case "EmployeeViewModel":
				retValue = (T) new EmployeeViewModel(application, navigationPropertyName, entityData);
				break;
			case "EnrollOrderTypeViewModel":
				retValue = (T) new EnrollOrderTypeViewModel(application, navigationPropertyName, entityData);
				break;
			case "EquipmentBatchsViewModel":
				retValue = (T) new EquipmentBatchsViewModel(application, navigationPropertyName, entityData);
				break;
			case "EquipmentViewModel":
				retValue = (T) new EquipmentViewModel(application, navigationPropertyName, entityData);
				break;
			case "InspectionCatalogCodeGroupViewModel":
				retValue = (T) new InspectionCatalogCodeGroupViewModel(application, navigationPropertyName, entityData);
				break;
			case "InspectionCatalogCodeViewModel":
				retValue = (T) new InspectionCatalogCodeViewModel(application, navigationPropertyName, entityData);
				break;
			case "InspectionCatalogViewModel":
				retValue = (T) new InspectionCatalogViewModel(application, navigationPropertyName, entityData);
				break;
			case "ListTechnicalEquipmentBatchViewModel":
				retValue = (T) new ListTechnicalEquipmentBatchViewModel(application, navigationPropertyName, entityData);
				break;
			case "ListTechnicalEquipmentViewModel":
				retValue = (T) new ListTechnicalEquipmentViewModel(application, navigationPropertyName, entityData);
				break;
			case "LocalInstallStructureViewModel":
				retValue = (T) new LocalInstallStructureViewModel(application, navigationPropertyName, entityData);
				break;
			case "LogSapViewModel":
				retValue = (T) new LogSapViewModel(application, navigationPropertyName, entityData);
				break;
			case "LongTextNotificationBatchViewModel":
				retValue = (T) new LongTextNotificationBatchViewModel(application, navigationPropertyName, entityData);
				break;
			case "LongTextNotificationViewModel":
				retValue = (T) new LongTextNotificationViewModel(application, navigationPropertyName, entityData);
				break;
			case "LongTextOrderBatchViewModel":
				retValue = (T) new LongTextOrderBatchViewModel(application, navigationPropertyName, entityData);
				break;
			case "LongTextOrderViewModel":
				retValue = (T) new LongTextOrderViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaintenanceActivityTypeViewModel":
				retValue = (T) new MaintenanceActivityTypeViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaintenanceControlParameterViewModel":
				retValue = (T) new MaintenanceControlParameterViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaintenanceNotificationBatchViewModel":
				retValue = (T) new MaintenanceNotificationBatchViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaintenanceNotificationCauseViewModel":
				retValue = (T) new MaintenanceNotificationCauseViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaintenanceNotificationItemViewModel":
				retValue = (T) new MaintenanceNotificationItemViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaintenanceNotificationViewModel":
				retValue = (T) new MaintenanceNotificationViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaintenanceOrderBatchViewModel":
				retValue = (T) new MaintenanceOrderBatchViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaintenanceOrderComponentViewModel":
				retValue = (T) new MaintenanceOrderComponentViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaintenanceOrderOperationViewModel":
				retValue = (T) new MaintenanceOrderOperationViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaintenanceOrderPriorityTextAssociationViewModel":
				retValue = (T) new MaintenanceOrderPriorityTextAssociationViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaintenanceOrderViewModel":
				retValue = (T) new MaintenanceOrderViewModel(application, navigationPropertyName, entityData);
				break;
			case "MasterDataBatchViewModel":
				retValue = (T) new MasterDataBatchViewModel(application, navigationPropertyName, entityData);
				break;
			case "MaterialDescriptionViewModel":
				retValue = (T) new MaterialDescriptionViewModel(application, navigationPropertyName, entityData);
				break;
			case "NotificationTypeTextViewModel":
				retValue = (T) new NotificationTypeTextViewModel(application, navigationPropertyName, entityData);
				break;
			case "OperationMaterialViewModel":
				retValue = (T) new OperationMaterialViewModel(application, navigationPropertyName, entityData);
				break;
			case "OrderRuleTypeDataBatchViewModel":
				retValue = (T) new OrderRuleTypeDataBatchViewModel(application, navigationPropertyName, entityData);
				break;
			case "OrderRuleTypeViewModel":
				retValue = (T) new OrderRuleTypeViewModel(application, navigationPropertyName, entityData);
				break;
			case "OrderTypeTextViewModel":
				retValue = (T) new OrderTypeTextViewModel(application, navigationPropertyName, entityData);
				break;
			case "ParametersViewModel":
				retValue = (T) new ParametersViewModel(application, navigationPropertyName, entityData);
				break;
			case "PriorityTextViewModel":
				retValue = (T) new PriorityTextViewModel(application, navigationPropertyName, entityData);
				break;
			case "UserViewModel":
				retValue = (T) new UserViewModel(application, navigationPropertyName, entityData);
				break;
			case "WorkCenterDescriptionViewModel":
				retValue = (T) new WorkCenterDescriptionViewModel(application, navigationPropertyName, entityData);
				break;
			case "WorkCenterViewModel":
				retValue = (T) new WorkCenterViewModel(application, navigationPropertyName, entityData);
				break;
		}
		return retValue;
	}
}