package org.smartregister.giz.activity;

import android.content.Intent;
import android.os.Bundle;

import org.smartregister.anc.library.activity.SiteCharacteristicsEnterActivity;
import org.smartregister.giz.R;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.presenter.LoginPresenter;
import org.smartregister.giz.util.DBQueryHelper;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.util.GizUtils;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import timber.log.Timber;

public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()) {
            goToHome(false);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    public void goToHome(boolean remote) {
        if (remote) {
            org.smartregister.util.Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        }

        if (mLoginPresenter.isServerSettingsSet()) {
            Intent intent = new Intent(this, ChildRegisterActivity.class);
            intent.putExtra(GizConstants.IntentKeyUtil.IS_REMOTE_LOGIN, remote);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SiteCharacteristicsEnterActivity.class);
            intent.putExtra(GizConstants.IntentKeyUtil.IS_REMOTE_LOGIN, remote);
            startActivity(intent);
        }
        try {
            DBQueryHelper.removeIndicators(
                    GizMalawiApplication
                            .getInstance()
                            .context().getEventClientRepository().getWritableDatabase());
        } catch (Exception e) {
            Timber.e(e);
        }
        finish();
    }

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = GizUtils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(GizUtils.setAppLocale(base, lang));
    }
}
