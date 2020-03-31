package org.smartregister.giz.interactor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.domain.UniqueId;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.interactor.BaseMaternityRegisterActivityInteractor;
import org.smartregister.opd.contract.OpdRegisterActivityContract;

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
}
