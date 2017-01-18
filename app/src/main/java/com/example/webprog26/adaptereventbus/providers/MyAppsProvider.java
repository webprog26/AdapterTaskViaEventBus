package com.example.webprog26.adaptereventbus.providers;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.util.Log;

import com.example.webprog26.adaptereventbus.db.DbHelper;
import com.example.webprog26.adaptereventbus.managers.DrawableToBitmapConverter;
import com.example.webprog26.adaptereventbus.models.AppCategoriesModel;
import com.example.webprog26.adaptereventbus.models.AppModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by webpr on 18.01.2017.
 */

public class MyAppsProvider {

    private static final String TAG = "MyAppsProvider";

    private Context mContext;
    private DbHelper mDbHelper;

    public MyAppsProvider(Context context) {
        this.mContext = context;
        this.mDbHelper = new DbHelper(context);
    }

    public List<AppModel> getAppModelList(PackageManager packageManager){
        Log.i("MainActivity_TAG", "thread " + Thread.currentThread().getName());
        List<AppModel> appModels = new ArrayList<>();
        for(ResolveInfo resolveInfo: getLauncherActivitiesList(packageManager)){
            String appLabel = resolveInfo.loadLabel(packageManager).toString();

            AppModel appModel = new AppModel();
            appModel.setAppLabel(appLabel);
            appModel.setAppIcon(DrawableToBitmapConverter.drawableToBitmap(resolveInfo.loadIcon(packageManager)));

            if(isMatching(appLabel)){
                appModel.setAppCategoriesModel(getAppCategoriesModel(appLabel));
            }

            appModels.add(appModel);
        }
        return appModels;
    }

    private List<ResolveInfo> getLauncherActivitiesList(final PackageManager packageManager){
        Intent getAppsIntent = new Intent(Intent.ACTION_MAIN);
        getAppsIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> activities = packageManager.queryIntentActivities(getAppsIntent, 0);

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                return String.CASE_INSENSITIVE_ORDER
                        .compare(
                                a.loadLabel(packageManager).toString(),
                                b.loadLabel(packageManager).toString()
                        );
            }
        });

        return activities;
    }

    private boolean isMatching(String appLabel){
        String whereClause = DbHelper.APP_NAME + " = ?";
        String[] whereArgs = new String[]{appLabel};

        long id = 0;

        Cursor cursor = mDbHelper.getReadableDatabase().query(DbHelper.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        while (cursor.moveToNext()){
            id = cursor.getLong(cursor.getColumnIndex(DbHelper.APP_ID));
        }

        return id != 0;
    }

    public AppCategoriesModel getAppCategoriesModel(String appLabel){

        AppCategoriesModel appCategoriesModel = new AppCategoriesModel();

        String whereClause = DbHelper.APP_NAME + " = ?";
        String[] whereArgs = new String[]{appLabel};

        Cursor cursor = mDbHelper.getReadableDatabase().query(DbHelper.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        while (cursor.moveToNext()){
            appCategoriesModel.setEducational(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DbHelper.IS_EDUCATIONAL))));
            appCategoriesModel.setForFun(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DbHelper.IS_FOR_FUN))));
            appCategoriesModel.setBlocked(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DbHelper.IS_BLOCKED))));
        }

        return appCategoriesModel;
    }

    public void saveAppCategory(AppModel model){
        Log.i(TAG, "saving to db " + model.toString());
        AppCategoriesModel appCategoriesModel = model.getAppCategoriesModel();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.IS_EDUCATIONAL, String.valueOf(appCategoriesModel.isEducational()));
        contentValues.put(DbHelper.IS_FOR_FUN, String.valueOf(appCategoriesModel.isForFun()));
        contentValues.put(DbHelper.IS_BLOCKED, String.valueOf(appCategoriesModel.isBlocked()));

        if(isMatching(model.getAppLabel())){
            String whereClause = DbHelper.APP_NAME + " = ?";
            String[] whereArgs = new String[]{model.getAppLabel()};

            mDbHelper.getWritableDatabase().update(DbHelper.TABLE_NAME, contentValues, whereClause, whereArgs);
            return;
        }
        contentValues.put(DbHelper.APP_NAME, model.getAppLabel());
        mDbHelper.getWritableDatabase().insert(DbHelper.TABLE_NAME, null, contentValues);
    }

    public void deleteAppFromDatabase(AppModel appModel){
        String whereClause = DbHelper.APP_NAME + " = ?";
        String[] whereArgs = new String[]{appModel.getAppLabel()};

        mDbHelper.getWritableDatabase().delete(DbHelper.TABLE_NAME, whereClause, whereArgs);
    }
}
