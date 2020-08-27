package org.smartregister.giz.activity;

import org.smartregister.giz.fragment.GizMaternityProfileVisitsFragment;
import org.smartregister.giz.presenter.GizMaternityProfileActivityPresenter;
import org.smartregister.maternity.activity.BaseMaternityProfileActivity;
import org.smartregister.maternity.fragment.MaternityProfileVisitsFragment;

public class GizMaternityProfileActivity extends BaseMaternityProfileActivity {

    @Override
    protected void initializePresenter() {
        presenter = new GizMaternityProfileActivityPresenter(this);
    }

    @Override
    protected MaternityProfileVisitsFragment getMaternityProfileVisitsFragment() {
        return GizMaternityProfileVisitsFragment.newInstance(this.getIntent().getExtras());
    }
}
