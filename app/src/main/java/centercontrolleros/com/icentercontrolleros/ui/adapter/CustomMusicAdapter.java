package centercontrolleros.com.icentercontrolleros.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.model.DatabaseHelper;
import centercontrolleros.com.icentercontrolleros.ui.model.MyResolveInfo;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;
import centercontrolleros.com.icentercontrolleros.ui.untils.SharedPref;

/**
 * Created by VuDuc on 9/1/2017.
 */

public class CustomMusicAdapter extends RecyclerView.Adapter<CustomMusicAdapter.ViewHolder> {

    public final String TAG = CustomMusicAdapter.class.getSimpleName();
    public static final String MUSIC_APP_CHECKED = "musicAppChecked";
    public static final String MUSIC_APP = "MUSIC_APP";
    public static final String MUSIC_APP_ISCHANGE = "MUSIC_APP_ISCHANGE";

    private ArrayList<MyResolveInfo> mPlayerList;
    private Context mContext;
    private PackageManager mPackageManager;
    private int lastChecked = -1;

    public CustomMusicAdapter(ArrayList<MyResolveInfo> playerList, Context context) {
        mPlayerList = playerList;
        mContext = context;
        mPackageManager = context.getPackageManager();

        Collections.sort(mPlayerList, new Comparator<MyResolveInfo>() {
            @Override
            public int compare(MyResolveInfo myResolveInfo, MyResolveInfo t1) {
                return myResolveInfo.getResolveInfo().loadLabel(mPackageManager).toString()
                        .compareTo(t1.getResolveInfo().loadLabel(mPackageManager).toString());
            }
        });

        lastChecked = SharedPref.getInstance(mContext).getInt(MUSIC_APP_CHECKED, -1);
        if (lastChecked != -1) {
            mPlayerList.get(lastChecked).setCheck(true);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_manager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MyResolveInfo mMusicAppInfo = mPlayerList.get(position);
        final ResolveInfo mMusicAppList = mMusicAppInfo.getResolveInfo();
        if (mMusicAppList != null) {
            holder.txt_music_item.setText(mMusicAppList.loadLabel(mPackageManager));
            try {
                Drawable appIcon = mPackageManager.getApplicationIcon(mMusicAppList.activityInfo.packageName);
                holder.img_music_item.setImageDrawable(appIcon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (lastChecked == -1) {
                defaultAppMusic(mMusicAppInfo, mMusicAppList.activityInfo.packageName, position);
            }
            holder.ckb_music_item.setChecked(mMusicAppInfo.isCheck());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lastChecked != -1) {
                        if (position != lastChecked) {
                            mPlayerList.get(lastChecked).setCheck(false);
                            lastChecked = position;
                            mMusicAppInfo.setCheck(true);
                            notifyDataSetChanged();
                        }
                    } else {
                        lastChecked = position;
                        mMusicAppInfo.setCheck(true);
                        notifyDataSetChanged();
                    }
                    SharedPref.getInstance(mContext).putInt(MUSIC_APP_CHECKED, lastChecked);
                    SharedPref.getInstance(mContext).putString(MUSIC_APP, mMusicAppList.activityInfo.packageName);
                    SharedPref.getInstance(mContext).putBoolean(MUSIC_APP_ISCHANGE, true);
                }
            });

        }
    }

    private void defaultAppMusic(MyResolveInfo mMusicAppInfo, String packageName, int position) {
        if (packageName.equals("ht.nct")||packageName.equals("com.android.music") || packageName.equals("com.sec.android.app.music") || packageName.equals("com.zing.mp3")) {
            lastChecked = position;
            mMusicAppInfo.setCheck(true);
            SharedPref.getInstance(mContext).putInt(MUSIC_APP_CHECKED, lastChecked);
            SharedPref.getInstance(mContext).putString(MUSIC_APP, packageName);
            SharedPref.getInstance(mContext).putBoolean(MUSIC_APP_ISCHANGE, true);
        }
    }

    @Override
    public int getItemCount() {
        return ((null != mPlayerList) ? mPlayerList.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView img_music_item;
        TextView txt_music_item;
        CheckBox ckb_music_item;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            img_music_item = (ImageView) itemView.findViewById(R.id.img_music_item);
            txt_music_item = (TextView) itemView.findViewById(R.id.txt_music_item);
            ckb_music_item = (CheckBox) itemView.findViewById(R.id.ckb_music_item);

            ckb_music_item.setClickable(false);
        }
    }
}
