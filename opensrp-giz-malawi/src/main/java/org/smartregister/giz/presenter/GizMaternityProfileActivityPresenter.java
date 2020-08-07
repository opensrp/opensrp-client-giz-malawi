package org.smartregister.giz.presenter;

import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.util.GizUtils;
import org.smartregister.maternity.activity.BaseMaternityProfileActivity;
import org.smartregister.maternity.contract.MaternityProfileActivityContract;
import org.smartregister.maternity.presenter.MaternityProfileActivityPresenter;
import org.smartregister.maternity.utils.MaternityConstants;

public class GizMaternityProfileActivityPresenter extends MaternityProfileActivityPresenter {

    public GizMaternityProfileActivityPresenter(MaternityProfileActivityContract.View profileView) {
        super(profileView);
    }

    @Override
    public boolean hasAncProfile() {
        BaseMaternityProfileActivity activity = (BaseMaternityProfileActivity) getProfileView();
        CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) activity.getIntent()
                .getSerializableExtra(MaternityConstants.IntentKey.CLIENT_OBJECT);
        return GizMalawiApplication.getInstance().gizEventRepository()
                .hasEvent(commonPersonObjectClient.getCaseId(),
                        ConstantsUtils.EventTypeUtils.ANC_MATERNITY_TRANSFER);
    }

    @Override
    public void openAncProfile() {
        BaseMaternityProfileActivity activity = (BaseMaternityProfileActivity) getProfileView();
        CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) activity.getIntent()
                .getSerializableExtra(MaternityConstants.IntentKey.CLIENT_OBJECT);
        commonPersonObjectClient.getColumnmaps().put("maternity_history","");
        GizUtils.openAncProfilePage(commonPersonObjectClient, activity);
    }
}
