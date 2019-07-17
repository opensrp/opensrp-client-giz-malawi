package org.smartregister.giz.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import org.smartregister.giz.R;
import org.smartregister.giz.adapter.NavigationAdapter;
import org.smartregister.giz.util.GizConstants;

public class NavigationListener implements View.OnClickListener {

    private Activity activity;
    private NavigationAdapter navigationAdapter;

    public NavigationListener(Activity activity, NavigationAdapter adapter) {
        this.activity = activity;
        this.navigationAdapter = adapter;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            if (v.getTag() instanceof String) {
                String tag = (String) v.getTag();

                switch (tag) {
                    case GizConstants.DrawerMenu.CHILD_CLIENTS:
                        break;
                    case GizConstants.DrawerMenu.ALL_FAMILIES:
                        break;
                    case GizConstants.DrawerMenu.ANC:
                        break;
                    case GizConstants.DrawerMenu.LD:
                        Toast.makeText(activity.getApplicationContext(), GizConstants.DrawerMenu.LD, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case GizConstants.DrawerMenu.PNC:
                        Toast.makeText(activity.getApplicationContext(), GizConstants.DrawerMenu.PNC, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case GizConstants.DrawerMenu.FAMILY_PLANNING:
                        Toast.makeText(activity.getApplicationContext(), GizConstants.DrawerMenu.FAMILY_PLANNING,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case GizConstants.DrawerMenu.MALARIA:
                        Toast.makeText(activity.getApplicationContext(), GizConstants.DrawerMenu.MALARIA, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    default:
                        break;
                }
                navigationAdapter.setSelectedView(tag);
            }
        }
    }

    private void startRegisterActivity(Class registerClass) {
        Intent intent = new Intent(activity, registerClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        activity.finish();
    }
}
