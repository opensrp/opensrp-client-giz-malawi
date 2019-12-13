package org.smartregister.giz;


import org.smartregister.giz.application.GizMalawiApplication;

import java.util.ArrayList;

public class TestGizMalawiApplication extends GizMalawiApplication {

    @Override
    public void onCreate() {

        setVaccineGroups(new ArrayList<>());

        super.onCreate();
        setTheme(R.style.Theme_AppCompat); //or just R.style.Theme_AppCompat
    }

    @Override
    protected void fixHardcodedVaccineConfiguration() {
        //Override
    }
}
