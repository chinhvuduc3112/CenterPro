package centercontrolleros.com.icentercontrolleros.ui.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.model.AppInfor;
import centercontrolleros.com.icentercontrolleros.model.DatabaseHelper;
import centercontrolleros.com.icentercontrolleros.ui.ReOrderListenner.OnCustomerListChangedListener;
import centercontrolleros.com.icentercontrolleros.ui.ReOrderListenner.OnStartDragListener;
import centercontrolleros.com.icentercontrolleros.ui.untils.ItemTouchHelperAdapter;
import centercontrolleros.com.icentercontrolleros.ui.untils.ItemTouchHelperViewHolder;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;

/**
 * Created by Duy on 9/1/2017.
 */

public class CustomFavouriteAdapter extends
        RecyclerView.Adapter<CustomFavouriteAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    public String TAG = CustomFavouriteAdapter.class.getSimpleName();
    private ArrayList<AppInfor> mApplicationInfos;
    private Context mContext;
    private PackageManager packageManager;
    DatabaseHelper mDatabaseHelper;
    private OnStartDragListener mDragStartListener;
    private OnCustomerListChangedListener mListChangedListener;

    public CustomFavouriteAdapter(ArrayList<AppInfor> applicationInfos,
                                  Context context,
                                  OnStartDragListener dragStartListener,
                                  OnCustomerListChangedListener listChangedListener) {
        mApplicationInfos = applicationInfos;
        mContext = context;
        packageManager = context.getPackageManager();
        mDatabaseHelper = new DatabaseHelper(mContext);
        mDragStartListener = dragStartListener;
        mListChangedListener = listChangedListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ApplicationInfo mAppInfo = mApplicationInfos.get(position);
        if (mAppInfo != null) {
            holder.mTextViewNameApp.setText(mAppInfo.loadLabel(packageManager));
            try {
                Drawable appIcon = packageManager.getApplicationIcon(mAppInfo.packageName);
                holder.mImageViewIconApp.setImageDrawable(appIcon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        holder.mImageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean mDelete = mDatabaseHelper.deleteFavourite(mContext, mAppInfo.packageName);
                Logger.d("mDelete", mDelete+"");
                if (mDelete) {
                    getList();
                } else {

                }
            }
        });

        holder.mImageDrop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return ((null != mApplicationInfos) ? mApplicationInfos.size() : 0);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (mApplicationInfos != null) {
            Collections.swap(mApplicationInfos, fromPosition, toPosition);
            mListChangedListener.onNoteListChanged(mApplicationInfos);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        ImageView mImageViewIconApp;
        TextView mTextViewNameApp;
        ImageButton mImageButtonDelete;
        ImageView mImageDrop;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImageViewIconApp = (ImageView) itemView.findViewById(R.id.app_icon);
            mTextViewNameApp = (TextView) itemView.findViewById(R.id.app_name);
            mImageButtonDelete = (ImageButton) itemView.findViewById(R.id.img_delete);
            mImageDrop = (ImageView) itemView.findViewById(R.id.img_drop);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    private void getList() {
        mApplicationInfos = new ArrayList<>();
        mApplicationInfos = (ArrayList<AppInfor>) mDatabaseHelper.getListFavouriteApp(mContext);
        notifyDataSetChanged();
    }
}
