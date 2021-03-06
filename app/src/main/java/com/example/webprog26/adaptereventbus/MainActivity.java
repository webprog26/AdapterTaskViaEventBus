package com.example.webprog26.adaptereventbus;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.webprog26.adaptereventbus.adapters.AppsListAdapter;
import com.example.webprog26.adaptereventbus.models.AppModel;
import com.example.webprog26.adaptereventbus.models.AppsCategoriesCounter;
import com.example.webprog26.adaptereventbus.models.events.AppCategoryChangedEvent;
import com.example.webprog26.adaptereventbus.models.events.AppsCountRecalculateEvent;
import com.example.webprog26.adaptereventbus.models.events.AppsListLoadedEvent;
import com.example.webprog26.adaptereventbus.models.events.RetrieveAppsListEvent;
import com.example.webprog26.adaptereventbus.models.events.SaveAppToDatabaseEvent;
import com.example.webprog26.adaptereventbus.providers.MyAppsProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_TAG";
    private MyAppsProvider mMyAppsProvider;
    private AppsListAdapter mAdapter;


    private TextView mTvTotalAppsCount, mTvEducationalAppsCount, mTvBlockedAppsCount, mTvForFunAppsCount;
    private ProgressBar mPbLoadingInProgress;
    private RecyclerView mRvAppsList;
    private LinearLayout mCountersLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.page_title));

        mMyAppsProvider = new MyAppsProvider(getApplicationContext());

        mPbLoadingInProgress = (ProgressBar) findViewById(R.id.pbLoadingInProgress);

        mCountersLayout = (LinearLayout) findViewById(R.id.countersLayout);

        mRvAppsList = (RecyclerView) findViewById(R.id.rwAppsList);
        mRvAppsList.setHasFixedSize(true);
        mRvAppsList.setItemAnimator(new DefaultItemAnimator());
        mRvAppsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mTvTotalAppsCount = (TextView) findViewById(R.id.tvTotalAppsCount);
        mTvEducationalAppsCount = (TextView) findViewById(R.id.tvEducationalAppsCount);
        mTvBlockedAppsCount = (TextView) findViewById(R.id.tvBlockedlAppsCount);
        mTvForFunAppsCount = (TextView) findViewById(R.id.tvForFunAppsCount);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Registering EventBus instance
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Sending request for list of AppModel
        EventBus.getDefault().post(new RetrieveAppsListEvent(getPackageManager()));
    }

    /**
     * Retrieving list of AppModel from {@link MyAppsProvider} asynchronously
     * @param retrieveAppsListEvent {@link RetrieveAppsListEvent}
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRetrieveAppsListEvent(RetrieveAppsListEvent retrieveAppsListEvent){
        Log.i(TAG, "onRetrieveAppsListEvent");
        AppsListLoadedEvent appsListLoadedEvent = new AppsListLoadedEvent(mMyAppsProvider
                .getAppModelList(retrieveAppsListEvent
                        .getPackageManager()), mMyAppsProvider.getAppsCategoriesCounter());
        EventBus.getDefault().post(appsListLoadedEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppsListLoadedEvent(AppsListLoadedEvent appsListLoadedEvent){
        if(mPbLoadingInProgress.getVisibility() == View.VISIBLE){
            mPbLoadingInProgress.setVisibility(View.GONE);
        }
        List<AppModel> appModels = appsListLoadedEvent.getAppModels();
        mAdapter = new AppsListAdapter(MainActivity.this, appModels);
        mRvAppsList.setAdapter(mAdapter);

        AppsCategoriesCounter counter = appsListLoadedEvent.getAppsCategoriesCounter();
        mTvTotalAppsCount.setText(getResources().getString(R.string.total_count, appModels.size()));
        mTvEducationalAppsCount.setText(getResources().getString(R.string.educational_count, counter.getEducationalCount()));
        mTvBlockedAppsCount.setText(getResources().getString(R.string.blocked_count, counter.getBlockedCount()));
        mTvForFunAppsCount.setText(getResources().getString(R.string.for_fun_count, counter.getForFunCount()));

        if(mCountersLayout.getVisibility() == View.GONE){
            mCountersLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * App category has been updated, we may ask {@link AppsListAdapter} to
     * update specific row by giving him this row's position, included in {@link AppCategoryChangedEvent}
     * @param appCategoryChangedEvent {@link AppCategoryChangedEvent}
     */
    @Subscribe
    public void onAppCategoryChangedEvent(AppCategoryChangedEvent appCategoryChangedEvent){
        EventBus.getDefault().post(new SaveAppToDatabaseEvent(appCategoryChangedEvent.getAppModel()));
        mAdapter.updateList(appCategoryChangedEvent.getPosition());
    }

    /**
     * App category has been updated, we should ask {@link MyAppsProvider} to save it it the database
     * or update existing one asynchronously
     * @param saveAppToDatabaseEvent {@link SaveAppToDatabaseEvent}
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSaveAppToDatabaseEvent(SaveAppToDatabaseEvent saveAppToDatabaseEvent){
        if(saveAppToDatabaseEvent.getAppModel().getAppCategoriesModel().isNeutral()){
            mMyAppsProvider.deleteAppFromDatabase(saveAppToDatabaseEvent.getAppModel());
            return;
        }
        mMyAppsProvider.saveAppCategory(saveAppToDatabaseEvent.getAppModel());
    }

    /**
     * Apps count by categories has been changed, so we must update UI with the new numbers
     * included in {@link AppsCountRecalculateEvent}
     * @param appsCountRecalculateEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppsCountRecalculateEvent(AppsCountRecalculateEvent appsCountRecalculateEvent){
        AppsCategoriesCounter counter = appsCountRecalculateEvent.getAppsCategoriesCounter();
        mTvEducationalAppsCount.setText(getResources().getString(R.string.educational_count, counter.getEducationalCount()));
        mTvBlockedAppsCount.setText(getResources().getString(R.string.blocked_count, counter.getBlockedCount()));
        mTvForFunAppsCount.setText(getResources().getString(R.string.for_fun_count, counter.getForFunCount()));
    }

    @Override
    protected void onStop() {
        //Unregistering EventBus instance
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
