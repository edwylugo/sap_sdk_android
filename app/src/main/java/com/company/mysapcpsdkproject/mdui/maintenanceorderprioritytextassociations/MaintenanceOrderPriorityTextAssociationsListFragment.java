package com.company.mysapcpsdkproject.mdui.maintenanceorderprioritytextassociations;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import com.company.mysapcpsdkproject.R;
import com.company.mysapcpsdkproject.app.ErrorHandler;
import com.company.mysapcpsdkproject.app.ErrorMessage;
import com.company.mysapcpsdkproject.app.SAPWizardApplication;
import com.company.mysapcpsdkproject.mdui.BundleKeys;
import com.company.mysapcpsdkproject.mdui.EntitySetListActivity;
import com.company.mysapcpsdkproject.mdui.EntitySetListActivity.EntitySetName;
import com.company.mysapcpsdkproject.mdui.InterfacedFragment;
import com.company.mysapcpsdkproject.mdui.UIConstants;
import com.company.mysapcpsdkproject.mdui.EntityKeyUtil;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import com.company.mysapcpsdkproject.repository.OperationResult;
import com.company.mysapcpsdkproject.service.SAPServiceManager;
import com.company.mysapcpsdkproject.viewmodel.EntityViewModelFactory;
import com.company.mysapcpsdkproject.viewmodel.maintenanceorderprioritytextassociation.MaintenanceOrderPriorityTextAssociationViewModel;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainerMetadata.EntitySets;
import com.sap.cloud.android.odata.ipmcontainer.MaintenanceOrderPriorityTextAssociation;
import com.sap.cloud.mobile.fiori.object.ObjectCell;
import com.sap.cloud.mobile.odata.DataValue;
import com.sap.cloud.mobile.odata.EntityValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceOrderPriorityTextAssociationsListFragment extends InterfacedFragment<MaintenanceOrderPriorityTextAssociation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceOrderPriorityTextAssociationsActivity.class);

    /**
     * Error handler to display message should error occurs
     */
    private ErrorHandler errorHandler;

    /**
     * List adapter to be used with RecyclerView containing all instances of maintenanceOrderPriorityTextAssociations
     */
    private MaintenanceOrderPriorityTextAssociationListAdapter adapter;

    private SwipeRefreshLayout refreshLayout;

    /**
     * View model of the entity type
     */
    private MaintenanceOrderPriorityTextAssociationViewModel viewModel;

    private ActionMode actionMode;
    private Boolean isInActionMode = false;
    private List<Integer> selectedItems = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshLayout.setRefreshing(true);
                refreshListData();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTitle = getString(EntitySetListActivity.EntitySetName.MaintenanceOrderPriorityTextAssociations.getTitleId());
        menu = R.menu.itemlist_menu;
        setHasOptionsMenu(true);
        if( savedInstanceState != null ) {
            isInActionMode = savedInstanceState.getBoolean("ActionMode");
        }

        errorHandler = ((SAPWizardApplication) currentActivity.getApplication()).getErrorHandler();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View objectHeader = currentActivity.findViewById(R.id.objectHeader);
        if( objectHeader != null) {
            objectHeader.setVisibility(View.GONE);
        }
        return inflater.inflate(R.layout.fragment_entityitem_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(this.menu, menu);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        currentActivity.setTitle(activityTitle);
        RecyclerView recyclerView = currentActivity.findViewById(R.id.item_list);
        if (recyclerView == null) throw new AssertionError();
        this.adapter = new MaintenanceOrderPriorityTextAssociationListAdapter(currentActivity);
        recyclerView.setAdapter(adapter);

        setupRefreshLayout();
        refreshLayout.setRefreshing(true);

        navigationPropertyName = currentActivity.getIntent().getStringExtra("navigation");
        parentEntityData = currentActivity.getIntent().getParcelableExtra("parent");

        FloatingActionButton floatButton = currentActivity.findViewById(R.id.fab);
        if (floatButton != null) {
            if (navigationPropertyName != null && parentEntityData != null) {
                floatButton.hide();
            } else {
                floatButton.setOnClickListener((v) -> {
                    listener.onFragmentStateChange(UIConstants.EVENT_CREATE_NEW_ITEM, null);
                });
            }
        }

        prepareViewModel();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("ActionMode", isInActionMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListData();
    }

    /** Initializes the view model and add observers on it */
    private void prepareViewModel() {
        if( navigationPropertyName != null && parentEntityData != null ) {
            viewModel = ViewModelProviders.of(currentActivity, new EntityViewModelFactory(currentActivity.getApplication(), navigationPropertyName, parentEntityData))
                .get(MaintenanceOrderPriorityTextAssociationViewModel.class);
        } else {
            viewModel = ViewModelProviders.of(currentActivity).get(MaintenanceOrderPriorityTextAssociationViewModel.class);
            viewModel.initialRead();
        }

        viewModel.getObservableItems().observe(this, maintenanceOrderPriorityTextAssociations -> {
            if (maintenanceOrderPriorityTextAssociations != null) {
                adapter.setItems(maintenanceOrderPriorityTextAssociations);

                MaintenanceOrderPriorityTextAssociation item = containsItem(maintenanceOrderPriorityTextAssociations, viewModel.getSelectedEntity().getValue());
                if (item == null) {
                    item = maintenanceOrderPriorityTextAssociations.isEmpty() ? null : maintenanceOrderPriorityTextAssociations.get(0);
                }

                if (item == null) {
                    hideDetailFragment();
                } else {
                    viewModel.setInFocusId(adapter.getItemIdForMaintenanceOrderPriorityTextAssociation(item));
                    if (currentActivity.getResources().getBoolean(R.bool.two_pane)) {
                        viewModel.setSelectedEntity(item);
                        if (!isInActionMode && !((MaintenanceOrderPriorityTextAssociationsActivity) currentActivity).isNavigationDisabled) {
                            listener.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            refreshLayout.setRefreshing(false);
        });

        viewModel.getReadResult().observe(this, state -> {
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }
        });

        viewModel.getDeleteResult().observe(this, result -> {
            onDeleteComplete(result);
        });
    }

    /** Searches 'item' in the refreshed list, if found, returns the one in list */
    private MaintenanceOrderPriorityTextAssociation containsItem(List<MaintenanceOrderPriorityTextAssociation> items, MaintenanceOrderPriorityTextAssociation item) {
        MaintenanceOrderPriorityTextAssociation found = null;
        if( item != null ) {
            for( MaintenanceOrderPriorityTextAssociation entity: items ) {
                if( adapter.getItemIdForMaintenanceOrderPriorityTextAssociation(entity) == adapter.getItemIdForMaintenanceOrderPriorityTextAssociation(item)) {
                    found = entity;
                    break;
                }
            }
        }
        return found;
    }

    /**
     * Hides the detail fragment.
     */
    private void hideDetailFragment() {
        Fragment detailFragment = currentActivity.getSupportFragmentManager().findFragmentByTag(UIConstants.DETAIL_FRAGMENT_TAG);
        if( detailFragment != null ) {
            currentActivity.getSupportFragmentManager().beginTransaction()
                .remove(detailFragment).commit();
        }
        if( secondaryToolbar != null ) {
            secondaryToolbar.getMenu().clear();
            secondaryToolbar.setTitle("");
        }
        View objectHeader = currentActivity.findViewById(R.id.objectHeader);
        if( objectHeader != null) {
            objectHeader.setVisibility(View.GONE);
        }
    }

    /** Callback function for delete operation */
    private void onDeleteComplete(@NonNull OperationResult<MaintenanceOrderPriorityTextAssociation> result) {
        if( progressBar != null ) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        viewModel.removeAllSelected();
        if (actionMode != null) {
            actionMode.finish();
            isInActionMode = false;
        }

        if (result.getError() != null) {
            handleDeleteError(result.getError());
        } else {
            refreshListData();
        }
    }

    /** Refreshes the list data */
    void refreshListData() {
        if (navigationPropertyName != null && parentEntityData != null) {
            viewModel.refresh((EntityValue) parentEntityData, navigationPropertyName);
        } else {
            viewModel.refresh();
        }
        adapter.notifyDataSetChanged();
    }

    /** Sets the selected item id into view model */
    private MaintenanceOrderPriorityTextAssociation setItemIdSelected(int itemId) {
        LiveData<List<MaintenanceOrderPriorityTextAssociation>> liveData = viewModel.getObservableItems();
        List<MaintenanceOrderPriorityTextAssociation> maintenanceOrderPriorityTextAssociations = liveData.getValue();
        if (maintenanceOrderPriorityTextAssociations != null && maintenanceOrderPriorityTextAssociations.size() > 0) {
            viewModel.setInFocusId(adapter.getItemIdForMaintenanceOrderPriorityTextAssociation(maintenanceOrderPriorityTextAssociations.get(itemId)));
            return maintenanceOrderPriorityTextAssociations.get(itemId);
        }
        return null;
    }

    /** Sets up the refresh layout */
    private void setupRefreshLayout() {
        refreshLayout = currentActivity.findViewById(R.id.swiperefresh);
        refreshLayout.setColorSchemeColors(UIConstants.FIORI_STANDARD_THEME_GLOBAL_DARK_BASE);
        refreshLayout.setProgressBackgroundColorSchemeColor(UIConstants.FIORI_STANDARD_THEME_BACKGROUND);
        refreshLayout.setOnRefreshListener(this::refreshListData);
    }

    /** Callback function to handle deletion error */
    private void handleDeleteError(@NonNull Exception exception) {
        ErrorMessage errorMessage;
        errorMessage = new ErrorMessage(getResources().getString(R.string.delete_failed),
                getResources().getString(R.string.delete_failed_detail), exception, false);
        errorHandler.sendErrorMessage(errorMessage);
        refreshLayout.setRefreshing(false);
    }

    /**
     * Represents the action mode of the list.
     */
    public class MaintenanceOrderPriorityTextAssociationsListActionMode implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            isInActionMode = true;
            FloatingActionButton fab = currentActivity.findViewById(R.id.fab);
            if (fab != null) {
                fab.hide();
            }
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.itemlist_view_options, menu);
            hideDetailFragment();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.update_item:
                    MaintenanceOrderPriorityTextAssociation maintenanceOrderPriorityTextAssociationEntity = viewModel.getSelected(0);
                    if (viewModel.numberOfSelected() == 1 && maintenanceOrderPriorityTextAssociationEntity != null) {
                        isInActionMode = false;
                        actionMode.finish();
                        viewModel.setSelectedEntity(maintenanceOrderPriorityTextAssociationEntity);
                        if( currentActivity.getResources().getBoolean(R.bool.two_pane)) {
                            listener.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, maintenanceOrderPriorityTextAssociationEntity);
                        }
                        listener.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, maintenanceOrderPriorityTextAssociationEntity);
                    }
                    return true;
                case R.id.delete_item:
                    listener.onFragmentStateChange(UIConstants.EVENT_ASK_DELETE_CONFIRMATION, null);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            isInActionMode = false;
            if (!(navigationPropertyName != null && parentEntityData != null)) {
                FloatingActionButton fab = currentActivity.findViewById(R.id.fab);
                if (fab != null) {
                    fab.show();
                }
            }
            selectedItems.clear();
            viewModel.removeAllSelected();
            refreshListData();
        }
    }

    /**
     * List adapter to be used with RecyclerView. It contains the set of maintenanceOrderPriorityTextAssociations.
     */
    public class MaintenanceOrderPriorityTextAssociationListAdapter extends RecyclerView.Adapter<MaintenanceOrderPriorityTextAssociationListAdapter.ViewHolder> {

        private Context context;

        /** Entire list of MaintenanceOrderPriorityTextAssociation collection */
        private List<MaintenanceOrderPriorityTextAssociation> maintenanceOrderPriorityTextAssociations;

        /** RecyclerView this adapter is associate with */
        private RecyclerView recyclerView;

        /** Flag to indicate whether we have checked retained selected maintenanceOrderPriorityTextAssociations */
        private boolean checkForSelectedOnCreate = false;

        public MaintenanceOrderPriorityTextAssociationListAdapter(Context context) {
            this.context = context;
            this.recyclerView = currentActivity.findViewById(R.id.item_list);
            if (this.recyclerView == null) throw new AssertionError();
            setHasStableIds(true);
        }

        /**
         * Use DiffUtil to calculate the difference and dispatch them to the adapter
         * Note: Please use background thread for calculation if the list is large to avoid blocking main thread
         */
        @WorkerThread
        public void setItems(@NonNull List<MaintenanceOrderPriorityTextAssociation> currentMaintenanceOrderPriorityTextAssociations) {
            if (maintenanceOrderPriorityTextAssociations == null) {
                maintenanceOrderPriorityTextAssociations = new ArrayList<>(currentMaintenanceOrderPriorityTextAssociations);
                notifyItemRangeInserted(0, currentMaintenanceOrderPriorityTextAssociations.size());
            } else {
                DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    @Override
                    public int getOldListSize() {
                        return maintenanceOrderPriorityTextAssociations.size();
                    }

                    @Override
                    public int getNewListSize() {
                        return currentMaintenanceOrderPriorityTextAssociations.size();
                    }

                    @Override
                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        return maintenanceOrderPriorityTextAssociations.get(oldItemPosition).getReadLink().equals(
                                currentMaintenanceOrderPriorityTextAssociations.get(newItemPosition).getReadLink());
                    }

                    @Override
                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        MaintenanceOrderPriorityTextAssociation maintenanceOrderPriorityTextAssociationEntity = maintenanceOrderPriorityTextAssociations.get(oldItemPosition);
                        return !maintenanceOrderPriorityTextAssociationEntity.isUpdated() && currentMaintenanceOrderPriorityTextAssociations.get(newItemPosition).equals(maintenanceOrderPriorityTextAssociationEntity);
                    }

                    @Nullable
                    @Override
                    public Object getChangePayload(final int oldItemPosition, final int newItemPosition) {
                        return super.getChangePayload(oldItemPosition, newItemPosition);
                    }
                });
                maintenanceOrderPriorityTextAssociations.clear();
                maintenanceOrderPriorityTextAssociations.addAll(currentMaintenanceOrderPriorityTextAssociations);
                result.dispatchUpdatesTo(this);
            }
        }

        @Override
        public final long getItemId(int position) {
            return getItemIdForMaintenanceOrderPriorityTextAssociation(maintenanceOrderPriorityTextAssociations.get(position));
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_entityitem_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            checkForRetainedSelection();

            final MaintenanceOrderPriorityTextAssociation maintenanceOrderPriorityTextAssociationEntity = maintenanceOrderPriorityTextAssociations.get(holder.getAdapterPosition());
            DataValue dataValue = maintenanceOrderPriorityTextAssociationEntity.getDataValue(MaintenanceOrderPriorityTextAssociation.priorityType);
            if (dataValue != null) {
                holder.masterPropertyValue = dataValue.toString();
            }
            populateObjectCell(holder, maintenanceOrderPriorityTextAssociationEntity);

            boolean isActive = getItemIdForMaintenanceOrderPriorityTextAssociation(maintenanceOrderPriorityTextAssociationEntity) == viewModel.getInFocusId();
            if (isActive) {
                setItemIdSelected(holder.getAdapterPosition());
            }
            boolean isMaintenanceOrderPriorityTextAssociationSelected = viewModel.selectedContains(maintenanceOrderPriorityTextAssociationEntity);
            setViewBackground(holder.view, isMaintenanceOrderPriorityTextAssociationSelected, isActive);

            holder.view.setOnLongClickListener(new onActionModeStartClickListener(holder));
            setOnClickListener(holder, maintenanceOrderPriorityTextAssociationEntity);

            setOnCheckedChangeListener(holder, maintenanceOrderPriorityTextAssociationEntity);
            holder.setSelected(isMaintenanceOrderPriorityTextAssociationSelected);
            setDetailImage(holder, maintenanceOrderPriorityTextAssociationEntity);
        }

        /**
         * Check to see if there are an retained selected maintenanceOrderPriorityTextAssociationEntity on start.
         * This situation occurs when a rotation with selected maintenanceOrderPriorityTextAssociations is triggered by user.
         */
        private void checkForRetainedSelection() {
            if (!checkForSelectedOnCreate) {
                checkForSelectedOnCreate = true;
                if (viewModel.numberOfSelected() > 0) {
                    manageActionModeOnCheckedTransition();
                }
            }
        }

        /**
         * If there are selected maintenanceOrderPriorityTextAssociations via long press, clear them as click and long press are mutually exclusive
         * In addition, since we are clearing all selected maintenanceOrderPriorityTextAssociations via long press, finish the action mode.
         */
        private void resetSelected() {
            if (viewModel.numberOfSelected() > 0) {
                viewModel.removeAllSelected();
                if (actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }
            }
        }

        /**
         * Attempt to locate previously clicked view and reset its background
         * Reset view model's inFocusId
         */
        private void resetPreviouslyClicked() {
            long inFocusId = viewModel.getInFocusId();
            ViewHolder viewHolder = (ViewHolder) recyclerView.findViewHolderForItemId(inFocusId);
            if (viewHolder != null) {
                setViewBackground(viewHolder.view, viewHolder.isSelected, false);
            } else {
                viewModel.refresh();
            }
        }

        private void processClickAction(@NonNull View view, @NonNull MaintenanceOrderPriorityTextAssociation maintenanceOrderPriorityTextAssociationEntity) {
            resetPreviouslyClicked();
            setViewBackground(view, false, true);
            viewModel.setInFocusId(getItemIdForMaintenanceOrderPriorityTextAssociation(maintenanceOrderPriorityTextAssociationEntity));
        }

        /**
         * Set ViewHolder's view onClickListener
         *
         * @param holder
         * @param maintenanceOrderPriorityTextAssociationEntity associated with this ViewHolder
         */
        private void setOnClickListener(@NonNull ViewHolder holder, @NonNull MaintenanceOrderPriorityTextAssociation maintenanceOrderPriorityTextAssociationEntity) {
            holder.view.setOnClickListener(view -> {
                boolean isNavigationDisabled = ((MaintenanceOrderPriorityTextAssociationsActivity) currentActivity).isNavigationDisabled;
                if(isNavigationDisabled) {
                    Toast.makeText(currentActivity, "Please save your changes first...", Toast.LENGTH_LONG).show();
                } else {
                    resetSelected();
                    resetPreviouslyClicked();
                    processClickAction(view, maintenanceOrderPriorityTextAssociationEntity);
                    viewModel.setSelectedEntity(maintenanceOrderPriorityTextAssociationEntity);
                    listener.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, maintenanceOrderPriorityTextAssociationEntity);
                }
            });
        }

        /**
         * Represents the listener to start the action mode
         */
        public class onActionModeStartClickListener implements View.OnClickListener, View.OnLongClickListener {

            ViewHolder holder;

            public onActionModeStartClickListener(@NonNull ViewHolder viewHolder) {
                this.holder = viewHolder;
            }

            @Override
            public void onClick(View view) {
                onAnyKindOfClick();
            }

            @Override
            public boolean onLongClick(View view) {
                return onAnyKindOfClick();
            }

            /** callback function for both normal and long click on an entity */
            private boolean onAnyKindOfClick() {
                boolean isNavigationDisabled = ((MaintenanceOrderPriorityTextAssociationsActivity) currentActivity).isNavigationDisabled;
                if( isNavigationDisabled ) {
                    Toast.makeText(currentActivity, "Please save your changes first...", Toast.LENGTH_LONG).show();
                } else {
                    if (!isInActionMode) {
                        actionMode = ((AppCompatActivity) currentActivity).startSupportActionMode(new MaintenanceOrderPriorityTextAssociationsListActionMode());
                        adapter.notifyDataSetChanged();
                    }
                    holder.setSelected(!holder.isSelected);
                }
                return true;
            }
        }

        /** sets the detail image to the given <code>viewHolder</code> */
        private void setDetailImage(@NonNull ViewHolder viewHolder, @NonNull MaintenanceOrderPriorityTextAssociation maintenanceOrderPriorityTextAssociationEntity) {
            if (isInActionMode) {
                int drawable;
                if (viewHolder.isSelected) {
                    drawable = R.drawable.ic_check_circle_black_24dp;
                } else {
                    drawable = R.drawable.ic_uncheck_circle_black_24dp;
                }
                viewHolder.objectCell.prepareDetailImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                viewHolder.objectCell.setDetailImage(drawable);
            } else {
                viewModel.downloadMedia(maintenanceOrderPriorityTextAssociationEntity, media -> {
                    viewHolder.objectCell.prepareDetailImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                    Drawable image = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(media, 0, media.length));
                    viewHolder.objectCell.setDetailImage(image);
                }, error -> {
                    if (maintenanceOrderPriorityTextAssociationEntity.getDataValue(MaintenanceOrderPriorityTextAssociation.priorityType) != null && !maintenanceOrderPriorityTextAssociationEntity.getDataValue(MaintenanceOrderPriorityTextAssociation.priorityType).toString().isEmpty()) {
                        viewHolder.objectCell.setDetailImageCharacter(maintenanceOrderPriorityTextAssociationEntity.getDataValue(MaintenanceOrderPriorityTextAssociation.priorityType).toString().substring(0, 1));
                    } else {
                        viewHolder.objectCell.setDetailImageCharacter("?");
                    }
                });
            }
        }

        /**
         * Set ViewHolder's CheckBox onCheckedChangeListener
         *
         * @param holder
         * @param maintenanceOrderPriorityTextAssociationEntity associated with this ViewHolder
         */
        private void setOnCheckedChangeListener(@NonNull ViewHolder holder, @NonNull MaintenanceOrderPriorityTextAssociation maintenanceOrderPriorityTextAssociationEntity) {
            holder.checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (checked) {
                    viewModel.addSelected(maintenanceOrderPriorityTextAssociationEntity);
                    manageActionModeOnCheckedTransition();
                    resetPreviouslyClicked();
                } else {
                    viewModel.removeSelected(maintenanceOrderPriorityTextAssociationEntity);
                    manageActionModeOnUncheckedTransition();
                }
                setViewBackground(holder.view, viewModel.selectedContains(maintenanceOrderPriorityTextAssociationEntity), false);
                setDetailImage(holder, maintenanceOrderPriorityTextAssociationEntity);
            });
        }

        /*
         * Start Action Mode if it has not been started
         * This is only called when long press action results in a selection. Hence action mode may
         * not have been started. Along with starting action mode, title will be set.
         * If this is an additional selection, adjust title appropriately.
         */
        private void manageActionModeOnCheckedTransition() {
            if (actionMode == null) {
                actionMode = ((AppCompatActivity) currentActivity).startSupportActionMode(new MaintenanceOrderPriorityTextAssociationsListActionMode());
            }
            if (viewModel.numberOfSelected() > 1) {
                actionMode.getMenu().findItem(R.id.update_item).setVisible(false);
            }
            actionMode.setTitle(String.valueOf(viewModel.numberOfSelected()));
        }

        /*
         * This is called when one of the selected maintenanceOrderPriorityTextAssociations has been de-selected
         * On this event, we will determine if update action needs to be made visible or
         * action mode should be terminated (no more selected)
         */
        private void manageActionModeOnUncheckedTransition() {
            switch (viewModel.numberOfSelected()) {
                case 1:
                    actionMode.getMenu().findItem(R.id.update_item).setVisible(true);
                    break;

                case 0:
                    if (actionMode != null) {
                        actionMode.finish();
                        actionMode = null;
                    }
                    return;

                default:
            }
            actionMode.setTitle(String.valueOf(viewModel.numberOfSelected()));
        }

        private void populateObjectCell(@NonNull ViewHolder viewHolder, @NonNull MaintenanceOrderPriorityTextAssociation maintenanceOrderPriorityTextAssociationEntity) {

            DataValue dataValue = maintenanceOrderPriorityTextAssociationEntity.getDataValue(MaintenanceOrderPriorityTextAssociation.priorityType);
            String masterPropertyValue = null;
            if (dataValue != null) {
                masterPropertyValue = dataValue.toString();
            }
            viewHolder.objectCell.setHeadline(masterPropertyValue);
            viewHolder.objectCell.setDetailImage(null);
            setDetailImage(viewHolder, maintenanceOrderPriorityTextAssociationEntity);

            viewHolder.objectCell.setSubheadline("Subheadline goes here");
            viewHolder.objectCell.setFootnote("Footnote goes here");
            if (maintenanceOrderPriorityTextAssociationEntity.getInErrorState()) {
                viewHolder.objectCell.setIcon(R.drawable.ic_error_state, 0, R.string.error_state);
            }
            else if (maintenanceOrderPriorityTextAssociationEntity.isUpdated()) {
                viewHolder.objectCell.setIcon(R.drawable.ic_updated_state, 0, R.string.updated_state);
            }
            else if (maintenanceOrderPriorityTextAssociationEntity.isLocal()) {
                viewHolder.objectCell.setIcon(R.drawable.ic_local_state, 0, R.string.local_state);
            }
            else {
                viewHolder.objectCell.setIcon(R.drawable.ic_download_state, 0, R.string.download_state);
            }
            viewHolder.objectCell.setIcon(R.drawable.default_dot, 1, R.string.attachment_item_content_desc);
            viewHolder.objectCell.setIcon("!", 2);
        }

        /**
         * Set background of view to indicate maintenanceOrderPriorityTextAssociationEntity selection status
         * Selected and Active are mutually exclusive. Only one can be true
         *
         * @param view
         * @param isMaintenanceOrderPriorityTextAssociationSelected - true if maintenanceOrderPriorityTextAssociationEntity is selected via long press action
         * @param isActive               - true if maintenanceOrderPriorityTextAssociationEntity is selected via click action
         */
        private void setViewBackground(@NonNull View view, boolean isMaintenanceOrderPriorityTextAssociationSelected, boolean isActive) {
            boolean isMasterDetailView = currentActivity.getResources().getBoolean(R.bool.two_pane);
            if (isMaintenanceOrderPriorityTextAssociationSelected) {
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.list_item_selected));
            } else if (isActive && isMasterDetailView && !isInActionMode) {
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.list_item_active));
            } else {
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.list_item_default));
            }
        }

        @Override
        public int getItemCount() {
            if (maintenanceOrderPriorityTextAssociations == null) {
                return 0;
            } else {
                return maintenanceOrderPriorityTextAssociations.size();
            }
        }

        /**
         * Computes a stable ID for each MaintenanceOrderPriorityTextAssociation object for use to locate the ViewHolder
         *
         * @param maintenanceOrderPriorityTextAssociationEntity
         * @return an ID based on the primary key of MaintenanceOrderPriorityTextAssociation
         */
        private long getItemIdForMaintenanceOrderPriorityTextAssociation(MaintenanceOrderPriorityTextAssociation maintenanceOrderPriorityTextAssociationEntity) {
            if(maintenanceOrderPriorityTextAssociationEntity.getReadLink() == null) return 0L;
            else return maintenanceOrderPriorityTextAssociationEntity.getReadLink().hashCode();
        }

        /**
         * ViewHolder for RecyclerView.
         * Each view has a Fiori ObjectCell and a checkbox (used by long press)
         */
        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View view;

            public boolean isSelected;

            public String masterPropertyValue;

            /** Fiori ObjectCell to display maintenanceOrderPriorityTextAssociationEntity in list */
            public final ObjectCell objectCell;

            /** Checkbox for long press selection */
            public final CheckBox checkBox;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                objectCell = view.findViewById(R.id.content);
                checkBox = view.findViewById(R.id.cbx);
                isSelected = false;
            }

            public void setSelected(Boolean selected) {
                isSelected = selected;
                checkBox.setChecked(selected);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + objectCell.getDescription() + "'";
            }
        }
    }
}
