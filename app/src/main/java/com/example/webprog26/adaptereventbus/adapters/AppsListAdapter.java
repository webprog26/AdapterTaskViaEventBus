package com.example.webprog26.adaptereventbus.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.webprog26.adaptereventbus.R;
import com.example.webprog26.adaptereventbus.listeners.OnRadioButtonClickListener;
import com.example.webprog26.adaptereventbus.managers.AppCategoryManager;
import com.example.webprog26.adaptereventbus.models.AppCategoriesModel;
import com.example.webprog26.adaptereventbus.models.AppModel;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by webpr on 18.01.2017.
 */

public class AppsListAdapter extends RecyclerView.Adapter<AppsListAdapter.AppsListViewHolder> {

    private static final String TAG = "AppsListAdapter";

    private List<AppModel> mAppModelList;
    private WeakReference<Context> mContextWeakReference;

    public AppsListAdapter(Context context, List<AppModel> appModelsList) {
        this.mContextWeakReference = new WeakReference<Context>(context);
        this.mAppModelList = appModelsList;
    }

    @Override
    public AppsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppsListViewHolder(LayoutInflater
                .from(mContextWeakReference.get())
                .inflate(R.layout.apps_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(AppsListViewHolder holder, int position) {
        AppModel appModel = mAppModelList.get(position);

        holder.mIvAppIcon.setImageBitmap(appModel.getAppIcon());
        holder.mTvAppLabel.setText(appModel.getAppLabel());

        AppCategoriesModel appCategoriesModel = appModel.getAppCategoriesModel();

        holder.mRbEducational.setChecked(appCategoriesModel.isEducational());
        holder.mRbForFun.setChecked(appCategoriesModel.isForFun());
        holder.mRbBlocked.setChecked(appCategoriesModel.isBlocked());

        TextView appCategoryTextView = holder.mTvAppCategory;

        OnRadioButtonClickListener clickListener = new OnRadioButtonClickListener(appModel, position, mContextWeakReference.get(), appCategoryTextView);

        new AppCategoryManager(mContextWeakReference.get()).setAppCategory(appCategoriesModel, appCategoryTextView);

        holder.mRbEducational.setOnClickListener(clickListener);
        holder.mRbForFun.setOnClickListener(clickListener);
        holder.mRbBlocked.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return mAppModelList.size();
    }

    public class AppsListViewHolder extends RecyclerView.ViewHolder{

        private ImageView mIvAppIcon;
        private TextView mTvAppLabel, mTvAppCategory;
        private AppCompatRadioButton mRbEducational, mRbForFun, mRbBlocked;

        public AppsListViewHolder(View itemView) {
            super(itemView);

            mIvAppIcon = (ImageView) itemView.findViewById(R.id.ivAppIcon);
            mTvAppLabel = (TextView) itemView.findViewById(R.id.tvAppLabel);
            mTvAppCategory = (TextView) itemView.findViewById(R.id.tvAppCategory);

            mRbEducational = (AppCompatRadioButton) itemView.findViewById(R.id.rbEducational);
            mRbForFun = (AppCompatRadioButton) itemView.findViewById(R.id.rbForFun);
            mRbBlocked = (AppCompatRadioButton) itemView.findViewById(R.id.rbBlocked);
        }
    }

    /**
     * Updates single row in list
     * @param appPosition int
     */
    public void updateList(int appPosition){
        notifyItemChanged(appPosition);
    }
}
