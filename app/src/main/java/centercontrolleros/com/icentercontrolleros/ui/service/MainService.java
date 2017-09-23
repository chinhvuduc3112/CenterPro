package centercontrolleros.com.icentercontrolleros.ui.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;

import java.util.ArrayList;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.model.AppInfor;
import centercontrolleros.com.icentercontrolleros.model.DatabaseHelper;
import centercontrolleros.com.icentercontrolleros.model.SettingEnableHelper;
import centercontrolleros.com.icentercontrolleros.ui.activity.ChangeActivity;
import centercontrolleros.com.icentercontrolleros.ui.activity.SettingActivity;
import centercontrolleros.com.icentercontrolleros.ui.adapter.CustomMusicAdapter;
import centercontrolleros.com.icentercontrolleros.ui.adapter.CustomRecyleAdapter;
import centercontrolleros.com.icentercontrolleros.ui.customview.MyRecycleView;
import centercontrolleros.com.icentercontrolleros.ui.customview.VerticalSeebar;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;
import centercontrolleros.com.icentercontrolleros.ui.untils.SharedPref;
import easytouch.myslidinglibrary.SlidingUpPanelLayout;

/**
 * Created by VuDuc on 8/30/2017.
 */

public class MainService extends Service {

    public static final String ACTION_DISABLE_CONTROL_CENTER = "DisableControlCenter";
    public static final String ACTION_ENABLE_CONTROL_CENTER = "EnableControlCenter";
    private static final float SWIPE_MIN_DISTANCE = 50;
    private static final float SWIPE_THRESHOLD_VELOCITY = 100;
    private static final String TAG = MainService.class.getSimpleName();
    Context mContext;
    IntentFilter mIntentFilter;
    MyRecycleView myRecycleView;
    SharedPreferences settingPrefs;

    int mBottomHeight;
    int mBottomWidth;

    SlidingUpPanelLayout.PanelSlideListener mPanelSlideListener = new SlidingUpPanelLayout.PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {

        }

        @Override
        public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            Log.d(TAG, "onPanelStateChanged:" + newState);
//         if(newState != SlidingUpPanelLayout.PanelState.DRAGGING)
//               myRecycleView.enableScrollRecyleview();
        }
    };
    private WindowManager.LayoutParams mainParams;
    private WindowManager.LayoutParams bottomParams;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private WindowManager mWindowManager;
    private View mainView;
    private View bottomView;
    //    private MyScrollView main_scrollView;
    private ViewStub layout_main;
    private SettingEnableHelper mSettingEnableHelper;
    private boolean enableControlCenter;
    private MyRecycleView.OnListenerMyRecycleView mOnListenerMyRecycleView = new MyRecycleView.OnListenerMyRecycleView() {
        @Override
        public void onScrolling() {
            mSlidingUpPanelLayout.disableSlidingPanel();
        }

        @Override
        public void onFling() {
            mSlidingUpPanelLayout.disableSlidingPanel();
        }

        @Override
        public void onUp() {
            mSlidingUpPanelLayout.enableSlidingPanel();
        }

        @Override
        public void onMinScrolling() {

            Log.d(TAG, "onMinScrolling");
            //  mainView.setVisibility(View.GONE);
            bottomView.setVisibility(View.VISIBLE);
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            mWindowManager.updateViewLayout(mainView, mainParams);

//            myRecycleView.disableScrollRecylceview();
//            mSlidingUpPanelLayout.enableSlidingPanel();
        }
    };
    private VerticalSeebar.OnListenerScrollSeekBar mOnListenerScrollSeekBar = new VerticalSeebar.OnListenerScrollSeekBar() {
        @Override
        public void onMove() {

            Log.d(TAG, "onMove....");
            mSlidingUpPanelLayout.disableSlidingPanel();
//            main_scrollView.disableSrolling();

            myRecycleView.disableScrollRecylceview();

        }

        @Override
        public void onDown() {
            mSlidingUpPanelLayout.disableSlidingPanel();
//            main_scrollView.disableSrolling();
            myRecycleView.disableScrollRecylceview();
        }

        @Override
        public void onUp() {
            Log.d(TAG, "onUp....");
            mSlidingUpPanelLayout.enableSlidingPanel();
//            main_scrollView.enableScrolling();
            myRecycleView.enableScrollRecyleview();
        }
    };
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action)) {
                    if (action.equals("ACTION_DISABLE")) {
                        disableLayout();
                    } else if (action.equals("ACTION_SILENT")) {
                        NotificationManager n = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (!n.isNotificationPolicyAccessGranted()) {
                                disableLayout();
                            }
                        }
                    } else if (action.equals(SettingActivity.BOTTOM_POSITION_CHANGE)) {
                        Logger.d(TAG, "prefUpdatePosion........");
                        removeAllViewControler();
                        addMainView();
                    } else if (action.equals(SettingActivity.BOTTOM_SIZE_CHANGE)) {
                        Logger.d(TAG, "prefUpdateSize........");
                        removeAllViewControler();
                        addMainView();
                    } else if (action.equals(SettingActivity.BACKGROUND_CHANGE)) {
                        removeAllViewControler();
                        addMainView();
                    }
                }
            }
        }
    };

    public static void startMainService(Context context, String action) {
        Intent intent = new Intent(context, MainService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public static void disableControlCenter(Context context) {
        Logger.d(TAG, "disableControlCenter...");
        startMainService(context, ACTION_DISABLE_CONTROL_CENTER);
    }

    public static void enableControlCenter(Context context) {
        Logger.d(TAG, "enableControlCenter...");
        startMainService(context, ACTION_ENABLE_CONTROL_CENTER);
    }

    public static void saveSharepreferences(Context mContext, boolean isChange) {
        SharedPreferences mSharePpreferrences = mContext.getSharedPreferences("CHANGE_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharePpreferrences.edit();
        mEditor.putBoolean("change", isChange);
        mEditor.commit();
    }

    private void disableLayout() {
        mainView.setVisibility(View.GONE);
        bottomView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "onCreate........");
        init();
    }

    private void init() {
        mContext = this;
        if (mSettingEnableHelper == null)
            mSettingEnableHelper = new SettingEnableHelper(this);

        if (mWindowManager == null)
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        settingPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        addMainView();
    }

    private ArrayList<AppInfor> loadApps() {
        ArrayList<AppInfor> mListApp;
        DatabaseHelper mDatabase = new DatabaseHelper(mContext);
        mListApp = (ArrayList<AppInfor>) mDatabase.getListFavouriteApp(mContext);
        return mListApp;
    }

    public void updateListApps() {
        //sharepre userchange;
        ArrayList<AppInfor> applicationInfos = loadApps();
        myRecycleView.updateData(applicationInfos);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "onStartCommand==========");
        super.onStartCommand(intent, flags, startId);

        Bundle bundle = new Bundle();
        bundle.putBoolean("On", true);
        ChangeActivity.startChangeActivity(mContext, ChangeActivity.ACTION_FINISH_TRANPARENT, bundle);

        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(ACTION_DISABLE_CONTROL_CENTER)) {
                    settingDisable();
                } else if (action.equals(ACTION_ENABLE_CONTROL_CENTER)) {
                    settingEnable();
                }
            } else {
            }
        } else {
        }
        return START_STICKY;
    }

    private void settingEnable() {
        enableControlCenter = true;
        bottomView.setVisibility(View.VISIBLE);
        registerAllActionControler();
    }

    private void settingDisable() {
        Logger.d(TAG, "settingDisable..............");
        enableControlCenter = false;
        bottomView.setVisibility(View.GONE);
        turnOffFlashLight();
    }

    private void unRegisterAllActionControler() {
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerAllActionControler() {
        try {
            if (mIntentFilter == null) {
                mIntentFilter = new IntentFilter();
            }
            mIntentFilter.addAction("ACTION_DISABLE");
            mIntentFilter.addAction("ACTION_SILENT");
            mIntentFilter.addAction(SettingActivity.BOTTOM_POSITION_CHANGE);
            mIntentFilter.addAction(SettingActivity.BOTTOM_SIZE_CHANGE);
            mIntentFilter.addAction(SettingActivity.BACKGROUND_CHANGE);
            registerReceiver(mBroadcastReceiver, mIntentFilter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeViewControler(View view) {
        try {
            view.setVisibility(View.GONE);
            ((WindowManager) getApplicationContext().getSystemService(Service.WINDOW_SERVICE)).removeView(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeAllViewControler() {
        try {
            removeViewControler(mainView);
            removeViewControler(bottomView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMainView() {
        if (Build.VERSION.SDK_INT < 26) {
            mainParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
        } else {
            mainParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
        }

        mainParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        mainParams.x = 0;
        mainParams.y = 0;
        mainView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_main_service_recyclviewer, null);

        String settingPossionString = settingPrefs.getString("prefUpdatePosion", null);
        String settingSizeString = settingPrefs.getString("prefUpdateSize", null);
        String settingBackground = settingPrefs.getString("prefUpdateBackground", null);
        settBackgroundApp(settingBackground);
        setSizeBottomBar(settingSizeString);

        if (settingPossionString != null) {
            int settingBottom = Integer.parseInt(settingPossionString);
            switch (settingBottom) {
                case 1:
                    setBottomParamVetical();
                    bottomParams.gravity = Gravity.LEFT;
                    bottomView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_bottom_service_vertical, null);
                    break;
                case 2:
                    setBottomParamVetical();
                    bottomParams.gravity = Gravity.RIGHT;
                    bottomView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_bottom_service_vertical, null);
                    break;
                case 3:
                    setBottomParamHorizontal();
                    bottomParams.gravity = Gravity.BOTTOM;
                    bottomView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_bottom_service, null);
            }
        }
        if (settingPossionString == null && settingSizeString == null) {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            mBottomWidth = (int) (metrics.widthPixels * 0.5f);
            if (Build.VERSION.SDK_INT < 26) {
                bottomParams = new WindowManager.LayoutParams(
                        mBottomWidth,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            } else {
                bottomParams = new WindowManager.LayoutParams(
                        mBottomWidth,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }
            bottomParams.gravity = Gravity.BOTTOM;
            bottomView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_bottom_service, null);
        }

        mWindowManager.addView(bottomView, bottomParams);
        mWindowManager.addView(mainView, mainParams);
        addControls(mainView);

        final GestureDetector gdt = new GestureDetector(mContext, new MyGestureListener());

        bottomView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gdt.onTouchEvent(motionEvent);
                return false;
            }
        });
    }

    private void settBackgroundApp(String settingBackground) {
        if (settingBackground != null) {
            int settingSizeBottom = Integer.parseInt(settingBackground);
            switch (settingSizeBottom) {
                case 1:
                    mainView.setBackgroundResource(android.R.color.transparent);
                    break;
                case 2:
                    mainView.setBackgroundResource(R.drawable.bg_tranfer);
                    break;
                case 3:
                    mainView.setBackgroundResource(R.color.colorPrimary);
                    break;
                case 4:
                    mainView.setBackgroundResource(R.drawable.images);
                    break;
            }
        } else {
            mainView.setBackgroundResource(R.drawable.images);
        }
    }


    private void setSizeBottomBar(String settingSizeString) {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        if (settingSizeString != null) {
            int settingSizeBottom = Integer.parseInt(settingSizeString);
            switch (settingSizeBottom) {
                case 1:
                    mBottomHeight = (int) (metrics.widthPixels * 0.3f);
                    mBottomWidth = (int) (metrics.widthPixels * 0.3f);
                    break;
                case 2:
                    mBottomHeight = (int) (metrics.widthPixels * 0.4f);
                    mBottomWidth = (int) (metrics.widthPixels * 0.4f);
                    break;
                case 3:
                    mBottomHeight = (int) (metrics.widthPixels * 0.5f);
                    mBottomWidth = (int) (metrics.widthPixels * 0.5f);
                    break;
                case 4:
                    mBottomHeight = (int) (metrics.widthPixels * 0.6f);
                    mBottomWidth = (int) (metrics.widthPixels * 0.6f);
                    break;
                case 5:
                    mBottomHeight = (int) (metrics.widthPixels * 0.7f);
                    mBottomWidth = (int) (metrics.widthPixels * 0.7f);
                    break;
            }
        }
    }

    private void setBottomParamVetical() {
        if (Build.VERSION.SDK_INT < 26) {
            bottomParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    mBottomHeight,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            bottomParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    mBottomHeight,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
    }

    private void setBottomParamHorizontal() {
        if (Build.VERSION.SDK_INT < 26) {
            bottomParams = new WindowManager.LayoutParams(
                    mBottomWidth,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            bottomParams = new WindowManager.LayoutParams(
                    mBottomWidth,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
    }

    private void addControls(View view) {
        mSlidingUpPanelLayout = view.findViewById(R.id.sliding_layout);
        mSlidingUpPanelLayout.addPanelSlideListener(mPanelSlideListener);
        layout_main = view.findViewById(R.id.layout_main);

//        main_scrollView = view.findViewById(main_scrollView);
//        main_scrollView.setOnListenerMyScrollView(mOnListenerMyScrollView);
        mSlidingUpPanelLayout.setPanelHeight(0);
        mSlidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState.name().toString().equalsIgnoreCase("Collapsed")) {
                    //action when collapsed
                    mainView.setVisibility(View.GONE);
                    bottomView.setVisibility(View.VISIBLE);

                } else if (newState.name().equalsIgnoreCase("Expanded")) {
                    //action when expanded

                }
            }
        });

        layout_main.setLayoutResource(R.layout.activity_main_center_controller_recycleviewr);
        layout_main.setVisibility(View.VISIBLE);
        myRecycleView = view.findViewById(R.id.myrecycleview);
        myRecycleView.setOnListenerMyRecycleView(mOnListenerMyRecycleView);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myRecycleView.setLayoutManager(llm);
        CustomRecyleAdapter customRecyleAdapter = new CustomRecyleAdapter(getApplicationContext(), loadApps());
        customRecyleAdapter.setOnListenerScrollSeekBar(mOnListenerScrollSeekBar);
        myRecycleView.setAdapter(customRecyleAdapter);
    }

    private boolean readSharePreferences(Context mContext) {
        SharedPreferences mSharePpreferrences = mContext.getSharedPreferences("CHANGE_DATA", Context.MODE_PRIVATE);
        boolean isChang = mSharePpreferrences.getBoolean("change", false);
        return isChang;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void turnOffFlashLight() {
        Logger.d(TAG, "turnOffFlashLight....");
//        Intent intentFlash = new Intent();
//        intentFlash.setAction(ACTION_FLASH_LIGHT);
//        mContext.sendBroadcast(intentFlash);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                turnOffFlashLight();
            }
        });
        settingDisable();
        unRegisterAllActionControler();
        removeAllViewControler();
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                addEventWhenFling();
                return true; // Right to left
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                addEventWhenFling();
                return true; // Left to right
            }

            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                addEventWhenFling();
                return true; // Bottom to top
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return true; // Top to bottom
            }
            return false;
        }

        private void addEventWhenFling() {
            Intent intent = new Intent();
            intent.setAction("ACTION_UP");
            sendBroadcast(intent);

            mainView.setVisibility(View.VISIBLE);
            bottomView.setVisibility(View.GONE);
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            mWindowManager.updateViewLayout(mainView, mainParams);

            if (readSharePreferences(mContext)) {
                updateListApps();
                saveSharepreferences(mContext, false);
            }

            if (SharedPref.getInstance(mContext).getBoolean(CustomMusicAdapter.MUSIC_APP_ISCHANGE)) {
                myRecycleView.updateUIMusic();
                SharedPref.getInstance(mContext).putBoolean(CustomMusicAdapter.MUSIC_APP_ISCHANGE, false);
            }
            myRecycleView.updateUIWifi();
        }
    }
}
