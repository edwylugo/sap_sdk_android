package com.company.mysapcpsdkproject.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.company.mysapcpsdkproject.service.SAPServiceManager;

import com.sap.cloud.android.odata.ipmcontainer.ipmContainer;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainerMetadata.EntitySets;

import com.sap.cloud.android.odata.ipmcontainer.ActionEvents;
import com.sap.cloud.android.odata.ipmcontainer.CatalogTypeBatchs;
import com.sap.cloud.android.odata.ipmcontainer.CatalogType;
import com.sap.cloud.android.odata.ipmcontainer.CauseOfConfirmation;
import com.sap.cloud.android.odata.ipmcontainer.CombinationOrderActivity;
import com.sap.cloud.android.odata.ipmcontainer.ConfigDataBatch;
import com.sap.cloud.android.odata.ipmcontainer.ConfirmationText;
import com.sap.cloud.android.odata.ipmcontainer.Confirmation;
import com.sap.cloud.android.odata.ipmcontainer.DeletedEntity;
import com.sap.cloud.android.odata.ipmcontainer.Employee;
import com.sap.cloud.android.odata.ipmcontainer.EnrollOrderType;
import com.sap.cloud.android.odata.ipmcontainer.EquipmentBatchs;
import com.sap.cloud.android.odata.ipmcontainer.Equipment;
import com.sap.cloud.android.odata.ipmcontainer.InspectionCatalogCodeGroup;
import com.sap.cloud.android.odata.ipmcontainer.InspectionCatalogCode;
import com.sap.cloud.android.odata.ipmcontainer.InspectionCatalog;
import com.sap.cloud.android.odata.ipmcontainer.ListTechnicalEquipmentBatch;
import com.sap.cloud.android.odata.ipmcontainer.ListTechnicalEquipment;
import com.sap.cloud.android.odata.ipmcontainer.LocalInstallStructure;
import com.sap.cloud.android.odata.ipmcontainer.LogSap;
import com.sap.cloud.android.odata.ipmcontainer.LongTextNotificationBatch;
import com.sap.cloud.android.odata.ipmcontainer.LongTextNotification;
import com.sap.cloud.android.odata.ipmcontainer.LongTextOrderBatch;
import com.sap.cloud.android.odata.ipmcontainer.LongTextOrder;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceActivityType;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceControlParameter;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceNotificationBatch;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceNotificationCause;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceNotificationItem;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceNotification;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceOrderBatch;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceOrderComponent;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceOrderOperation;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceOrderPriorityTextAssociation;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceOrder;
import com.sap.cloud.android.odata.ipmcontainer.MasterDataBatch;
import com.sap.cloud.android.odata.ipmcontainer.MaterialDescription;
import com.sap.cloud.android.odata.ipmcontainer.NotificationTypeText;
import com.sap.cloud.android.odata.ipmcontainer.OperationMaterial;
import com.sap.cloud.android.odata.ipmcontainer.OrderRuleTypeDataBatch;
import com.sap.cloud.android.odata.ipmcontainer.OrderRuleType;
import com.sap.cloud.android.odata.ipmcontainer.OrderTypeText;
import com.sap.cloud.android.odata.ipmcontainer.Parameters;
import com.sap.cloud.android.odata.ipmcontainer.PriorityText;
import com.sap.cloud.android.odata.ipmcontainer.User;
import com.sap.cloud.android.odata.ipmcontainer.WorkCenterDescription;
import com.sap.cloud.android.odata.ipmcontainer.WorkCenter;

import com.sap.cloud.mobile.odata.EntitySet;
import com.sap.cloud.mobile.odata.Property;

import java.util.WeakHashMap;

/*
 * Repository factory to construct repository for an entity set
 */
public class RepositoryFactory {

    /*
     * Cache all repositories created to avoid reconstruction and keeping the entities of entity set
     * maintained by each repository in memory. Use a weak hash map to allow recovery in low memory
     * conditions
     */
    private WeakHashMap<String, Repository> repositories;

    /*
     * Service manager to interact with OData service
     */
    private SAPServiceManager sapServiceManager;

    /**
     * Construct a RepositoryFactory instance. There should only be one repository factory and used
     * throughout the life of the application to avoid caching entities multiple times.
     * @param sapServiceManager - Service manager for interaction with OData service
     */
    public RepositoryFactory(SAPServiceManager sapServiceManager) {
        repositories = new WeakHashMap<>();
        this.sapServiceManager = sapServiceManager;
    }

    /**
     * Construct or return an existing repository for the specified entity set
     * @param entitySet - entity set for which the repository is to be returned
     * @param orderByProperty - if specified, collection will be sorted ascending with this property
     * @return a repository for the entity set
     */
    public Repository getRepository(@NonNull EntitySet entitySet, @Nullable Property orderByProperty) {
        ipmContainer ipmContainer = sapServiceManager.getipmContainer();
        String key = entitySet.getLocalName();
        Repository repository = repositories.get(key);
        if (repository == null) {
            if (key.equals(EntitySets.actionEventss.getLocalName())) {
                repository = new Repository<ActionEvents>(ipmContainer, EntitySets.actionEventss, orderByProperty);
            } else if (key.equals(EntitySets.catalogTypeBatchss.getLocalName())) {
                repository = new Repository<CatalogTypeBatchs>(ipmContainer, EntitySets.catalogTypeBatchss, orderByProperty);
            } else if (key.equals(EntitySets.catalogTypes.getLocalName())) {
                repository = new Repository<CatalogType>(ipmContainer, EntitySets.catalogTypes, orderByProperty);
            } else if (key.equals(EntitySets.causeOfConfirmations.getLocalName())) {
                repository = new Repository<CauseOfConfirmation>(ipmContainer, EntitySets.causeOfConfirmations, orderByProperty);
            } else if (key.equals(EntitySets.combinationOrderActivitys.getLocalName())) {
                repository = new Repository<CombinationOrderActivity>(ipmContainer, EntitySets.combinationOrderActivitys, orderByProperty);
            } else if (key.equals(EntitySets.configDataBatchs.getLocalName())) {
                repository = new Repository<ConfigDataBatch>(ipmContainer, EntitySets.configDataBatchs, orderByProperty);
            } else if (key.equals(EntitySets.confirmationTexts.getLocalName())) {
                repository = new Repository<ConfirmationText>(ipmContainer, EntitySets.confirmationTexts, orderByProperty);
            } else if (key.equals(EntitySets.confirmations.getLocalName())) {
                repository = new Repository<Confirmation>(ipmContainer, EntitySets.confirmations, orderByProperty);
            } else if (key.equals(EntitySets.deletedEntitys.getLocalName())) {
                repository = new Repository<DeletedEntity>(ipmContainer, EntitySets.deletedEntitys, orderByProperty);
            } else if (key.equals(EntitySets.employees.getLocalName())) {
                repository = new Repository<Employee>(ipmContainer, EntitySets.employees, orderByProperty);
            } else if (key.equals(EntitySets.enrollOrderTypes.getLocalName())) {
                repository = new Repository<EnrollOrderType>(ipmContainer, EntitySets.enrollOrderTypes, orderByProperty);
            } else if (key.equals(EntitySets.equipmentBatchss.getLocalName())) {
                repository = new Repository<EquipmentBatchs>(ipmContainer, EntitySets.equipmentBatchss, orderByProperty);
            } else if (key.equals(EntitySets.equipments.getLocalName())) {
                repository = new Repository<Equipment>(ipmContainer, EntitySets.equipments, orderByProperty);
            } else if (key.equals(EntitySets.inspectionCatalogCodeGroups.getLocalName())) {
                repository = new Repository<InspectionCatalogCodeGroup>(ipmContainer, EntitySets.inspectionCatalogCodeGroups, orderByProperty);
            } else if (key.equals(EntitySets.inspectionCatalogCodes.getLocalName())) {
                repository = new Repository<InspectionCatalogCode>(ipmContainer, EntitySets.inspectionCatalogCodes, orderByProperty);
            } else if (key.equals(EntitySets.inspectionCatalogs.getLocalName())) {
                repository = new Repository<InspectionCatalog>(ipmContainer, EntitySets.inspectionCatalogs, orderByProperty);
            } else if (key.equals(EntitySets.listTechnicalEquipmentBatchs.getLocalName())) {
                repository = new Repository<ListTechnicalEquipmentBatch>(ipmContainer, EntitySets.listTechnicalEquipmentBatchs, orderByProperty);
            } else if (key.equals(EntitySets.listTechnicalEquipments.getLocalName())) {
                repository = new Repository<ListTechnicalEquipment>(ipmContainer, EntitySets.listTechnicalEquipments, orderByProperty);
            } else if (key.equals(EntitySets.localInstallStructures.getLocalName())) {
                repository = new Repository<LocalInstallStructure>(ipmContainer, EntitySets.localInstallStructures, orderByProperty);
            } else if (key.equals(EntitySets.logSaps.getLocalName())) {
                repository = new Repository<LogSap>(ipmContainer, EntitySets.logSaps, orderByProperty);
            } else if (key.equals(EntitySets.longTextNotificationBatchs.getLocalName())) {
                repository = new Repository<LongTextNotificationBatch>(ipmContainer, EntitySets.longTextNotificationBatchs, orderByProperty);
            } else if (key.equals(EntitySets.longTextNotifications.getLocalName())) {
                repository = new Repository<LongTextNotification>(ipmContainer, EntitySets.longTextNotifications, orderByProperty);
            } else if (key.equals(EntitySets.longTextOrderBatchs.getLocalName())) {
                repository = new Repository<LongTextOrderBatch>(ipmContainer, EntitySets.longTextOrderBatchs, orderByProperty);
            } else if (key.equals(EntitySets.longTextOrders.getLocalName())) {
                repository = new Repository<LongTextOrder>(ipmContainer, EntitySets.longTextOrders, orderByProperty);
            } else if (key.equals(EntitySets.maintenanceActivityTypes.getLocalName())) {
                repository = new Repository<MaintenanceActivityType>(ipmContainer, EntitySets.maintenanceActivityTypes, orderByProperty);
            } else if (key.equals(EntitySets.maintenanceControlParameters.getLocalName())) {
                repository = new Repository<MaintenanceControlParameter>(ipmContainer, EntitySets.maintenanceControlParameters, orderByProperty);
            } else if (key.equals(EntitySets.maintenanceNotificationBatchs.getLocalName())) {
                repository = new Repository<MaintenanceNotificationBatch>(ipmContainer, EntitySets.maintenanceNotificationBatchs, orderByProperty);
            } else if (key.equals(EntitySets.maintenanceNotificationCauses.getLocalName())) {
                repository = new Repository<MaintenanceNotificationCause>(ipmContainer, EntitySets.maintenanceNotificationCauses, orderByProperty);
            } else if (key.equals(EntitySets.maintenanceNotificationItems.getLocalName())) {
                repository = new Repository<MaintenanceNotificationItem>(ipmContainer, EntitySets.maintenanceNotificationItems, orderByProperty);
            } else if (key.equals(EntitySets.maintenanceNotifications.getLocalName())) {
                repository = new Repository<MaintenanceNotification>(ipmContainer, EntitySets.maintenanceNotifications, orderByProperty);
            } else if (key.equals(EntitySets.maintenanceOrderBatchs.getLocalName())) {
                repository = new Repository<MaintenanceOrderBatch>(ipmContainer, EntitySets.maintenanceOrderBatchs, orderByProperty);
            } else if (key.equals(EntitySets.maintenanceOrderComponents.getLocalName())) {
                repository = new Repository<MaintenanceOrderComponent>(ipmContainer, EntitySets.maintenanceOrderComponents, orderByProperty);
            } else if (key.equals(EntitySets.maintenanceOrderOperations.getLocalName())) {
                repository = new Repository<MaintenanceOrderOperation>(ipmContainer, EntitySets.maintenanceOrderOperations, orderByProperty);
            } else if (key.equals(EntitySets.maintenanceOrderPriorityTextAssociations.getLocalName())) {
                repository = new Repository<MaintenanceOrderPriorityTextAssociation>(ipmContainer, EntitySets.maintenanceOrderPriorityTextAssociations, orderByProperty);
            } else if (key.equals(EntitySets.maintenanceOrders.getLocalName())) {
                repository = new Repository<MaintenanceOrder>(ipmContainer, EntitySets.maintenanceOrders, orderByProperty);
            } else if (key.equals(EntitySets.masterDataBatchs.getLocalName())) {
                repository = new Repository<MasterDataBatch>(ipmContainer, EntitySets.masterDataBatchs, orderByProperty);
            } else if (key.equals(EntitySets.materialDescriptions.getLocalName())) {
                repository = new Repository<MaterialDescription>(ipmContainer, EntitySets.materialDescriptions, orderByProperty);
            } else if (key.equals(EntitySets.notificationTypeTexts.getLocalName())) {
                repository = new Repository<NotificationTypeText>(ipmContainer, EntitySets.notificationTypeTexts, orderByProperty);
            } else if (key.equals(EntitySets.operationMaterials.getLocalName())) {
                repository = new Repository<OperationMaterial>(ipmContainer, EntitySets.operationMaterials, orderByProperty);
            } else if (key.equals(EntitySets.orderRuleTypeDataBatchs.getLocalName())) {
                repository = new Repository<OrderRuleTypeDataBatch>(ipmContainer, EntitySets.orderRuleTypeDataBatchs, orderByProperty);
            } else if (key.equals(EntitySets.orderRuleTypes.getLocalName())) {
                repository = new Repository<OrderRuleType>(ipmContainer, EntitySets.orderRuleTypes, orderByProperty);
            } else if (key.equals(EntitySets.orderTypeTexts.getLocalName())) {
                repository = new Repository<OrderTypeText>(ipmContainer, EntitySets.orderTypeTexts, orderByProperty);
            } else if (key.equals(EntitySets.parameterss.getLocalName())) {
                repository = new Repository<Parameters>(ipmContainer, EntitySets.parameterss, orderByProperty);
            } else if (key.equals(EntitySets.priorityTexts.getLocalName())) {
                repository = new Repository<PriorityText>(ipmContainer, EntitySets.priorityTexts, orderByProperty);
            } else if (key.equals(EntitySets.users.getLocalName())) {
                repository = new Repository<User>(ipmContainer, EntitySets.users, orderByProperty);
            } else if (key.equals(EntitySets.workCenterDescriptions.getLocalName())) {
                repository = new Repository<WorkCenterDescription>(ipmContainer, EntitySets.workCenterDescriptions, orderByProperty);
            } else if (key.equals(EntitySets.workCenters.getLocalName())) {
                repository = new Repository<WorkCenter>(ipmContainer, EntitySets.workCenters, orderByProperty);
            } else {
                throw new AssertionError("Fatal error, entity set[" + key + "] missing in generated code");
            }
            repositories.put(key, repository);
        }
        return repository;
    }

    /**
     * Get rid of all cached repositories
     */
    public void reset() {
        repositories.clear();
    }
 }
