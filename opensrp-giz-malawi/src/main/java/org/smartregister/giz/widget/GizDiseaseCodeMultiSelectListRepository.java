package org.smartregister.giz.widget;

import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.interfaces.MultiSelectListRepository;

import org.smartregister.domain.Setting;
import org.smartregister.giz.util.GizOpdMultiSelectListRepositoryHelper;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.utils.OpdConstants;

import java.util.List;

import timber.log.Timber;

public class GizDiseaseCodeMultiSelectListRepository implements MultiSelectListRepository {

    @Override
    public List<MultiSelectItem> fetchData() {
        try {
            Setting setting = OpdLibrary.getInstance().context().allSettings().getSetting(OpdConstants.SettingsConfig.OPD_DISEASE_CODES);
            GizOpdMultiSelectListRepositoryHelper gizOpdMultiSelectListRepositoryHelper = new GizOpdMultiSelectListRepositoryHelper();
            gizOpdMultiSelectListRepositoryHelper.fetchHelperData(setting);
            return null;
        } catch (Exception e) {
            Timber.e(e, "settings are null");
            return null;
        }
    }
}
