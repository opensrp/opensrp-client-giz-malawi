package org.smartregister.giz.presenter;

import org.smartregister.anc.library.contract.RegisterFragmentContract;
import org.smartregister.anc.library.model.RegisterFragmentModel;
import org.smartregister.anc.library.presenter.RegisterFragmentPresenter;
import org.smartregister.giz.model.AncRegisterFragmentModel;

public class GizAncRegisterFragmentPresenter extends RegisterFragmentPresenter {
    public GizAncRegisterFragmentPresenter(RegisterFragmentContract.View view, String viewConfigurationIdentifier) {
        super(view, viewConfigurationIdentifier);
        setModel(new AncRegisterFragmentModel());
    }

    @Override
    public void setModel(RegisterFragmentContract.Model model) {
        super.setModel(model);
    }
}
