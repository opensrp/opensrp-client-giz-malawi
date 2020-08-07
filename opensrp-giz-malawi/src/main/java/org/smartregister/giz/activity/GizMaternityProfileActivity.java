package org.smartregister.giz.activity;

import org.smartregister.giz.presenter.GizMaternityProfileActivityPresenter;
import org.smartregister.maternity.activity.BaseMaternityProfileActivity;

public class GizMaternityProfileActivity extends BaseMaternityProfileActivity {

    @Override
    protected void initializePresenter() {
        presenter = new GizMaternityProfileActivityPresenter(this);
    }
}
