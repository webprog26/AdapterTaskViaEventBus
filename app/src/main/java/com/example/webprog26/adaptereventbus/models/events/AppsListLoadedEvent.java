package com.example.webprog26.adaptereventbus.models.events;

import com.example.webprog26.adaptereventbus.models.AppModel;
import com.example.webprog26.adaptereventbus.models.AppsCategoriesCounter;

import java.util.List;

/**
 * Created by webpr on 18.01.2017.
 */

public class AppsListLoadedEvent {

    final List<AppModel> mAppModels;
    final AppsCategoriesCounter mAppsCategoriesCounter;

    public AppsListLoadedEvent(List<AppModel> mAppModels, AppsCategoriesCounter appsCategoriesCounter) {
        this.mAppModels = mAppModels;
        this.mAppsCategoriesCounter = appsCategoriesCounter;
    }

    public List<AppModel> getAppModels() {
        return mAppModels;
    }

    public AppsCategoriesCounter getAppsCategoriesCounter() {
        return mAppsCategoriesCounter;
    }
}
