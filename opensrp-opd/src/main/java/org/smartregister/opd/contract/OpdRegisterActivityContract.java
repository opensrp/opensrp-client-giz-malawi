package org.smartregister.opd.contract;

import android.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public interface OpdRegisterActivityContract {

    interface View extends BaseRegisterContract.View {
        OpdRegisterActivityContract.Presenter presenter();
    }

    interface Presenter extends BaseRegisterContract.Presenter {

        void saveLanguage(String language);

        void saveForm(String jsonString, boolean isEditMode);

    }

    interface Model {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        String getLocationId(String locationName);

        Pair<Client, Event> processRegistration(String jsonString);

        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId, String familyId) throws Exception;

        String getInitials();

    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

    }

    interface InteractorCallBack {

    }
}