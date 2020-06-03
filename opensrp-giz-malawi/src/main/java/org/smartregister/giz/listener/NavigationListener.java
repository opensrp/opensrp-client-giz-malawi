package org.smartregister.giz.listener;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import org.smartregister.anc.library.AncLibrary;
import org.smartregister.giz.activity.ChildRegisterActivity;
import org.smartregister.giz.activity.MaternityRegisterActivity;
import org.smartregister.giz.activity.OpdRegisterActivity;
import org.smartregister.giz.adapter.NavigationAdapter;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.view.NavDrawerActivity;
import org.smartregister.giz.view.NavigationMenu;

public class NavigationListener implements View.OnClickListener {

    private Activity activity;
    private NavigationAdapter navigationAdapter;

    public NavigationListener(Activity activity, NavigationAdapter adapter) {
        this.activity = activity;
        this.navigationAdapter = adapter;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null && v.getTag() instanceof String) {
            String tag = (String) v.getTag();

            switch (tag) {
                case GizConstants.DrawerMenu.CHILD_CLIENTS:
                    navigateToActivity(ChildRegisterActivity.class);
                    break;

                case GizConstants.DrawerMenu.ALL_CLIENTS:
                case GizConstants.DrawerMenu.OPD_CLIENTS:
                    navigateToActivity(OpdRegisterActivity.class);
                    break;

                case GizConstants.DrawerMenu.ANC_CLIENTS:
                    navigateToActivity(AncLibrary.getInstance().getActivityConfiguration().getHomeRegisterActivityClass());
                    break;

                case GizConstants.DrawerMenu.MATERNITY_CLIENTS:
                    navigateToActivity(MaternityRegisterActivity.class);
                    break;

                default:
                    break;
            }
            navigationAdapter.setSelectedView(tag);
        }
    }

    private void navigateToActivity(@NonNull Class<?> clas) {
        NavigationMenu.closeDrawer();

        if (activity instanceof NavDrawerActivity) {
            ((NavDrawerActivity) activity).finishActivity();
        } else {
            activity.finish();
        }

        activity.startActivity(new Intent(activity, clas));
    }
}
