package com.example.webprog26.adaptereventbus;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.webprog26.adaptereventbus.managers.DrawableToBitmapConverter;
import com.example.webprog26.adaptereventbus.models.AppModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppModel appModel = new AppModel();
        appModel.setAppLabel("Test");
        appModel.setAppIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        appModel.setHasCategory(true);
        appModel.getAppCategoriesModel().setEducational(true);

        Log.i(TAG, appModel.toString());
    }
}
