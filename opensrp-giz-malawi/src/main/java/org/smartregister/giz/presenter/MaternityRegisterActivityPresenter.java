package org.smartregister.giz.presenter;

import android.support.annotation.NonNull;

import org.smartregister.domain.FetchStatus;
import org.smartregister.giz.R;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.pojos.MaternityEventClient;
import org.smartregister.maternity.pojos.RegisterParams;
import org.smartregister.maternity.presenter.BaseMaternityRegisterActivityPresenter;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;

import java.util.List;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 31-03-2020.
 */
public class MaternityRegisterActivityPresenter extends BaseMaternityRegisterActivityPresenter {

    public MaternityRegisterActivityPresenter(@NonNull MaternityRegisterActivityContract.View view, @NonNull MaternityRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void saveForm(@NonNull String jsonString, @NonNull RegisterParams registerParams) {
        if (registerParams.getFormTag() == null) {
            registerParams.setFormTag(MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences()));
        }

        List<MaternityEventClient> maternityEventClientList = model.processRegistration(jsonString, registerParams.getFormTag());
        if (maternityEventClientList == null || maternityEventClientList.isEmpty()) {
            return;
        }

        registerParams.setEditMode(false);
        interactor.saveRegistration(maternityEventClientList, jsonString, registerParams, this);
    }

    @Override
    public void onNoUniqueId() {
        if (getView() != null) {
            getView().displayShortToast(R.string.no_unique_id);
        }
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }
}
