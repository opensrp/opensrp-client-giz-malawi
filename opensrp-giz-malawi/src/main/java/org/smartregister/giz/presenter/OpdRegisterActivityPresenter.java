package org.smartregister.giz.presenter;

import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public class OpdRegisterActivityPresenter extends BaseOpdRegisterActivityPresenter {

    public OpdRegisterActivityPresenter(OpdRegisterActivityContract.View view, OpdRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void saveForm(String jsonString, boolean isEditMode) {

    }
}
