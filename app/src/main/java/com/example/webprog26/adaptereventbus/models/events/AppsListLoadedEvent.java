package com.example.webprog26.adaptereventbus.models.events;

import com.example.webprog26.adaptereventbus.models.AppModel;

import java.util.List;

/**
 * Created by webpr on 18.01.2017.
 */

public class AppsListLoadedEvent {

    final List<AppModel> mAppModels;

    public AppsListLoadedEvent(List<AppModel> mAppModels) {
        this.mAppModels = mAppModels;
    }

    public List<AppModel> getAppModels() {
        return mAppModels;
    }
}
