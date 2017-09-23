/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package centercontrolleros.com.icentercontrolleros.ui.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.LinkedList;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.model.AppInfor;
import centercontrolleros.com.icentercontrolleros.model.DatabaseHelper;
import centercontrolleros.com.icentercontrolleros.ui.fragment.ExampleDataProviderFragment;
import centercontrolleros.com.icentercontrolleros.ui.model.AbstractDataProvider;
import centercontrolleros.com.icentercontrolleros.ui.model.ExampleDataProvider;
import centercontrolleros.com.icentercontrolleros.ui.untils.DrawableUtils;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;
import centercontrolleros.com.icentercontrolleros.ui.untils.SharedPref;
import centercontrolleros.com.icentercontrolleros.ui.untils.ViewUtils;

public class DraggableExampleItemAdapter
        extends RecyclerView.Adapter<DraggableExampleItemAdapter.MyViewHolder>
        implements DraggableItemAdapter<DraggableExampleItemAdapter.MyViewHolder> {
    private static final String TAG = "MyDraggableItemAdapter";

    private PackageManager packageManager;
    private ArrayList<AppInfor> mApplicationInfos;
    private DatabaseHelper mDatabaseHelper;
    private Context mContext;

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    private AbstractDataProvider mProvider;

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {

        ImageView mImageViewIconApp;
        TextView mTextViewNameApp;
        ImageButton mImageButtonDelete;
        ImageView mImageDrop;
        LinearLayout mLinearLayout;


        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;

        public MyViewHolder(View v) {
            super(v);
            Logger.d(TAG, "MyViewHolder:");
            mImageViewIconApp = v.findViewById(R.id.app_icon);
            mTextViewNameApp = v.findViewById(R.id.app_name);
            mImageButtonDelete = v.findViewById(R.id.img_delete);
            mImageDrop = v.findViewById(R.id.img_drop);
            mLinearLayout = v.findViewById(R.id.linearLayout);


            mContainer = (FrameLayout) v.findViewById(R.id.container);
//            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextView = (TextView) v.findViewById(R.id.app_name);
        }
    }

    public DraggableExampleItemAdapter(AbstractDataProvider dataProvider, Context mContext) {
        mProvider = dataProvider;
        this.mContext = mContext;
        mDatabaseHelper = new DatabaseHelper(mContext);
        Logger.d(TAG, "mProvider :" + mProvider.getCount());

        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);


    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return mProvider.getItem(position).getViewType();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate((viewType == 0) ? R.layout.item_favourite : R.layout.item_favourite, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (packageManager == null)
            packageManager = holder.mImageViewIconApp.getContext().getPackageManager();

        final AbstractDataProvider.Data item = mProvider.getItem(position);


        // set text
        holder.mTextView.setText(item.getText());

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }


        final AppInfor mAppInfo = item.getAppInfor();
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
                Logger.d("mDelete", mDelete + "");
                if (mDelete) {
                    mProvider = new ExampleDataProvider(mContext);
                    notifyDataSetChanged();

                } else {
                }
            }
        });


    }

    @Override
    public int getItemCount() {

        int size = mProvider.getCount();
        Logger.d(TAG, "getItemCount:" + size);
        return size;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }

        mProvider.moveItem(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mLinearLayout;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }
}
