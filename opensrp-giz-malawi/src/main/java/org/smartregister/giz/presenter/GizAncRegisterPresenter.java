package org.smartregister.giz.presenter;

import android.app.Activity;

import org.smartregister.anc.library.contract.RegisterContract;
import org.smartregister.anc.library.presenter.RegisterPresenter;
import org.smartregister.giz.view.NavigationMenu;

import java.lang.ref.WeakReference;

public class GizAncRegisterPresenter extends RegisterPresenter {
    private WeakReference<RegisterContract.View> gizAncViewReference;

    public GizAncRegisterPresenter(RegisterContract.View view) {
        super(view);
        this.gizAncViewReference = new WeakReference(view);

    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        super.onRegistrationSaved(isEdit);
        NavigationMenu navigationMenu = NavigationMenu.getInstance((Activity) gizAncViewReference.get(), null, null);
        if (navigationMenu != null) {
            navigationMenu.runRegisterCount();
        }
    }
}
