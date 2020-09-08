package org.smartregister.giz.fragment;

import org.smartregister.giz.configuration.AllClientsRegisterQueryProvider;
import org.smartregister.opd.utils.ConfigurationInstancesHelper;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-17
 */

public class AllClientsRegisterFragment extends OpdRegisterFragment {

    public AllClientsRegisterFragment() {
        super();
        setOpdRegisterQueryProvider(ConfigurationInstancesHelper.newInstance(AllClientsRegisterQueryProvider.class));
    }
}