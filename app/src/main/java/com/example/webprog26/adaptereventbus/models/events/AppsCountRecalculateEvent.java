package com.example.webprog26.adaptereventbus.models.events;

import com.example.webprog26.adaptereventbus.models.AppsCategoriesCounter;

/**
 * Created by webpr on 18.01.2017.
 */

public class AppsCountRecalculateEvent {

    private final AppsCategoriesCounter mAppsCategoriesCounter;

    public AppsCountRecalculateEvent(AppsCategoriesCounter appsCategoriesCounter) {
        this.mAppsCategoriesCounter = appsCategoriesCounter;
    }

    public AppsCategoriesCounter getAppsCategoriesCounter() {
        return mAppsCategoriesCounter;
    }
}
