package com.example.webprog26.adaptereventbus.providers;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;

import com.example.webprog26.adaptereventbus.db.DbHelper;
import com.example.webprog26.adaptereventbus.managers.DrawableToBitmapConverter;
import com.example.webprog26.adaptereventbus.models.AppCategoriesModel;
import com.example.webprog26.adaptereventbus.models.AppModel;
import com.example.webprog26.adaptereventbus.models.AppsCategoriesCounter;
import com.example.webprog26.adaptereventbus.models.events.AppsCountRecalculateEvent;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by webpr on 18.01.2017.
 */

public class MyAppsProvider {

    private static final String TAG = "MyAppsProvider";

    private DbHelper mDbHelper;

    public MyAppsProvider(Context context) {
        this.mDbHelper = new DbHelper(context);
    }

    /**
     * Returns {@link List} of {@link AppModel} contains icons and labels
     * of all the applications installed on the device
     * @param packageManager {@link PackageManager}
     * @return List<AppModel>
     */
    public List<AppModel> getAppModelList(PackageManager packageManager){
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

    /**
     * Returns {@link List} of {@link ResolveInfo}
     * with information about all the applications installed on the device
     * @param packageManager {@link PackageManager}
     * @return List<ResolveInfo>
     */
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

    /**
     * Checks if app is already saved in the local database on the device
     * @param appLabel {@link String}
     * @return boolean
     */
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

    /**
     * Returns {@link AppCategoriesModel} instance filled  with data for specific {@link AppModel} from database
     * @param appLabel {@link String}
     * @return AppCategoriesModel
     */
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

    /**
     * Inserts new {@link AppModel} to database or updates existing one
     * @param model {@link AppModel}
     */
    public void saveAppCategory(AppModel model){
        AppCategoriesModel appCategoriesModel = model.getAppCategoriesModel();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.IS_EDUCATIONAL, String.valueOf(appCategoriesModel.isEducational()));
        contentValues.put(DbHelper.IS_FOR_FUN, String.valueOf(appCategoriesModel.isForFun()));
        contentValues.put(DbHelper.IS_BLOCKED, String.valueOf(appCategoriesModel.isBlocked()));

        if(isMatching(model.getAppLabel())){
            String whereClause = DbHelper.APP_NAME + " = ?";
            String[] whereArgs = new String[]{model.getAppLabel()};

            mDbHelper.getWritableDatabase().update(DbHelper.TABLE_NAME, contentValues, whereClause, whereArgs);
            EventBus.getDefault().post(new AppsCountRecalculateEvent(getAppsCategoriesCounter()));
            return;
        }
        contentValues.put(DbHelper.APP_NAME, model.getAppLabel());
        mDbHelper.getWritableDatabase().insert(DbHelper.TABLE_NAME, null, contentValues);
        EventBus.getDefault().post(new AppsCountRecalculateEvent(getAppsCategoriesCounter()));
    }

    /**
     * Deletes application info from database if category set to neutral. This could be useful to avoid growing of database
     * @param appModel {@link AppModel}
     */
    public void deleteAppFromDatabase(AppModel appModel){
        String whereClause = DbHelper.APP_NAME + " = ?";
        String[] whereArgs = new String[]{appModel.getAppLabel()};

        mDbHelper.getWritableDatabase().delete(DbHelper.TABLE_NAME, whereClause, whereArgs);
        EventBus.getDefault().post(new AppsCountRecalculateEvent(getAppsCategoriesCounter()));
    }

    /**
     * Returns number of apps whom category has been set to education
     * @return int
     */
    private int getEducationalCount(){
        int count = 0;

        String whereClause = DbHelper.IS_EDUCATIONAL + " = ?";
        String[] whereArgs = new String[]{String.valueOf(true)};

        Cursor cursor = mDbHelper.getReadableDatabase().query(DbHelper.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        while(cursor.moveToNext()){
            count++;
        }

        return count;
    }

    /**
     * Returns number of apps whom category has been set to fun
     * @return int
     */
    private int getForFunCount(){
        int count = 0;

        String whereClause = DbHelper.IS_FOR_FUN + " = ?";
        String[] whereArgs = new String[]{String.valueOf(true)};

        Cursor cursor = mDbHelper.getReadableDatabase().query(DbHelper.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        while(cursor.moveToNext()){
            count++;
        }

        return count;
    }

    /**
     * Returns number of apps whom category has been set to blocked
     * @return int
     */
    private int getBlockedCount(){
        int count = 0;

        String whereClause = DbHelper.IS_BLOCKED + " = ?";
        String[] whereArgs = new String[]{String.valueOf(true)};

        Cursor cursor = mDbHelper.getReadableDatabase().query(DbHelper.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        while(cursor.moveToNext()){
            count++;
        }

        return count;
    }

    /**
     * Returns AppCategoriesModel instance filled with data about apps counted by category
     * @return AppCategoriesModel
     */
    public AppsCategoriesCounter getAppsCategoriesCounter(){
        AppsCategoriesCounter categoriesCounter = new AppsCategoriesCounter();

        categoriesCounter.setEducationalCount(getEducationalCount());
        categoriesCounter.setForFunCount(getForFunCount());
        categoriesCounter.setBlockedCount(getBlockedCount());

        return categoriesCounter;
    }
}
