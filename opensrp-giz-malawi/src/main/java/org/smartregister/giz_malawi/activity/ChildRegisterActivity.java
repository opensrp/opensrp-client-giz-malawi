package org.smartregister.giz_malawi.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.child.presenter.BaseChildRegisterPresenter;
import org.smartregister.giz_malawi.R;
import org.smartregister.giz_malawi.event.LoginEvent;
import org.smartregister.giz_malawi.fragment.AdvancedSearchFragment;
import org.smartregister.giz_malawi.fragment.ChildRegisterFragment;
import org.smartregister.giz_malawi.util.GizConstants;
import org.smartregister.giz_malawi.util.GizUtils;
import org.smartregister.giz_malawi.view.NavigationMenu;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.lang.ref.WeakReference;

public class ChildRegisterActivity extends BaseChildRegisterActivity {

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = GizUtils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(GizUtils.setAppLocale(base, lang));
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseChildRegisterPresenter(this, new BaseChildRegisterModel());
    }

    @Override
    protected Fragment[] getOtherFragments() {
        ADVANCED_SEARCH_POSITION = 1;

        Fragment[] fragments = new Fragment[1];
        fragments[ADVANCED_SEARCH_POSITION - 1] = new AdvancedSearchFragment();

        return fragments;
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        WeakReference<ChildRegisterFragment> childRegisterFragmentWeakReference = new WeakReference<>(
                new ChildRegisterFragment());

        return childRegisterFragmentWeakReference.get();
    }

    @Override
    public String getRegistrationForm() {
        return GizConstants.JSON_FORM.CHILD_ENROLLMENT;
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        MenuItem clients = bottomNavigationView.getMenu().findItem(org.smartregister.child.R.id.action_clients);
        if (clients != null) {
            clients.setTitle(getString(org.smartregister.child.R.string.header_children));
        }
        bottomNavigationView.getMenu().removeItem(R.id.action_scan_qr);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }


    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void showNfcNotInstalledDialog(LoginEvent event) {
        if (event != null) {
            GizUtils.removeStickyEvent(event);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showNfcDialog();
                }
            });

        }
    }

    private void showNfcDialog() {
        GizUtils.showDialogMessage(this, R.string.nfc_sdk_missing, R.string.please_install_nfc_sdk);
    }

    public void openDrawer() {
        NavigationMenu.getInstance(this).getDrawer().openDrawer(GravityCompat.START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void startNFCCardScanner() {
        // Todo
    }

    public void refresh() {
        Intent intent = new Intent(ChildRegisterActivity.this, ChildRegisterActivity.class);
        getApplicationContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
