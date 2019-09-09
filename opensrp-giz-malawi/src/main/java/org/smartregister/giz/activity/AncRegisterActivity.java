package org.smartregister.giz.activity;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.giz.R;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.view.NavigationMenu;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-09
 */

public class AncRegisterActivity extends BaseHomeRegisterActivity {

    private ImageView openNavBtn;
    private ImageView addPatientBtn;

    @Override
    protected void registerBottomNavigation() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)this.findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setSelectedBottomBarMenuItem(int itemId) {
        // Do nothing here
    }

    @Override
    protected void onResumption() {
        super.onResumption();

        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    protected void onCreation() {
        super.onCreation();

        openNavBtn = findViewById(R.id.toolbarBackButton);
        addPatientBtn = findViewById(R.id.add_child_image_view);

        openNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDrawer();
            }
        });

        addPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistration();
            }
        });
    }

    public void createDrawer() {
        NavigationMenu navigationMenu = NavigationMenu.getInstance(this, null, null);
        navigationMenu.getNavigationAdapter().setSelectedView(GizConstants.DrawerMenu.ANC_CLIENTS);
        //navigationMenu.runRegisterCount();
    }
}
