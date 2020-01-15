package org.smartregister.giz.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.child.widgets.ChildEditTextFactory;
import org.smartregister.giz.widget.AdverseEffectDatePickerFactory;

public class ChildFormInteractor extends JsonFormInteractor {

    private static final ChildFormInteractor CHILD_INTERACTOR_INSTANCE = new ChildFormInteractor();

    private ChildFormInteractor() {
        super();
    }

    public static JsonFormInteractor getChildInteractorInstance() {
        return CHILD_INTERACTOR_INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new ChildEditTextFactory());
        map.put(JsonFormConstants.DATE_PICKER, new AdverseEffectDatePickerFactory());
    }
}
