package com.example.webprog26.adaptereventbus.managers;

import android.content.Context;
import android.widget.TextView;

import com.example.webprog26.adaptereventbus.R;
import com.example.webprog26.adaptereventbus.models.AppCategoriesModel;

import java.lang.ref.WeakReference;

/**
 * Created by webpr on 18.01.2017.
 */

public class AppCategoryManager {

    private WeakReference<Context> mContextWeakReference;

    public AppCategoryManager(Context context) {
        mContextWeakReference = new WeakReference<Context>(context);
    }

    public void setAppCategory(AppCategoriesModel appCategoriesModel, TextView appCategoryTextView){
        if(appCategoriesModel.isNeutral()){
            appCategoryTextView.setText(mContextWeakReference.get().getResources().getString(R.string.category_neutral));
            return;
        }

        if(appCategoriesModel.isEducational()){
            appCategoryTextView.setText(mContextWeakReference.get().getResources().getString(R.string.category_education));
        } else if(appCategoriesModel.isForFun()){
            appCategoryTextView.setText(mContextWeakReference.get().getResources().getString(R.string.category_fun));
        } else if(appCategoriesModel.isBlocked()){
            appCategoryTextView.setText(mContextWeakReference.get().getResources().getString(R.string.category_blocked));
        }
    }
}
