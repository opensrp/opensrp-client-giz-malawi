package org.smartregister.giz_malawi.interactor;

import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.giz_malawi.application.GizMalawiApplication;
import org.smartregister.giz_malawi.contract.NavigationContract;
import org.smartregister.giz_malawi.util.AppExecutors;
import org.smartregister.giz_malawi.util.DBConstants;
import org.smartregister.giz_malawi.util.GizChildDbConstants;
import org.smartregister.giz_malawi.util.GizConstants;

import java.text.MessageFormat;
import java.util.Date;

import timber.log.Timber;

public class NavigationInteractor implements NavigationContract.Interactor {

    private static NavigationInteractor instance;
    AppExecutors appExecutors = new AppExecutors();

    private NavigationInteractor() {

    }

    public static NavigationInteractor getInstance() {
        if (instance == null)
            instance = new NavigationInteractor();

        return instance;
    }

    @Override
    public Date getLastSync() {
        return null;
    }

    @Override
    public void getRegisterCount(final String tableName, final NavigationContract.InteractorCallback<Integer> callback) {
        if (callback != null) {
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Integer finalCount = getCount(tableName);
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResult(finalCount);
                            }
                        });
                    } catch (final Exception e) {
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }
                }
            });

        }
    }

    private int getCount(String tableName) {

        int count;
        Cursor c = null;
        String mainCondition;
        if (tableName.equalsIgnoreCase(GizConstants.TABLE_NAME.CHILD)) {
            mainCondition = String.format(" where %s is null AND %s", DBConstants.KEY.DATE_REMOVED,
                    GizChildDbConstants.childAgeLimitFilter());
        } else {
            mainCondition = " where 1 = 1 ";
        }
        try {

            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
            String query = MessageFormat.format("select count(*) from {0} {1}", tableName, mainCondition);
            query = sqb.Endquery(query);
            Timber.i("2%s", query);
            c = commonRepository(tableName).rawCustomQueryForAdapter(query);
            if (c.moveToFirst()) {
                count = c.getInt(0);
            } else {
                count = 0;
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }


        return count;
    }

    private CommonRepository commonRepository(String tableName) {
        return GizMalawiApplication.getInstance().getContext().commonrepository(tableName);
    }

    @Override
    public Date Sync() {
        Date res = null;
        try {
            res = new Date(getLastCheckTimeStamp());
        } catch (Exception e) {
            Timber.e(e.toString());
        }
        return res;
    }

    private Long getLastCheckTimeStamp() {
        return GizMalawiApplication.getInstance().getEcSyncHelper().getLastCheckTimeStamp();
    }

    private boolean isValidFilterForFts(CommonRepository commonRepository, String filters) {
        return commonRepository.isFts() && filters != null && !StringUtils
                .containsIgnoreCase(filters, "like") && !StringUtils
                .startsWithIgnoreCase(filters.trim(), "and ");
    }

}
