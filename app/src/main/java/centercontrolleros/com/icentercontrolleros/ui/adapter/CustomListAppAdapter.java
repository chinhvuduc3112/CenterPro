package centercontrolleros.com.icentercontrolleros.ui.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.model.AppInfor;
import centercontrolleros.com.icentercontrolleros.model.DatabaseHelper;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;

/**
 * Created by Duy on 8/31/2017.
 */

public class CustomListAppAdapter extends RecyclerView.Adapter<CustomListAppAdapter.ViewHolder> {

    public final String TAG = CustomListAppAdapter.class.getSimpleName();

    private ArrayList<ApplicationInfo> mApplicationInfos;
    private Context mContext;
    private PackageManager packageManager;
    private DatabaseHelper mDatabaseHelper;
    private boolean isState;

    public CustomListAppAdapter(ArrayList<ApplicationInfo> applicationInfos, Context context) {
        mApplicationInfos = applicationInfos;
        packageManager = context.getPackageManager();
        Collections.sort(mApplicationInfos, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo applicationInfo, ApplicationInfo t1) {
                return applicationInfo.loadLabel(packageManager).toString().compareTo(t1.loadLabel(packageManager).toString());
            }
        });

        mContext = context;
        mDatabaseHelper = new DatabaseHelper(mContext);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_install_app, parent, false);
        ApplicationInfo mAppInfo = mApplicationInfos.get(parent.getChildCount());
//        isState = mDatabaseHelper.checkInsert((String) mAppInfo.packageName);
//        if (isState){
//            view.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
//        } else {
//            view.setBackgroundColor(mContext.getResources().getColor(R.color.colorBgmain));
//        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ApplicationInfo mAppInfo = mApplicationInfos.get(position);

        if (mAppInfo != null) {
            holder.mTextViewNameApp.setText(mAppInfo.loadLabel(packageManager));
            try {
                Drawable appIcon = packageManager.getApplicationIcon(mAppInfo.packageName);
                holder.mImageViewIconApp.setImageDrawable(appIcon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            boolean isInsert = mDatabaseHelper.checkInsert((String) mApplicationInfos.get(position).loadLabel(packageManager));
            if (isInsert) {
                Logger.d(TAG, isInsert + "========="+position);
                holder.mCkbSelectApp.setChecked(true);
            }else{
                holder.mCkbSelectApp.setChecked(false);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isState = mDatabaseHelper.checkInsert((String) mApplicationInfos.get(position).loadLabel(packageManager));

                if (!isState) {
                    boolean mIsInsert = mDatabaseHelper.insertFavouriteApp(mContext, mApplicationInfos.get(position), mDatabaseHelper.getSizeListFavourite());
                    if (mIsInsert) {
                        Toast.makeText(mContext, "Added to Favorites", Toast.LENGTH_SHORT).show();
                        holder.mCkbSelectApp.setChecked(true);
                    } else {
                        Toast.makeText(mContext, "Added Faild", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(mContext, "Application is in list favourite", Toast.LENGTH_SHORT).show();
                    holder.mCkbSelectApp.setChecked(false);
                    boolean mDeleteFavouriteApp = mDatabaseHelper.deleteFavourite(mContext, mAppInfo.packageName);
                    if(mDeleteFavouriteApp){
                        Toast.makeText(mContext, "Delete Complete", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(mContext, "Delete Faild, Check again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return ((null != mApplicationInfos) ? mApplicationInfos.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageViewIconApp;
        TextView mTextViewNameApp;
        CheckBox mCkbSelectApp;
        View itemView;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.itemView = itemView;
            mImageViewIconApp = (ImageView) itemView.findViewById(R.id.app_icon);
            mTextViewNameApp = (TextView) itemView.findViewById(R.id.app_name);
            mCkbSelectApp = (CheckBox) itemView.findViewById(R.id.ckb_app_select);

            mCkbSelectApp.setClickable(false);
        }
    }
}
