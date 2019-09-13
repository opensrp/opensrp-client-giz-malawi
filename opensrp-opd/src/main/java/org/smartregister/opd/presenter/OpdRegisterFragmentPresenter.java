package org.smartregister.opd.presenter;

import org.smartregister.opd.contract.OpdRegisterFragmentContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

import org.apache.commons.lang3.StringUtils;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class OpdRegisterFragmentPresenter implements OpdRegisterFragmentContract.Presenter {


    protected Set<View> visibleColumns = new TreeSet<>();
    private WeakReference<OpdRegisterFragmentContract.View> viewReference;
    private OpdRegisterFragmentContract.Model model;
    private RegisterConfiguration config;
    private String viewConfigurationIdentifier;

    public OpdRegisterFragmentPresenter(OpdRegisterFragmentContract.View view, OpdRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = model.defaultRegisterConfiguration();
    }

    @Override
    public void processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = model.getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            config = (RegisterConfiguration) viewConfiguration.getMetadata();
            setVisibleColumns(model.getRegisterActiveColumns(viewConfigurationIdentifier));
        }

        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public void initializeQueries(String mainCondition) {

        String countSelect = model.countSelect(CoreConstants.TABLE_NAME.CHILD, mainCondition);
        String mainSelect = model.mainSelect(CoreConstants.TABLE_NAME.CHILD, CoreConstants.TABLE_NAME.FAMILY, CoreConstants.TABLE_NAME.FAMILY_MEMBER, mainCondition);

        getView().initializeQueryParams(CoreConstants.TABLE_NAME.CHILD, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {
        //ServiceTools.startSyncService(getActivity());
    }

    @Override
    public void searchGlobally(String uniqueId) {
        // TODO implement search global
    }

    private void setVisibleColumns(Set<View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    protected OpdRegisterFragmentContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(org.smartregister.R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s is null AND %s", DBConstants.KEY.DATE_REMOVED, ChildDBConstants.childAgeLimitFilter());
    }

    @Override
    public String getMainCondition(String tableName) {
        return String.format(" %s is null AND %s", tableName + "." + DBConstants.KEY.DATE_REMOVED, ChildDBConstants.childAgeLimitFilter(tableName));
    }

    @Override
    public String getDefaultSortQuery() {
        return DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";// AND "+ChildDBConstants.childAgeLimitFilter();
    }

    @Override
    public String getDueFilterCondition() {
        return getMainCondition() + " AND " + ChildDBConstants.childDueFilter();
    }

    public void setModel(OpdRegisterFragmentContract.Model model) {
        this.model = model;
    }
}
