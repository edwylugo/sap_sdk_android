package com.company.mysapcpsdkproject.mdui.listtechnicalequipmentbatchs;

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
import com.company.mysapcpsdkproject.viewmodel.listtechnicalequipmentbatch.ListTechnicalEquipmentBatchViewModel;
import com.sap.cloud.android.odata.ipmcontainer.ipmContainerMetadata.EntitySets;
import com.sap.cloud.android.odata.ipmcontainer.ListTechnicalEquipmentBatch;
import com.sap.cloud.mobile.fiori.object.ObjectCell;
import com.sap.cloud.mobile.odata.DataValue;
import com.sap.cloud.mobile.odata.EntityValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ListTechnicalEquipmentBatchsListFragment extends InterfacedFragment<ListTechnicalEquipmentBatch> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListTechnicalEquipmentBatchsActivity.class);

    /**
     * Error handler to display message should error occurs
     */
    private ErrorHandler errorHandler;

    /**
     * List adapter to be used with RecyclerView containing all instances of listTechnicalEquipmentBatchs
     */
    private ListTechnicalEquipmentBatchListAdapter adapter;

    private SwipeRefreshLayout refreshLayout;

    /**
     * View model of the entity type
     */
    private ListTechnicalEquipmentBatchViewModel viewModel;

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
        activityTitle = getString(EntitySetListActivity.EntitySetName.ListTechnicalEquipmentBatchs.getTitleId());
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
        this.adapter = new ListTechnicalEquipmentBatchListAdapter(currentActivity);
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
                .get(ListTechnicalEquipmentBatchViewModel.class);
        } else {
            viewModel = ViewModelProviders.of(currentActivity).get(ListTechnicalEquipmentBatchViewModel.class);
            viewModel.initialRead();
        }

        viewModel.getObservableItems().observe(this, listTechnicalEquipmentBatchs -> {
            if (listTechnicalEquipmentBatchs != null) {
                adapter.setItems(listTechnicalEquipmentBatchs);

                ListTechnicalEquipmentBatch item = containsItem(listTechnicalEquipmentBatchs, viewModel.getSelectedEntity().getValue());
                if (item == null) {
                    item = listTechnicalEquipmentBatchs.isEmpty() ? null : listTechnicalEquipmentBatchs.get(0);
                }

                if (item == null) {
                    hideDetailFragment();
                } else {
                    viewModel.setInFocusId(adapter.getItemIdForListTechnicalEquipmentBatch(item));
                    if (currentActivity.getResources().getBoolean(R.bool.two_pane)) {
                        viewModel.setSelectedEntity(item);
                        if (!isInActionMode && !((ListTechnicalEquipmentBatchsActivity) currentActivity).isNavigationDisabled) {
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
    private ListTechnicalEquipmentBatch containsItem(List<ListTechnicalEquipmentBatch> items, ListTechnicalEquipmentBatch item) {
        ListTechnicalEquipmentBatch found = null;
        if( item != null ) {
            for( ListTechnicalEquipmentBatch entity: items ) {
                if( adapter.getItemIdForListTechnicalEquipmentBatch(entity) == adapter.getItemIdForListTechnicalEquipmentBatch(item)) {
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
    private void onDeleteComplete(@NonNull OperationResult<ListTechnicalEquipmentBatch> result) {
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
    private ListTechnicalEquipmentBatch setItemIdSelected(int itemId) {
        LiveData<List<ListTechnicalEquipmentBatch>> liveData = viewModel.getObservableItems();
        List<ListTechnicalEquipmentBatch> listTechnicalEquipmentBatchs = liveData.getValue();
        if (listTechnicalEquipmentBatchs != null && listTechnicalEquipmentBatchs.size() > 0) {
            viewModel.setInFocusId(adapter.getItemIdForListTechnicalEquipmentBatch(listTechnicalEquipmentBatchs.get(itemId)));
            return listTechnicalEquipmentBatchs.get(itemId);
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
    public class ListTechnicalEquipmentBatchsListActionMode implements ActionMode.Callback {
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
                    ListTechnicalEquipmentBatch listTechnicalEquipmentBatchEntity = viewModel.getSelected(0);
                    if (viewModel.numberOfSelected() == 1 && listTechnicalEquipmentBatchEntity != null) {
                        isInActionMode = false;
                        actionMode.finish();
                        viewModel.setSelectedEntity(listTechnicalEquipmentBatchEntity);
                        if( currentActivity.getResources().getBoolean(R.bool.two_pane)) {
                            listener.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, listTechnicalEquipmentBatchEntity);
                        }
                        listener.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, listTechnicalEquipmentBatchEntity);
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
     * List adapter to be used with RecyclerView. It contains the set of listTechnicalEquipmentBatchs.
     */
    public class ListTechnicalEquipmentBatchListAdapter extends RecyclerView.Adapter<ListTechnicalEquipmentBatchListAdapter.ViewHolder> {

        private Context context;

        /** Entire list of ListTechnicalEquipmentBatch collection */
        private List<ListTechnicalEquipmentBatch> listTechnicalEquipmentBatchs;

        /** RecyclerView this adapter is associate with */
        private RecyclerView recyclerView;

        /** Flag to indicate whether we have checked retained selected listTechnicalEquipmentBatchs */
        private boolean checkForSelectedOnCreate = false;

        public ListTechnicalEquipmentBatchListAdapter(Context context) {
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
        public void setItems(@NonNull List<ListTechnicalEquipmentBatch> currentListTechnicalEquipmentBatchs) {
            if (listTechnicalEquipmentBatchs == null) {
                listTechnicalEquipmentBatchs = new ArrayList<>(currentListTechnicalEquipmentBatchs);
                notifyItemRangeInserted(0, currentListTechnicalEquipmentBatchs.size());
            } else {
                DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    @Override
                    public int getOldListSize() {
                        return listTechnicalEquipmentBatchs.size();
                    }

                    @Override
                    public int getNewListSize() {
                        return currentListTechnicalEquipmentBatchs.size();
                    }

                    @Override
                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        return listTechnicalEquipmentBatchs.get(oldItemPosition).getReadLink().equals(
                                currentListTechnicalEquipmentBatchs.get(newItemPosition).getReadLink());
                    }

                    @Override
                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        ListTechnicalEquipmentBatch listTechnicalEquipmentBatchEntity = listTechnicalEquipmentBatchs.get(oldItemPosition);
                        return !listTechnicalEquipmentBatchEntity.isUpdated() && currentListTechnicalEquipmentBatchs.get(newItemPosition).equals(listTechnicalEquipmentBatchEntity);
                    }

                    @Nullable
                    @Override
                    public Object getChangePayload(final int oldItemPosition, final int newItemPosition) {
                        return super.getChangePayload(oldItemPosition, newItemPosition);
                    }
                });
                listTechnicalEquipmentBatchs.clear();
                listTechnicalEquipmentBatchs.addAll(currentListTechnicalEquipmentBatchs);
                result.dispatchUpdatesTo(this);
            }
        }

        @Override
        public final long getItemId(int position) {
            return getItemIdForListTechnicalEquipmentBatch(listTechnicalEquipmentBatchs.get(position));
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

            final ListTechnicalEquipmentBatch listTechnicalEquipmentBatchEntity = listTechnicalEquipmentBatchs.get(holder.getAdapterPosition());
            DataValue dataValue = listTechnicalEquipmentBatchEntity.getDataValue(ListTechnicalEquipmentBatch.id);
            if (dataValue != null) {
                holder.masterPropertyValue = dataValue.toString();
            }
            populateObjectCell(holder, listTechnicalEquipmentBatchEntity);

            boolean isActive = getItemIdForListTechnicalEquipmentBatch(listTechnicalEquipmentBatchEntity) == viewModel.getInFocusId();
            if (isActive) {
                setItemIdSelected(holder.getAdapterPosition());
            }
            boolean isListTechnicalEquipmentBatchSelected = viewModel.selectedContains(listTechnicalEquipmentBatchEntity);
            setViewBackground(holder.view, isListTechnicalEquipmentBatchSelected, isActive);

            holder.view.setOnLongClickListener(new onActionModeStartClickListener(holder));
            setOnClickListener(holder, listTechnicalEquipmentBatchEntity);

            setOnCheckedChangeListener(holder, listTechnicalEquipmentBatchEntity);
            holder.setSelected(isListTechnicalEquipmentBatchSelected);
            setDetailImage(holder, listTechnicalEquipmentBatchEntity);
        }

        /**
         * Check to see if there are an retained selected listTechnicalEquipmentBatchEntity on start.
         * This situation occurs when a rotation with selected listTechnicalEquipmentBatchs is triggered by user.
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
         * If there are selected listTechnicalEquipmentBatchs via long press, clear them as click and long press are mutually exclusive
         * In addition, since we are clearing all selected listTechnicalEquipmentBatchs via long press, finish the action mode.
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

        private void processClickAction(@NonNull View view, @NonNull ListTechnicalEquipmentBatch listTechnicalEquipmentBatchEntity) {
            resetPreviouslyClicked();
            setViewBackground(view, false, true);
            viewModel.setInFocusId(getItemIdForListTechnicalEquipmentBatch(listTechnicalEquipmentBatchEntity));
        }

        /**
         * Set ViewHolder's view onClickListener
         *
         * @param holder
         * @param listTechnicalEquipmentBatchEntity associated with this ViewHolder
         */
        private void setOnClickListener(@NonNull ViewHolder holder, @NonNull ListTechnicalEquipmentBatch listTechnicalEquipmentBatchEntity) {
            holder.view.setOnClickListener(view -> {
                boolean isNavigationDisabled = ((ListTechnicalEquipmentBatchsActivity) currentActivity).isNavigationDisabled;
                if(isNavigationDisabled) {
                    Toast.makeText(currentActivity, "Please save your changes first...", Toast.LENGTH_LONG).show();
                } else {
                    resetSelected();
                    resetPreviouslyClicked();
                    processClickAction(view, listTechnicalEquipmentBatchEntity);
                    viewModel.setSelectedEntity(listTechnicalEquipmentBatchEntity);
                    listener.onFragmentStateChange(UIConstants.EVENT_ITEM_CLICKED, listTechnicalEquipmentBatchEntity);
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
                boolean isNavigationDisabled = ((ListTechnicalEquipmentBatchsActivity) currentActivity).isNavigationDisabled;
                if( isNavigationDisabled ) {
                    Toast.makeText(currentActivity, "Please save your changes first...", Toast.LENGTH_LONG).show();
                } else {
                    if (!isInActionMode) {
                        actionMode = ((AppCompatActivity) currentActivity).startSupportActionMode(new ListTechnicalEquipmentBatchsListActionMode());
                        adapter.notifyDataSetChanged();
                    }
                    holder.setSelected(!holder.isSelected);
                }
                return true;
            }
        }

        /** sets the detail image to the given <code>viewHolder</code> */
        private void setDetailImage(@NonNull ViewHolder viewHolder, @NonNull ListTechnicalEquipmentBatch listTechnicalEquipmentBatchEntity) {
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
                viewModel.downloadMedia(listTechnicalEquipmentBatchEntity, media -> {
                    viewHolder.objectCell.prepareDetailImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                    Drawable image = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(media, 0, media.length));
                    viewHolder.objectCell.setDetailImage(image);
                }, error -> {
                    if (listTechnicalEquipmentBatchEntity.getDataValue(ListTechnicalEquipmentBatch.id) != null && !listTechnicalEquipmentBatchEntity.getDataValue(ListTechnicalEquipmentBatch.id).toString().isEmpty()) {
                        viewHolder.objectCell.setDetailImageCharacter(listTechnicalEquipmentBatchEntity.getDataValue(ListTechnicalEquipmentBatch.id).toString().substring(0, 1));
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
         * @param listTechnicalEquipmentBatchEntity associated with this ViewHolder
         */
        private void setOnCheckedChangeListener(@NonNull ViewHolder holder, @NonNull ListTechnicalEquipmentBatch listTechnicalEquipmentBatchEntity) {
            holder.checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (checked) {
                    viewModel.addSelected(listTechnicalEquipmentBatchEntity);
                    manageActionModeOnCheckedTransition();
                    resetPreviouslyClicked();
                } else {
                    viewModel.removeSelected(listTechnicalEquipmentBatchEntity);
                    manageActionModeOnUncheckedTransition();
                }
                setViewBackground(holder.view, viewModel.selectedContains(listTechnicalEquipmentBatchEntity), false);
                setDetailImage(holder, listTechnicalEquipmentBatchEntity);
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
                actionMode = ((AppCompatActivity) currentActivity).startSupportActionMode(new ListTechnicalEquipmentBatchsListActionMode());
            }
            if (viewModel.numberOfSelected() > 1) {
                actionMode.getMenu().findItem(R.id.update_item).setVisible(false);
            }
            actionMode.setTitle(String.valueOf(viewModel.numberOfSelected()));
        }

        /*
         * This is called when one of the selected listTechnicalEquipmentBatchs has been de-selected
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

        private void populateObjectCell(@NonNull ViewHolder viewHolder, @NonNull ListTechnicalEquipmentBatch listTechnicalEquipmentBatchEntity) {

            DataValue dataValue = listTechnicalEquipmentBatchEntity.getDataValue(ListTechnicalEquipmentBatch.id);
            String masterPropertyValue = null;
            if (dataValue != null) {
                masterPropertyValue = dataValue.toString();
            }
            viewHolder.objectCell.setHeadline(masterPropertyValue);
            viewHolder.objectCell.setDetailImage(null);
            setDetailImage(viewHolder, listTechnicalEquipmentBatchEntity);

            viewHolder.objectCell.setSubheadline("Subheadline goes here");
            viewHolder.objectCell.setFootnote("Footnote goes here");
            if (listTechnicalEquipmentBatchEntity.getInErrorState()) {
                viewHolder.objectCell.setIcon(R.drawable.ic_error_state, 0, R.string.error_state);
            }
            else if (listTechnicalEquipmentBatchEntity.isUpdated()) {
                viewHolder.objectCell.setIcon(R.drawable.ic_updated_state, 0, R.string.updated_state);
            }
            else if (listTechnicalEquipmentBatchEntity.isLocal()) {
                viewHolder.objectCell.setIcon(R.drawable.ic_local_state, 0, R.string.local_state);
            }
            else {
                viewHolder.objectCell.setIcon(R.drawable.ic_download_state, 0, R.string.download_state);
            }
            viewHolder.objectCell.setIcon(R.drawable.default_dot, 1, R.string.attachment_item_content_desc);
            viewHolder.objectCell.setIcon("!", 2);
        }

        /**
         * Set background of view to indicate listTechnicalEquipmentBatchEntity selection status
         * Selected and Active are mutually exclusive. Only one can be true
         *
         * @param view
         * @param isListTechnicalEquipmentBatchSelected - true if listTechnicalEquipmentBatchEntity is selected via long press action
         * @param isActive               - true if listTechnicalEquipmentBatchEntity is selected via click action
         */
        private void setViewBackground(@NonNull View view, boolean isListTechnicalEquipmentBatchSelected, boolean isActive) {
            boolean isMasterDetailView = currentActivity.getResources().getBoolean(R.bool.two_pane);
            if (isListTechnicalEquipmentBatchSelected) {
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.list_item_selected));
            } else if (isActive && isMasterDetailView && !isInActionMode) {
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.list_item_active));
            } else {
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.list_item_default));
            }
        }

        @Override
        public int getItemCount() {
            if (listTechnicalEquipmentBatchs == null) {
                return 0;
            } else {
                return listTechnicalEquipmentBatchs.size();
            }
        }

        /**
         * Computes a stable ID for each ListTechnicalEquipmentBatch object for use to locate the ViewHolder
         *
         * @param listTechnicalEquipmentBatchEntity
         * @return an ID based on the primary key of ListTechnicalEquipmentBatch
         */
        private long getItemIdForListTechnicalEquipmentBatch(ListTechnicalEquipmentBatch listTechnicalEquipmentBatchEntity) {
            if(listTechnicalEquipmentBatchEntity.getReadLink() == null) return 0L;
            else return listTechnicalEquipmentBatchEntity.getReadLink().hashCode();
        }

        /**
         * ViewHolder for RecyclerView.
         * Each view has a Fiori ObjectCell and a checkbox (used by long press)
         */
        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View view;

            public boolean isSelected;

            public String masterPropertyValue;

            /** Fiori ObjectCell to display listTechnicalEquipmentBatchEntity in list */
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