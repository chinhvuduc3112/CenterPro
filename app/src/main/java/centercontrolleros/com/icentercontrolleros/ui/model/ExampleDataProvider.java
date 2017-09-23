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

package centercontrolleros.com.icentercontrolleros.ui.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import centercontrolleros.com.icentercontrolleros.model.AppInfor;
import centercontrolleros.com.icentercontrolleros.model.DatabaseHelper;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;
import centercontrolleros.com.icentercontrolleros.ui.untils.SharedPref;

public class ExampleDataProvider extends AbstractDataProvider {


    private final static String TAG = ExampleDataProvider.class.getSimpleName();

    private ConcreteData mLastRemovedData;
    private int mLastRemovedPosition = -1;
    private List<ConcreteData> mData;
    List<AppInfor> mListFavourite = null;

    DatabaseHelper mDatabase;
    private Context mContext;
    private PackageManager mPackageManager;

    private void loadData() {

        mListFavourite = mDatabase.getListFavouriteApp(mContext);
        if (mListFavourite != null && mListFavourite.size() > 0) {

            mData = new LinkedList<>();
            int i = 0;
            for (AppInfor applicationInfo : mListFavourite) {
                final long id = i;
                final int viewType = 0;
                final String text = "";
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;
                mData.add(new ConcreteData(id, viewType, text, swipeReaction, applicationInfo));
                Logger.d("mData", applicationInfo.packageName);
                i++;

            }
        }

    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != mPackageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }
    private void loadDefaultData(){

        List<ApplicationInfo> mListApp = checkForLaunchIntent(mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        for (ApplicationInfo mApplication : mListApp) {
            defaultAppFavourite(mApplication);
            Logger.d("ApplicationInfo", mApplication.packageName);
        }
        mListFavourite = mDatabase.getListFavouriteApp(mContext);
        if (mListFavourite != null && mListFavourite.size() > 0) {
            mData = new LinkedList<>();
            int i = 0;
            for (AppInfor applicationInfo : mListFavourite) {
                final long id = i;
                final int viewType = 0;
                final String text = "";
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;
                mData.add(new ConcreteData(id, viewType, text, swipeReaction, applicationInfo));
                Logger.d("mData", applicationInfo.packageName);
                i++;
            }
        }
    }

    void defaultAppFavourite(ApplicationInfo mApplication) {
        if (mApplication != null) {
            String packageName = mApplication.packageName;
            if (packageName.equals("com.pinterest") || packageName.equals("com.google.android.youtube") || packageName.equals("com.twitter.android") || packageName.equals("com.facebook.katana") || packageName.equals("com.instagram.android")) {
                boolean isState = mDatabase.checkInsert((String) mApplication.loadLabel(mPackageManager));
                if (!isState) {
                    mDatabase.insertFavouriteApp(mContext, mApplication, mDatabase.getSizeListFavourite());
                }
            }

        }
    }


    public ExampleDataProvider(Context context) {

        this.mContext = context;

        mPackageManager = context.getPackageManager();

        mDatabase = new DatabaseHelper(mContext);
        int checkLoadData = SharedPref.getInstance(mContext).getInt("checkLoadData", 1);
        Logger.d(TAG, checkLoadData +"");
        if (checkLoadData == 1){
            loadDefaultData();
        } else {
            loadData();
        }
        SharedPref.getInstance(mContext).putInt("checkLoadData", 0);

    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return mData.get(index);
    }

    @Override
    public int undoLastRemoval() {
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            mData.add(insertedPosition, mLastRemovedData);

            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {

        if (fromPosition == toPosition) {
            return;
        }

        Logger.d(TAG, "moveItem..........");

        final ConcreteData item = mData.remove(fromPosition);

        mData.add(toPosition, item);
        mLastRemovedPosition = -1;


        new Thread(new Runnable() {
            @Override
            public void run() {
//                mDatabase.deleteAllFavourite();
                int i = 0;
                for (ConcreteData data : mData) {
                    AppInfor applicationInfo = data.getAppInfor();
                    //  mDatabase.insertFavouriteApp(mContext, applicationInfo);

                    applicationInfo.positionSort = i;
                    mDatabase.updateFavouriteApp(mContext, applicationInfo);
                    i++;
                }
            }
        }).start();

    }

    @Override
    public void swapItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Collections.swap(mData, toPosition, fromPosition);
        mLastRemovedPosition = -1;
    }

    @Override
    public void removeItem(int position) {
        //noinspection UnnecessaryLocalVariable
        final ConcreteData removedItem = mData.remove(position);

        mLastRemovedData = removedItem;
        mLastRemovedPosition = position;
    }

    public static final class ConcreteData extends Data {

        private final long mId;
        private final String mText;
        private final int mViewType;
        private boolean mPinned;
        private AppInfor mAppInfor;

        ConcreteData(long id, int viewType, String text, int swipeReaction, AppInfor mAppInfor) {
            mId = id;
            mViewType = viewType;
            mText = makeText(id, text, swipeReaction);
            this.mAppInfor = mAppInfor;
        }

        private static String makeText(long id, String text, int swipeReaction) {
            final StringBuilder sb = new StringBuilder();

            sb.append(id);
            sb.append(" - ");
            sb.append(text);

            return sb.toString();
        }

        @Override
        public boolean isSectionHeader() {
            return false;
        }

        @Override
        public int getViewType() {
            return mViewType;
        }

        @Override
        public long getId() {
            return mId;
        }

        @Override
        public String toString() {
            return mText;
        }

        @Override
        public String getText() {
            return mText;
        }

        @Override
        public boolean isPinned() {
            return mPinned;
        }

        @Override
        public AppInfor getAppInfor() {
            return mAppInfor;
        }


        @Override
        public void setPinned(boolean pinned) {
            mPinned = pinned;
        }
    }
}
