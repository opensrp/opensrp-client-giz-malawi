package org.smartregister.giz.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.giz.task.OpdTransferTask;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.opd.activity.BaseOpdProfileActivity;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.util.Utils;

import java.util.Map;

public class GizOpdProfileActivity extends BaseOpdProfileActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(org.smartregister.opd.R.menu.menu_opd_profile_activity, menu);

        if (GizConstants.RegisterType.OPD.equalsIgnoreCase(getRegisterType())) {
            MenuItem closeMenu = menu.findItem(org.smartregister.opd.R.id.opd_menu_item_close_client);
            if (closeMenu != null) {
                closeMenu.setEnabled(true);
            }
            buildAllClientsMenu(menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if (id == R.id.opd_menu_item_enrol_anc) {
            showOpdTransferDialog(GizConstants.EventType.OPD_ANC_TRANSFER);
        } else if (id == R.id.opd_menu_item_enrol_pnc) {
            showOpdTransferDialog(GizConstants.EventType.OPD_PNC_TRANSFER);
        } else if (id == R.id.opd_menu_item_enrol_maternity) {
            showOpdTransferDialog(GizConstants.EventType.OPD_MATERNITY_TRANSFER);
        }

        return true;
    }

    public void showOpdTransferDialog(@Nullable String eventType) {
        if (StringUtils.isNotBlank(eventType)) {
            AlertDialog dialog = new AlertDialog.Builder(GizOpdProfileActivity.this, R.style.AppThemeAlertDialog)
                    .setCancelable(true)
                    .setMessage(R.string.opd_enrol_client_message)
                    .setPositiveButton("Yes", (dialog12, which) -> new OpdTransferTask(GizOpdProfileActivity.this, eventType, getClient().getCaseId()).execute())
                    .setNegativeButton("no", (dialog1, which) -> {
                        dialog1.dismiss();
                    }).create();

            dialog.show();
        }
    }

    public void buildAllClientsMenu(@NonNull Menu menu) {
        CommonPersonObjectClient client = getClient();
        Map<String, String> detailsMap = client.getDetails();
        String dob = detailsMap.get(OpdDbConstants.Column.Client.DOB);
        String gender = detailsMap.get(OpdDbConstants.Column.Client.GENDER);
        if (StringUtils.isNotBlank(dob)) {
            DateTime age = Utils.dobStringToDateTime(dob);
            if (age != null) {
                int years = Years.yearsBetween(age.toLocalDate(), new DateTime().toLocalDate()).getYears();
                if (years >= 10 && StringUtils.isNotBlank(gender) && gender.toLowerCase().startsWith("f")) {
                    MenuItem maternityMenuItem = menu.findItem(R.id.opd_menu_item_enrol_maternity);
                    maternityMenuItem.setVisible(true);

                    MenuItem ancMenuItem = menu.findItem(R.id.opd_menu_item_enrol_anc);
                    ancMenuItem.setVisible(true);

                    MenuItem pncMenuItem = menu.findItem(R.id.opd_menu_item_enrol_pnc);
                    pncMenuItem.setVisible(true);
                }
            }
        }

    }
}
