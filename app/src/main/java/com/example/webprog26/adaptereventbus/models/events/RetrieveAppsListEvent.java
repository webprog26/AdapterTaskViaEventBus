package com.example.webprog26.adaptereventbus.models.events;

import android.content.pm.PackageManager;

/**
 * Created by webpr on 18.01.2017.
 */

public class RetrieveAppsListEvent {

    final PackageManager mPackageManager;

    public RetrieveAppsListEvent(PackageManager packageManager) {
        this.mPackageManager = packageManager;
    }

    public PackageManager getPackageManager() {
        return mPackageManager;
    }
}
