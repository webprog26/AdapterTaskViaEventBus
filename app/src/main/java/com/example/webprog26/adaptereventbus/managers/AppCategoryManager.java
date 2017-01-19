package com.example.webprog26.adaptereventbus.managers;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.example.webprog26.adaptereventbus.R;
import com.example.webprog26.adaptereventbus.models.AppCategoriesModel;

import java.lang.ref.WeakReference;

/**
 * Created by webpr on 18.01.2017.
 */

public class AppCategoryManager {

    private WeakReference<Context> mContextWeakReference;
    private static final String NEUTRAL_CATEGORY_TITLE_COLOR = "#898989";

    public AppCategoryManager(Context context) {
        mContextWeakReference = new WeakReference<Context>(context);
    }

    /**
     * Changes category title and it's color accordingly to the user choice
     * @param appCategoriesModel {@link AppCategoriesModel}
     * @param appCategoryTextView {@link TextView}
     */
    public void setAppCategory(AppCategoriesModel appCategoriesModel, TextView appCategoryTextView){
        if(appCategoriesModel.isNeutral()){
            appCategoryTextView.setText(mContextWeakReference.get().getResources().getString(R.string.category_neutral));
            appCategoryTextView.setTextColor(Color.parseColor(NEUTRAL_CATEGORY_TITLE_COLOR));
            return;
        }

        if(appCategoriesModel.isEducational()){
            appCategoryTextView.setText(mContextWeakReference.get().getResources().getString(R.string.category_education));
            appCategoryTextView.setTextColor(Color.BLACK);
        } else if(appCategoriesModel.isForFun()){
            appCategoryTextView.setText(mContextWeakReference.get().getResources().getString(R.string.category_fun));
            appCategoryTextView.setTextColor(Color.BLACK);
        } else if(appCategoriesModel.isBlocked()){
            appCategoryTextView.setText(mContextWeakReference.get().getResources().getString(R.string.category_blocked));
            appCategoryTextView.setTextColor(Color.BLACK);
        }
    }
}
