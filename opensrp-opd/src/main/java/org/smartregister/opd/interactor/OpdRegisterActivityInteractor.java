package org.smartregister.opd.interactor;


import android.support.annotation.VisibleForTesting;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.UniqueId;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.utils.AppExecutors;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterActivityInteractor implements OpdRegisterActivityContract.Interactor {

    public static final String TAG = OpdRegisterActivityInteractor.class.getName();
    private AppExecutors appExecutors;


    public OpdRegisterActivityInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    OpdRegisterActivityInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        //TODO set presenter or model to null
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final OpdRegisterActivityContract.InteractorCallBack callBack, final String familyId) {

    }

    @Override
    public void saveRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode, final OpdRegisterActivityContract.InteractorCallBack callBack) {

    }

    @Override
    public void removeChildFromRegister(final String closeFormJsonString, final String providerId) {
    }


    public ECSyncHelper getSyncHelper() {
        return OpdLibrary.getInstance().getEcSyncHelper();
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return OpdLibrary.getInstance().context().allSharedPreferences();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return DrishtiApplication.getInstance().getClientProcessor();
    }

    public UniqueIdRepository getUniqueIdRepository() {
        return OpdLibrary.getInstance().getUniqueIdRepository();
    }

    public enum type {SAVED, UPDATED}
}