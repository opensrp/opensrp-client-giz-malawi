package org.smartregister.giz.presenter;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.contract.GizOpdProfileFragmentContract;
import org.smartregister.giz.dao.GizVisitDao;
import org.smartregister.giz.domain.ProfileHistory;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.util.NativeFormProcessor;
import org.smartregister.util.CallableInteractor;
import org.smartregister.util.CallableInteractorCallBack;
import org.smartregister.util.GenericInteractor;
import org.smartregister.util.Utils;
import org.smartregister.view.ListContract;
import org.smartregister.view.presenter.ListPresenter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import timber.log.Timber;

import static org.smartregister.giz.util.GizConstants.KEY.ENCOUNTER_TYPE;

public class GizOpdProfileVisitsFragmentPresenter extends ListPresenter<ProfileHistory> implements GizOpdProfileFragmentContract.Presenter<ProfileHistory> {

    private CallableInteractor callableInteractor;

    @Override
    public void openForm(Context context, String formName, String baseEntityID, String formSubmissionId) {
        CallableInteractor myInteractor = getCallableInteractor();
        Callable<JSONObject> callable = () -> readFormAndAddValues(readFormAsJson(context, formName, baseEntityID), formSubmissionId);
        myInteractor.execute(callable, new CallableInteractorCallBack<JSONObject>() {
            @Override
            public void onResult(JSONObject jsonObject) {
                GizOpdProfileFragmentContract.View<ProfileHistory> view = getView();
                if (view != null) {
                    if (jsonObject != null) {
                        view.startJsonForm(jsonObject);
                    } else {
                        view.onFetchError(new IllegalArgumentException("Form not found"));
                    }
                    view.setLoadingState(false);
                }
            }

            @Override
            public void onError(Exception ex) {
                ListContract.View<ProfileHistory> view = getView();
                if (view != null) {
                    view.onFetchError(ex);
                    view.setLoadingState(false);
                }
            }
        });
    }

    private JSONObject readFormAndAddValues(JSONObject jsonObject, String formSubmissionId) throws JSONException {
        if (StringUtils.isEmpty(formSubmissionId)) return jsonObject;

        NativeFormProcessor processor = NativeFormProcessor.createInstance(jsonObject);

        // read values
        String eventJson = GizVisitDao.fetchEventByFormSubmissionId(formSubmissionId);
        JSONObject savedEvent = new JSONObject(eventJson);

        Event event = GizMalawiApplication.getInstance().getEcSyncHelper().convert(savedEvent, Event.class);
        SimpleDateFormat sdfDate = new SimpleDateFormat(GizConstants.DateTimeFormat.dd_MMM_yyyy, Locale.US);
        boolean readonly = !sdfDate.format(event.getEventDate().toDate()).equals(sdfDate.format(new Date()));

        Map<String, Object> values = processor.getFormResults(savedEvent);
        jsonObject.put(GizConstants.Properties.FORM_SUBMISSION_ID, formSubmissionId);


        // inject values
        processor.populateValues(values, jsonObject1 -> {
            try {
                jsonObject1.put(JsonFormConstants.READ_ONLY, readonly);
            } catch (JSONException e) {
                Timber.e(e);
            }
        });

        return jsonObject;
    }


    @Override
    public JSONObject readFormAsJson(Context context, String formName, String baseEntityID) throws JSONException {
        // read form and inject base id
        String jsonForm = readAssetContents(context, formName);
        JSONObject jsonObject = new JSONObject(jsonForm);
        jsonObject.put(GizConstants.Properties.BASE_ENTITY_ID, baseEntityID);
        return jsonObject;
    }

    @Override
    public String readAssetContents(Context context, String path) {
        return Utils.readAssetContents(context, path);
    }

    @Override
    public void saveForm(String jsonString, Context context) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String title = jsonObject.getString(ENCOUNTER_TYPE);

            // TODO
            if (title.equals(GizConstants.OpdModuleEvents.OPD_CHECK_IN)) {
                CallableInteractor myInteractor = getCallableInteractor();

                Callable<Void> callable = () -> {
                    //JSONObject jsonObject = new JSONObject(jsonString);
                    NativeFormProcessor processor = NativeFormProcessor.createInstance(jsonObject);
                    String entityId = jsonObject.getString(GizConstants.Properties.BASE_ENTITY_ID);
                    String formSubmissionId = jsonObject.has(GizConstants.Properties.FORM_SUBMISSION_ID) ?
                            jsonObject.getString(GizConstants.Properties.FORM_SUBMISSION_ID) : null;

                    // String eventType = jsonObject.getString(ENCOUNTER_TYPE);

                    // update metadata
                    processor.withBindType("ec_client")
                            .withEncounterType(title)
                            .withFormSubmissionId(formSubmissionId)
                            .withEntityId(entityId)
                            .tagEventMetadata()
                            // create and save event to db
                            .saveEvent()
                            // execute client processing
                            .clientProcessForm();

                    return null;
                };

                myInteractor.execute(callable, new CallableInteractorCallBack<Void>() {
                    @Override
                    public void onResult(Void aVoid) {
                        GizOpdProfileFragmentContract.View<ProfileHistory> view = getView();
                        if (view != null) {
                            view.reloadFromSource();
                            view.setLoadingState(false);
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        GizOpdProfileFragmentContract.View<ProfileHistory> view = getView();
                        if (view == null) return;
                        view.onFetchError(ex);
                        view.setLoadingState(false);
                    }
                });
            }

        } catch (Exception ex) {
            Timber.e(ex.getMessage());
        }

    }

    @Override
    public CallableInteractor getCallableInteractor() {
        if (callableInteractor == null)
            callableInteractor = new GenericInteractor();

        return callableInteractor;
    }

    @Override
    public GizOpdProfileFragmentContract.View<ProfileHistory> getView() {
        return (GizOpdProfileFragmentContract.View<ProfileHistory>) super.getView();
    }
}
