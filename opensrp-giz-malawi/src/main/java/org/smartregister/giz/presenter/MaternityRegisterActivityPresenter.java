package org.smartregister.giz.presenter;

import android.app.AlertDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.giz.R;
import org.smartregister.giz.interactor.MaternityRegisterActivityInteractor;
import org.smartregister.giz.util.GizUtils;
import org.smartregister.maternity.activity.BaseMaternityRegisterActivity;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.pojo.MaternityEventClient;
import org.smartregister.maternity.pojo.RegisterParams;
import org.smartregister.maternity.presenter.BaseMaternityRegisterActivityPresenter;
import org.smartregister.maternity.utils.MaternityConstants;
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

    @NonNull
    @Override
    public MaternityRegisterActivityContract.Interactor createInteractor() {
        return new MaternityRegisterActivityInteractor();
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

    @Override
    public void saveOutcomeForm(@NonNull String eventType, @Nullable Intent data) {
        super.saveOutcomeForm(eventType, data);
    }

    @Override
    public void onEventSaved(List<Event> events) {
        super.onEventSaved(events);
        for (Event event : events) {
            if (MaternityConstants.EventType.MATERNITY_OUTCOME.equals(event.getEventType())) {
                goToPncProfileActivity(event);
                break;
            }
        }
    }

    private void goToPncProfileActivity(Event event) {
        if (getView() instanceof BaseMaternityRegisterActivity) {
            BaseMaternityRegisterActivity activity = (BaseMaternityRegisterActivity) getView();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("You will now be redirected to record the Postnatal Care for the woman");
            builder.setCancelable(false);
            builder.setPositiveButton("GO TO PROFILE", (dialogInterface, i) -> {
                GizUtils.openPncProfile(activity, event.getBaseEntityId());
            });
            builder.create().show();
        }
    }

}
