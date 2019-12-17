package org.smartregister.giz.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.child.widgets.ChildDatePickerFactory;

public class ChildFormInteractor extends org.smartregister.child.interactor.ChildFormInteractor {
    private static final ChildFormInteractor CHILD_INTERACTOR_INSTANCE = new ChildFormInteractor();
    private static final OpdFormInteractor INSTANCE = new OpdFormInteractor();

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new OpdEditTextFactory());
        map.put(JsonFormConstants.BARCODE, new OpdBarcodeFactory());
        map.put(OpdConstants.JsonFormWidget.MULTI_SELECT_DRUG_PICKER, new OpdMultiSelectDrugPicker());
        map.put(JsonFormConstants.MULTI_SELECT_LIST, new OpdMultiSelectList());
    }
    public static JsonFormInteractor getChildInteractorInstance() {
        return CHILD_INTERACTOR_INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.DATE_PICKER, new ChildDatePickerFactory());
    }
}
