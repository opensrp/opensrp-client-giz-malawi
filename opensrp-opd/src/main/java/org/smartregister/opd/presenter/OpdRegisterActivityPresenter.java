package org.smartregister.opd.presenter;


import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.interactor.OpdRegisterActivityInteractor;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterActivityPresenter implements OpdRegisterActivityContract.Presenter, OpdRegisterActivityContract.InteractorCallBack {
    public static final String TAG = OpdRegisterActivityPresenter.class.getName();
    private WeakReference<OpdRegisterActivityContract.View> viewReference;
    private OpdRegisterActivityContract.Interactor interactor;
    private OpdRegisterActivityContract.Model model;

    public OpdRegisterActivityPresenter(OpdRegisterActivityContract.View view, OpdRegisterActivityContract.Model model) {
        viewReference = new WeakReference<>(view);
        interactor = new OpdRegisterActivityInteractor();
        this.model = model;
    }

    public void setModel(OpdRegisterActivityContract.Model model) {
        this.model = model;
    }

    public void setInteractor(OpdRegisterActivityContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        model.registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        model.unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        viewReference = null;//set to null on destroy
        // Inform interactor
        interactor.onDestroy(isChangingConfiguration);
        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            interactor = null;
            model = null;
        }
    }

    @Override
    public void updateInitials() {
        String initials = model.getInitials();
        if (initials != null) {
            getView().updateInitialsText(initials);
        }
    }

    private OpdRegisterActivityContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void saveLanguage(String language) {
        model.saveLanguage(language);
        getView().displayToast(language + " selected");
    }

    @Override
    public void saveForm(String jsonString, boolean isEditMode) {

    }
}