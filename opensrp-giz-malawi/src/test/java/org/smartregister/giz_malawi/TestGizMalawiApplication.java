package org.smartregister.giz_malawi;


import org.smartregister.giz_malawi.application.GizMalawiApplication;

public class TestGizMalawiApplication extends GizMalawiApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.Theme_AppCompat); //or just R.style.Theme_AppCompat
    }
}
