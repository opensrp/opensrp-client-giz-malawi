package org.smartregister.giz.presenter;

import android.app.Activity;

import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.presenter.BaseChildRegisterPresenter;
import org.smartregister.giz.view.NavigationMenu;

import java.util.List;

public class GizChildRegisterPresenter extends BaseChildRegisterPresenter {
    public GizChildRegisterPresenter(ChildRegisterContract.View view, ChildRegisterContract.Model model) {
        super(view, model);
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        super.onRegistrationSaved(isEdit);
        NavigationMenu navigationMenu = NavigationMenu.getInstance((Activity) viewReference.get(), null, null);
        if (navigationMenu != null) {
            navigationMenu.runRegisterCount();
        }
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        //Do nothing
    }
}
