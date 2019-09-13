package org.smartregister.opd.provider;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.opd.holders.FooterViewHolder;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.opd.R;
import org.smartregister.opd.holders.OpdRegisterViewHolder;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.util.Set;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterProvider implements RecyclerViewProvider<OpdRegisterViewHolder> {
    private final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private Context context;

    public OpdRegisterProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.context = context;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, OpdRegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, viewHolder);
            populateIdentifierColumn(pc, viewHolder);

            return;
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(org.smartregister.R.string.str_page_info), currentPageCount,
                        totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {//Implement Abstract Method
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public OpdRegisterViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.opd_register_list_row, parent, false);

        /*
        ConfigurableViewsHelper helper = ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper();
        if (helper.isJsonViewsEnabled()) {

            ViewConfiguration viewConfiguration = helper.getViewConfiguration(Constants.CONFIGURATION.HOME_REGISTER_ROW);
            ViewConfiguration commonConfiguration = helper.getViewConfiguration(COMMON_REGISTER_ROW);

            if (viewConfiguration != null) {
                return helper.inflateDynamicView(viewConfiguration, commonConfiguration, view, R.id.register_columns, false);
            }
        }*/

        return new OpdRegisterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof FooterViewHolder;
    }

    public void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, OpdRegisterViewHolder viewHolder) {

        String registerType = Utils.getValue(pc.getColumnmaps(), OpdDbConstants.KEY.REGISTER_TYPE, false);

        if (registerType.contains("child")) {
            viewHolder.showCareGiveName();

            String parentFirstName = Utils.getValue(pc.getColumnmaps(), OpdDbConstants.KEY.FAMILY_FIRST_NAME, true);
            String parentLastName = Utils.getValue(pc.getColumnmaps(), OpdDbConstants.KEY.FAMILY_LAST_NAME, true);
            String parentMiddleName = Utils.getValue(pc.getColumnmaps(), OpdDbConstants.KEY.FAMILY_MIDDLE_NAME, true);

            String parentName = context.getResources().getString(R.string.care_giver_initials)
                    + ": "
                    + org.smartregister.util.Utils.getName(parentFirstName, parentMiddleName + " " + parentLastName);
            fillValue(viewHolder.textViewParentName, WordUtils.capitalize(parentName));
        }

        String firstName = Utils.getValue(pc.getColumnmaps(), OpdDbConstants.KEY.FIRST_NAME, true);
        String middleName = Utils.getValue(pc.getColumnmaps(), OpdDbConstants.KEY.MIDDLE_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), OpdDbConstants.KEY.LAST_NAME, true);
        String childName = org.smartregister.util.Utils.getName(firstName, middleName + " " + lastName);

        String dobString = Utils.getDuration(Utils.getValue(pc.getColumnmaps(), OpdDbConstants.KEY.DOB, false));
        //dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        fillValue(viewHolder.textViewChildName, WordUtils.capitalize(childName) + ", " + WordUtils.capitalize(OpdUtils.getTranslatedDate(dobString, context)));
        setAddressAndGender(pc, viewHolder);

        addButtonClickListeners(client, viewHolder);

    }

    public void populateIdentifierColumn(CommonPersonObjectClient pc, OpdRegisterViewHolder viewHolder) {
        //fillValue(viewHolder.ancId, String.format(context.getString(R.string.unique_id_text), uniqueId));
    }

    public static void fillValue(TextView v, String value) {
        if (v != null) {
            v.setText(value);
        }

    }

    public void setAddressAndGender(CommonPersonObjectClient pc, OpdRegisterViewHolder viewHolder) {
        String address = Utils.getValue(pc.getColumnmaps(), OpdDbConstants.KEY.FAMILY_HOME_ADDRESS, true);
        String gender = Utils.getValue(pc.getColumnmaps(), OpdDbConstants.KEY.GENDER, true);
        fillValue(viewHolder.textViewAddressGender, address + " \u00B7 " + gender);
    }

    public void addButtonClickListeners(SmartRegisterClient client, OpdRegisterViewHolder viewHolder) {
        View patient = viewHolder.childColumn;
        attachPatientOnclickListener(patient, client);
    }

    public void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        //view.setTag(R.id.VIEW_ID, BaseFamilyRegisterFragment.CLICK_VIEW_NORMAL);
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public View.OnClickListener getPaginationClickListener() {
        return paginationClickListener;
    }

    public void setPaginationClickListener(View.OnClickListener paginationClickListener) {
        this.paginationClickListener = paginationClickListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}