package com.example.webprog26.adaptereventbus.models;

import android.graphics.Bitmap;

/**
 * Created by webpr on 18.01.2017.
 */

public class AppModel {

    private String mAppLabel;
    private Bitmap mAppIcon;
    private boolean hasCategory = false;
    private AppCategoriesModel mAppCategoriesModel;

    public AppModel() {
        this.mAppCategoriesModel = new AppCategoriesModel();
        mAppCategoriesModel.setNeutral(true);
    }

    public String getAppLabel() {
        return mAppLabel;
    }

    public void setAppLabel(String mAppLabel) {
        this.mAppLabel = mAppLabel;
    }

    public Bitmap getAppIcon() {
        return mAppIcon;
    }

    public void setAppIcon(Bitmap mAppIcon) {
        this.mAppIcon = mAppIcon;
    }

    public boolean isHasCategory() {
        if(mAppCategoriesModel.isNeutral()){
            mAppCategoriesModel.setNeutral(false);
        }
        return hasCategory;
    }

    public void setHasCategory(boolean hasCategory) {
        this.hasCategory = hasCategory;
    }

    public AppCategoriesModel getAppCategoriesModel() {
        return mAppCategoriesModel;
    }

    public void setAppCategoriesModel(AppCategoriesModel appCategoriesModel) {
        this.mAppCategoriesModel = appCategoriesModel;
    }

    @Override
    public String toString() {
        return "App \"" + getAppLabel() + "\" with icon " + getAppIcon() + "\n"
                + "hasCategory  " + isHasCategory() + "\n"
                + "neutral " + mAppCategoriesModel.isNeutral() + "\n"
                + "educational " + mAppCategoriesModel.isEducational() + "\n"
                + "for fun " + mAppCategoriesModel.isForFun() + "\n"
                + "blocked " + mAppCategoriesModel.isBlocked();
    }
}
