package org.smartregister.giz;


import org.smartregister.giz.application.GizMalawiApplication;

public class TestGizMalawiApplication extends GizMalawiApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.Theme_AppCompat); //or just R.style.Theme_AppCompat
    }
}
