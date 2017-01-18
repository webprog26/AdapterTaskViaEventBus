package com.example.webprog26.adaptereventbus.models;


/**
 * Created by webpr on 18.01.2017.
 */

public class AppCategoriesModel {

    private boolean isNeutral;
    private boolean isEducational;
    private boolean isForFun;
    private boolean isBlocked;

    public boolean isNeutral() {
        return isNeutral;
    }

    public void setNeutral(boolean neutral) {
        if(neutral){
            setEducational(false);
            setForFun(false);
            setBlocked(false);
        }
        isNeutral = neutral;
    }

    public boolean isEducational() {
        return isEducational;
    }

    public void setEducational(boolean educational) {
        isEducational = educational;
    }

    public boolean isForFun() {
        return isForFun;
    }

    public void setForFun(boolean forFun) {
        isForFun = forFun;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
