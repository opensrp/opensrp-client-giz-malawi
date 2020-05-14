package org.smartregister.giz.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.UniqueId;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.interactor.BaseMaternityRegisterActivityInteractor;
import org.smartregister.maternity.pojos.MaternityEventClient;
import org.smartregister.maternity.pojos.RegisterParams;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 31-03-2020.
 */
public class MaternityRegisterActivityInteractor extends BaseMaternityRegisterActivityInteractor {

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final MaternityRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();
                final String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.isBlank(entityId)) {
                            callBack.onNoUniqueId();
                        } else {
                            callBack.onUniqueIdFetched(triple, entityId);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final List<MaternityEventClient> maternityEventClientList, final String jsonString
            , final RegisterParams registerParams, @NonNull final MaternityRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(maternityEventClientList, jsonString, registerParams);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onRegistrationSaved(registerParams.isEditMode());
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void saveRegistration(@NonNull List<MaternityEventClient> maternityEventClients, @NonNull String jsonString,
                                  @NonNull RegisterParams params) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();

            for (int i = 0; i < maternityEventClients.size(); i++) {
                try {

                    MaternityEventClient maternityEventClient = maternityEventClients.get(i);
                    Client baseClient = maternityEventClient.getClient();
                    Event baseEvent = maternityEventClient.getEvent();

                    if (baseClient != null) {
                        JSONObject clientJson = new JSONObject(MaternityJsonFormUtils.gson.toJson(baseClient));
                        if (params.isEditMode()) {
                            try {
                                MaternityJsonFormUtils.mergeAndSaveClient(baseClient);
                            } catch (Exception e) {
                                Timber.e(e);
                            }
                        } else {
                            getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                        }
                    }

                    addEvent(params, currentFormSubmissionIds, baseEvent);
                    updateOpenSRPId(jsonString, params, baseClient);
                    addImageLocation(jsonString, i, baseClient, baseEvent);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(currentFormSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }
    private void addImageLocation(String jsonString, int i, Client baseClient, Event baseEvent) {
        if (baseClient != null && baseEvent != null) {
            String imageLocation = null;
            if (i == 0) {
                imageLocation = MaternityJsonFormUtils.getFieldValue(jsonString, MaternityConstants.KEY.PHOTO);
            } else if (i == 1) {
                imageLocation =
                        MaternityJsonFormUtils.getFieldValue(jsonString, MaternityJsonFormUtils.STEP2, MaternityConstants.KEY.PHOTO);
            }

            if (StringUtils.isNotBlank(imageLocation)) {
                MaternityJsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }
        }
    }

    private void updateOpenSRPId(@NonNull String jsonString, @NonNull RegisterParams params, @Nullable Client baseClient) {
        if (params.isEditMode()) {
            // Unassign current OPENSRP ID
            if (baseClient != null) {
                try {
                    String newOpenSRPId = baseClient.getIdentifier(MaternityJsonFormUtils.OPENSRP_ID).replace("-", "");
                    String currentOpenSRPId = MaternityJsonFormUtils.getString(jsonString, MaternityJsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
                    if (!newOpenSRPId.equals(currentOpenSRPId)) {
                        //OPENSRP ID was changed
                        getUniqueIdRepository().open(currentOpenSRPId);
                    }
                } catch (Exception e) {//might crash if M_ZEIR
                    Timber.d(e, "RegisterInteractor --> unassign opensrp id");
                }
            }

        } else {
            if (baseClient != null) {
                String opensrpId = baseClient.getIdentifier(MaternityJsonFormUtils.ZEIR_ID);
                //mark OPENSRP ID as used
                getUniqueIdRepository().close(opensrpId);
            }
        }
    }

    private void addEvent(RegisterParams params, List<String> currentFormSubmissionIds, @Nullable Event baseEvent) throws JSONException {
        if (baseEvent != null) {
            JSONObject eventJson = new JSONObject(MaternityJsonFormUtils.gson.toJson(baseEvent));
            getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, params.getStatus());
            currentFormSubmissionIds
                    .add(eventJson.getString(EventClientRepository.event_column.formSubmissionId.toString()));
        }
    }

}
