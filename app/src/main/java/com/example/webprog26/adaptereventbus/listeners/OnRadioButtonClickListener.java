package com.example.webprog26.adaptereventbus.listeners;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.TextView;
import com.example.webprog26.adaptereventbus.R;
import com.example.webprog26.adaptereventbus.managers.AppCategoryManager;
import com.example.webprog26.adaptereventbus.models.AppCategoriesModel;
import com.example.webprog26.adaptereventbus.models.AppModel;
import com.example.webprog26.adaptereventbus.models.events.AppCategoryChangedEvent;
import org.greenrobot.eventbus.EventBus;
import java.lang.ref.WeakReference;

/**
 * Created by webpr on 18.01.2017.
 */

public class OnRadioButtonClickListener implements View.OnClickListener {

    private static final String TAG = "ClickListener";

    private AppModel mAppModel;
    private AppCategoriesModel mAppCategoriesModel;
    private int mAppPosition;
    private WeakReference<Context> mContextWeakReference;
    private TextView mTvAppCategory;

    public OnRadioButtonClickListener(AppModel mAppModel, int appPosition, Context context, TextView tvAppCategory) {
        this.mAppModel = mAppModel;
        this.mAppCategoriesModel = mAppModel.getAppCategoriesModel();
        this.mAppPosition = appPosition;
        this.mContextWeakReference = new WeakReference<Context>(context);
        this.mTvAppCategory = tvAppCategory;
    }

    @Override
    public void onClick(View v) {
        AppCompatRadioButton radioButton = (AppCompatRadioButton) v;

        switch (v.getId()){
            case R.id.rbEducational:
                if(mAppCategoriesModel.isEducational()){
                    mAppCategoriesModel.setNeutral(true);
                    radioButton.setChecked(false);
                    break;
                }
                mAppCategoriesModel.setEducational(true);
                break;
            case R.id.rbForFun:
                if(mAppCategoriesModel.isForFun()){
                    mAppCategoriesModel.setNeutral(true);
                    radioButton.setChecked(false);
                    break;
                }
                mAppCategoriesModel.setForFun(true);
                break;
            case R.id.rbBlocked:
                if(mAppCategoriesModel.isBlocked()){
                    mAppCategoriesModel.setNeutral(true);
                    radioButton.setChecked(false);
                    break;
                }
                mAppCategoriesModel.setBlocked(true);
                break;
        }
        EventBus.getDefault().post(new AppCategoryChangedEvent(mAppModel, mAppPosition));
        new AppCategoryManager(mContextWeakReference.get()).setAppCategory(mAppCategoriesModel, mTvAppCategory);
    }
}
