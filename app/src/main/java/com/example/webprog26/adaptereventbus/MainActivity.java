package com.example.webprog26.adaptereventbus;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.webprog26.adaptereventbus.adapters.AppsListAdapter;
import com.example.webprog26.adaptereventbus.models.events.AppCategoryChangedEvent;
import com.example.webprog26.adaptereventbus.models.events.AppsListLoadedEvent;
import com.example.webprog26.adaptereventbus.models.events.RetrieveAppsListEvent;
import com.example.webprog26.adaptereventbus.models.events.SaveAppToDatabaseEvent;
import com.example.webprog26.adaptereventbus.providers.MyAppsProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_TAG";
    private MyAppsProvider mMyAppsProvider;
    private AppsListAdapter mAdapter;


    private ProgressBar mPbLoadingInProgress;
    private RecyclerView mRvAppsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mMyAppsProvider = new MyAppsProvider(getApplicationContext());

        mPbLoadingInProgress = (ProgressBar) findViewById(R.id.pbLoadingInProgress);

        mRvAppsList = (RecyclerView) findViewById(R.id.rwAppsList);
        mRvAppsList.setHasFixedSize(true);
        mRvAppsList.setItemAnimator(new DefaultItemAnimator());
        mRvAppsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post(new RetrieveAppsListEvent(getPackageManager()));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRetrieveAppsListEvent(RetrieveAppsListEvent retrieveAppsListEvent){
        Log.i(TAG, "onRetrieveAppsListEvent");
        AppsListLoadedEvent appsListLoadedEvent = new AppsListLoadedEvent(mMyAppsProvider
                .getAppModelList(retrieveAppsListEvent
                        .getPackageManager()));
        EventBus.getDefault().post(appsListLoadedEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppsListLoadedEvent(AppsListLoadedEvent appsListLoadedEvent){
        if(mPbLoadingInProgress.getVisibility() == View.VISIBLE){
            mPbLoadingInProgress.setVisibility(View.GONE);
        }
        mAdapter = new AppsListAdapter(MainActivity.this, appsListLoadedEvent.getAppModels());
        mRvAppsList.setAdapter(mAdapter);
    }

    @Subscribe
    public void onAppCategoryChangedEvent(AppCategoryChangedEvent appCategoryChangedEvent){
        EventBus.getDefault().post(new SaveAppToDatabaseEvent(appCategoryChangedEvent.getAppModel()));
        mAdapter.updateList(appCategoryChangedEvent.getPosition());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSaveAppToDatabaseEvent(SaveAppToDatabaseEvent saveAppToDatabaseEvent){
        if(saveAppToDatabaseEvent.getAppModel().getAppCategoriesModel().isNeutral()){
            mMyAppsProvider.deleteAppFromDatabase(saveAppToDatabaseEvent.getAppModel());
            return;
        }
        mMyAppsProvider.saveAppCategory(saveAppToDatabaseEvent.getAppModel());
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
