package org.smartregister.giz.presenter;

import android.database.Cursor;
import android.database.CursorJoiner;

import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.giz.cursor.CreateRemoteLocalCursor;
import org.smartregister.giz.model.AdvancedSearchModel;
import org.smartregister.giz.util.DBConstants;
import org.smartregister.giz.util.DBQueryHelper;

/**
 * Created by ndegwamartin on 11/04/2019.
 */
public class AdvancedSearchPresenter extends BaseChildAdvancedSearchPresenter {
    public AdvancedSearchPresenter(ChildAdvancedSearchContract.View view, String viewConfigurationIdentifier) {
        super(view, viewConfigurationIdentifier, new AdvancedSearchModel());
    }

    @Override
    protected AdvancedMatrixCursor getRemoteLocalMatrixCursor(AdvancedMatrixCursor matrixCursor) {
        String query = getView().filterAndSortQuery();
        Cursor cursor = getView().getRawCustomQueryForAdapter(query);
        if (cursor != null && cursor.getCount() > 0) {
            AdvancedMatrixCursor remoteLocalCursor = new AdvancedMatrixCursor(
                    new String[] {DBConstants.KEY.ID_LOWER_CASE, DBConstants.KEY.RELATIONALID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME, DBConstants.KEY.DOB, DBConstants.KEY.MER_ID});

            CursorJoiner joiner = new CursorJoiner(matrixCursor,
                    new String[] {DBConstants.KEY.MER_ID, DBConstants.KEY.ID_LOWER_CASE}, cursor,
                    new String[] {DBConstants.KEY.MER_ID, DBConstants.KEY.ID_LOWER_CASE});
            for (CursorJoiner.Result joinerResult : joiner) {
                switch (joinerResult) {
                    case BOTH:
                        CreateRemoteLocalCursor createRemoteLocalCursor = new CreateRemoteLocalCursor(matrixCursor, true);
                        remoteLocalCursor
                                .addRow(new Object[] {createRemoteLocalCursor.getId(), createRemoteLocalCursor.getRelationalId(),
                                        createRemoteLocalCursor.getFirstName(), createRemoteLocalCursor.getLastName(), createRemoteLocalCursor.getDob(), createRemoteLocalCursor.getOpenSrpId()});
                        break;
                    case RIGHT:
                        CreateRemoteLocalCursor localCreateRemoteLocalCursor = new CreateRemoteLocalCursor(cursor, false);
                        remoteLocalCursor
                                .addRow(new Object[] {localCreateRemoteLocalCursor.getId(), localCreateRemoteLocalCursor.getRelationalId(),
                                        localCreateRemoteLocalCursor.getFirstName(), localCreateRemoteLocalCursor.getLastName(), localCreateRemoteLocalCursor.getDob(), localCreateRemoteLocalCursor.getOpenSrpId()});

                        break;
                    case LEFT:
                        createRemoteLocalCursor = new CreateRemoteLocalCursor(matrixCursor, true);
                        remoteLocalCursor
                                .addRow(new Object[] {createRemoteLocalCursor.getId(), createRemoteLocalCursor.getRelationalId(),
                                        createRemoteLocalCursor.getFirstName(), createRemoteLocalCursor.getLastName(), createRemoteLocalCursor.getDob(), createRemoteLocalCursor.getOpenSrpId()});
                        break;
                    default:
                        break;
                }
            }

            cursor.close();
            matrixCursor.close();
            return remoteLocalCursor;
        } else {
            return matrixCursor;
        }
    }

    @Override
    public String getMainCondition() {
        return DBQueryHelper.getHomeRegisterCondition();
    }
}
