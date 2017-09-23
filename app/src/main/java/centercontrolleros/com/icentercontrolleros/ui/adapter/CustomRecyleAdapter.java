package centercontrolleros.com.icentercontrolleros.ui.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.model.AppInfor;
import centercontrolleros.com.icentercontrolleros.model.DeviceMainHelper;
import centercontrolleros.com.icentercontrolleros.presenter.MainPresenter;
import centercontrolleros.com.icentercontrolleros.ui.activity.ChangeActivity;
import centercontrolleros.com.icentercontrolleros.ui.activity.MusicMangerActivity;
import centercontrolleros.com.icentercontrolleros.ui.customview.VerticalSeebar;
import centercontrolleros.com.icentercontrolleros.ui.model.ItemRecycleViewControl;
import centercontrolleros.com.icentercontrolleros.ui.model.MyTimeOut;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;
import centercontrolleros.com.icentercontrolleros.ui.untils.MusicMediaApp;
import centercontrolleros.com.icentercontrolleros.ui.untils.MyTimeOutData;
import centercontrolleros.com.icentercontrolleros.ui.untils.SharedPref;
import centercontrolleros.com.icentercontrolleros.ui.view.ViewMainListender;

import static centercontrolleros.com.icentercontrolleros.ui.activity.MainActivity.REQUEST_CODE_WRITE_SETTINGS;

/**
 * Created by mr.logic on 9/1/2017.
 */

public class CustomRecyleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = CustomRecyleAdapter.class.getSimpleName();

    private static final int TYPE_VIEW_CONTROL_WIFI_MEDIA = 1;
    private static final int TYPE_VIEW_CONTROL_VOLUME = 3;
    private static final int TYPE_VIEW_CONTROL_APP_BASIC = 4;
    private static final int TYPE_VIEW_CONTROL_APP_CUSTOM = 5;
    ArrayList<AppInfor> listallapps;
    private ArrayList<ItemRecycleViewControl> dataSet;
    private Context mContext;
    private VerticalSeebar.OnListenerScrollSeekBar mOnListenerScrollSeekBar;

    public CustomRecyleAdapter(Context context, ArrayList<AppInfor> dataSet) {
        this.mContext = context;
        //    this.dataSet = dataSet;

        if (dataSet == null)
            this.dataSet = getDataDefault();
        else
            this.dataSet = calculatorArrlistItemContorl(dataSet);
        Log.d(TAG, "CustomRecyleAdapter");

    }

    public void setOnListenerScrollSeekBar(VerticalSeebar.OnListenerScrollSeekBar onListenerScrollSeekBar) {
        this.mOnListenerScrollSeekBar = onListenerScrollSeekBar;
    }

    public ArrayList<ItemRecycleViewControl> getDataDefault() {

        return calculatorArrlistItemContorl(null);
    }


    private ArrayList<ItemRecycleViewControl> calculatorArrlistItemContorl(ArrayList<AppInfor> listallapps) {
        this.listallapps = listallapps;
        ArrayList<ItemRecycleViewControl> itemRecycleViewControls = new ArrayList<>();
        itemRecycleViewControls.addAll(getHardCodeList());
        itemRecycleViewControls.addAll(getListCustom(listallapps));
        return itemRecycleViewControls;
    }


    public void updateData(ArrayList<AppInfor> listallapps) {
        dataSet = calculatorArrlistItemContorl(listallapps);
        notifyDataSetChanged();
    }

    public void updateUI() {
        notifyDataSetChanged();
    }


    private ArrayList<ItemRecycleViewControl> getListCustom(ArrayList<AppInfor> listallapps) {
        ArrayList<ItemRecycleViewControl> itemRecycleViewControls = new ArrayList<>();

        if (listallapps != null) {
            int size = listallapps.size();
            int totalpape = ((size % 4 == 0) ? size / 4 : size / 4 + 1);
            for (int i = 0; i < totalpape; i++) {
                ItemRecycleViewControl itemRecycleViewControlCustom = new ItemRecycleViewControl();
                itemRecycleViewControlCustom.typeView = TYPE_VIEW_CONTROL_APP_CUSTOM;
                itemRecycleViewControls.add(itemRecycleViewControlCustom);
            }
        }

        return itemRecycleViewControls;
    }


    private ArrayList<ItemRecycleViewControl> getHardCodeList() {
        ArrayList<ItemRecycleViewControl> itemRecycleViewControls = new ArrayList<>();
        ItemRecycleViewControl itemRecycleViewControlWifi = new ItemRecycleViewControl();
        itemRecycleViewControlWifi.typeView = TYPE_VIEW_CONTROL_WIFI_MEDIA;
        itemRecycleViewControls.add(itemRecycleViewControlWifi);

        ItemRecycleViewControl itemRecycleViewControlVolume = new ItemRecycleViewControl();
        itemRecycleViewControlVolume.typeView = TYPE_VIEW_CONTROL_VOLUME;
        itemRecycleViewControls.add(itemRecycleViewControlVolume);

        ItemRecycleViewControl itemRecycleViewControlBasic = new ItemRecycleViewControl();
        itemRecycleViewControlBasic.typeView = TYPE_VIEW_CONTROL_APP_BASIC;
        itemRecycleViewControls.add(itemRecycleViewControlBasic);

        return itemRecycleViewControls;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder viewHolder = null;
        Log.d(TAG, "onCreateViewHolder:" + viewType);
        switch (viewType) {
            case TYPE_VIEW_CONTROL_WIFI_MEDIA:
                View viewwifi = inflater.inflate(R.layout.item_control_wifi_media, null);
                viewHolder = new MyViewHolderControllerWifiMedia(viewwifi);
                break;
            case TYPE_VIEW_CONTROL_VOLUME:
                View viewvolume = inflater.inflate(R.layout.item_control_volume, null);
                viewHolder = new MyViewHolderControllerVolume(viewvolume);
                ((MyViewHolderControllerVolume) viewHolder).setOnListenerScrollSeekBar(mOnListenerScrollSeekBar);
                break;
            case TYPE_VIEW_CONTROL_APP_BASIC:
                View viewbasic = inflater.inflate(R.layout.item_control_custom, null);
                viewHolder = new MyViewHolderControllerBasic(viewbasic);
                break;
            case TYPE_VIEW_CONTROL_APP_CUSTOM:
                View viewcustom = inflater.inflate(R.layout.item_control_custom, null);
                viewHolder = new MyViewHolderControllerCustom(viewcustom);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ItemRecycleViewControl itemRecycleViewControl = dataSet.get(position);
        int typeView = itemRecycleViewControl.typeView;
        //Logger.d(TAG,"onBindViewHolder typeview:"+typeView);
        switch (typeView) {
            case TYPE_VIEW_CONTROL_WIFI_MEDIA:

                MyViewHolderControllerWifiMedia myViewHolderControllerWifiMedia = (MyViewHolderControllerWifiMedia) holder;
                myViewHolderControllerWifiMedia.initMusicAppIcon();
                myViewHolderControllerWifiMedia.updateConnect();

                break;
            case TYPE_VIEW_CONTROL_VOLUME:
                MyViewHolderControllerVolume myViewHolderControllerVolume = (MyViewHolderControllerVolume) holder;
                myViewHolderControllerVolume.updateImageRotate();
                myViewHolderControllerVolume.initSeekBar();
                break;
            case TYPE_VIEW_CONTROL_APP_BASIC:
                break;
            case TYPE_VIEW_CONTROL_APP_CUSTOM:

                MyViewHolderControllerCustom myViewHolderControllerCustom = (MyViewHolderControllerCustom) holder;

                int page = position - 3;
                myViewHolderControllerCustom.setIndexPage(page);


                ArrayList<ApplicationInfo> listPageApps = new ArrayList<>();
                int size = listallapps.size();
                for (int i = 0; i < 4; i++) {
                    int index = page * 4 + i;
                    if (index < size) {
                        listPageApps.add(listallapps.get(index));
                    }

                }

                myViewHolderControllerCustom.setListAppPages(listPageApps);
                myViewHolderControllerCustom.displayIcon();

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        ItemRecycleViewControl itemRecycleViewControl = dataSet.get(position);
        return itemRecycleViewControl.typeView;
    }

    @Override
    public int getItemCount() {
        return dataSet != null ? dataSet.size() : 0;
    }

    private static class MyViewHolderBase extends RecyclerView.ViewHolder implements ViewMainListender, View.OnAttachStateChangeListener {

        protected MainPresenter mMainPresenter;
        protected DeviceMainHelper mDeviceMainHelper;
        protected Context mContext;
        protected boolean isAttachView = false;
        private View mItemView;

        public MyViewHolderBase(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mItemView = itemView;
            mDeviceMainHelper = new DeviceMainHelper(itemView.getContext());
            mMainPresenter = new MainPresenter(mDeviceMainHelper, this);
            mItemView.addOnAttachStateChangeListener(this);
            isAttachView = true;
        }

        public void disableParams(String action) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(action);
            mContext.sendBroadcast(intent);
        }


        public void checkPermisionWriteSetting() {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
        }

        @Override
        public void visibleWifi() {
        }

        @Override
        public void invisibleWifi() {
        }

        @Override
        public void visibleAirPlane() {
        }

        @Override
        public void invisibleAirPlane() {
        }

        @Override
        public void visibleBluetooth() {
        }

        @Override
        public void invisibleBluetooth() {
        }

        @Override
        public void visibleMobileData() {
        }

        @Override
        public void invisibleMobileData() {
        }

        @Override
        public void invisibleRotate() {
        }

        @Override
        public void visibleRotate() {
        }

        @Override
        public void visiableCamera() {
        }

        @Override
        public void visiableFlashLight() {
        }

        @Override
        public void invisiableFlashLight() {
        }

        @Override
        public void visiableSilent() {
        }

        @Override
        public void invisiableSilent() {
        }

        @Override
        public void onViewAttachedToWindow(View view) {
            isAttachView = true;

        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            isAttachView = false;


        }
    }

    public static class MyViewHolderControllerWifiMedia extends MyViewHolderBase implements View.OnClickListener, View.OnLongClickListener {

        ImageButton mImageButtonWifi;
        ImageButton mImageButtonBluetooth;
        ImageButton mImageButtonAirPlane;
        ImageButton mImageButtonSyncData;

        ImageButton mImageButtonSetting;
        ImageButton mImageButtonPrev;
        ImageButton mImageButtonPause;
        ImageButton mImageButtonNext;

        MusicMediaApp mMusicMediaApp = new MusicMediaApp();
        boolean mIsPlay = true;
        PackageManager mPackageManager;
        String musicPackageName;

        public MyViewHolderControllerWifiMedia(View itemView) {
            super(itemView);
            mImageButtonWifi = (ImageButton) itemView.findViewById(R.id.img_wifi);
            mImageButtonBluetooth = (ImageButton) itemView.findViewById(R.id.img_bluetooth);
            mImageButtonAirPlane = (ImageButton) itemView.findViewById(R.id.img_airplane);
            mImageButtonSyncData = (ImageButton) itemView.findViewById(R.id.img_syncdata);

            mImageButtonSetting = (ImageButton) itemView.findViewById(R.id.img_setting);
            mImageButtonPrev = (ImageButton) itemView.findViewById(R.id.img_prev);
            mImageButtonPause = (ImageButton) itemView.findViewById(R.id.img_pause);
            mImageButtonNext = (ImageButton) itemView.findViewById(R.id.img_next);

            mImageButtonWifi.setOnClickListener(this);
            mImageButtonBluetooth.setOnClickListener(this);
            mImageButtonAirPlane.setOnClickListener(this);
            mImageButtonSyncData.setOnClickListener(this);
            mImageButtonWifi.setOnLongClickListener(this);
            mImageButtonBluetooth.setOnLongClickListener(this);

            mImageButtonSetting.setOnClickListener(this);
            mImageButtonPrev.setOnClickListener(this);
            mImageButtonPause.setOnClickListener(this);
            mImageButtonNext.setOnClickListener(this);
            mImageButtonSetting.setOnLongClickListener(this);

            mPackageManager = itemView.getContext().getPackageManager();


        }

        @SuppressWarnings("deprecation")
        private void updateConnect() {
            WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled()) {

                mImageButtonWifi.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_wifi));


            } else {
                mImageButtonWifi.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_wifi_off));
            }
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
            } else {
                if (mBluetoothAdapter.isEnabled()) {
                    mImageButtonBluetooth.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bluetooth));
                } else {
                    mImageButtonBluetooth.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bluetooth_off));
                }
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (Settings.System.getInt(mContext.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, 0) != 0) {
                    mImageButtonAirPlane.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_airplane));
                } else {
                    mImageButtonAirPlane.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_airplane_off));
                }
            } else {
                if (Settings.Global.getInt(mContext.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, 0) != 0) {
                    mImageButtonAirPlane.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_airplane));
                } else {
                    mImageButtonAirPlane.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_airplane_off));
                }
            }

        }

        public void initMusicAppIcon() {
            musicPackageName = SharedPref.getInstance(mContext).getString(CustomMusicAdapter.MUSIC_APP, "");
            if (!TextUtils.isEmpty(musicPackageName)) {
                Drawable appIcon = null;
                try {
                    appIcon = mPackageManager.getApplicationIcon(musicPackageName);
                    mImageButtonSetting.setImageDrawable(appIcon);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        public void visibleBluetooth() {
            if (isAttachView) {
                mImageButtonBluetooth.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bluetooth));
            }
        }


        public void invisibleBluetooth() {
            if (isAttachView) {
                mImageButtonBluetooth.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bluetooth_off));
            }
        }

        public void visibleWifi() {
            if (isAttachView) {
                mImageButtonWifi.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_wifi));
            }

        }


        public void invisibleWifi() {
            if (isAttachView) {
                mImageButtonWifi.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_wifi_off));
            }
        }


        @Override
        public void onClick(View view) {
            initMusicAppIcon();
            int id = view.getId();
            switch (id) {
                case R.id.img_wifi:
                    mMainPresenter.turnOnOffWifi(mContext);
                    break;
                case R.id.img_bluetooth:
                    mMainPresenter.turnOnOffBluetooth(mContext);
                    break;
                case R.id.img_airplane:
                    mMainPresenter.turnOnOffAirPlane(mContext);
                    disableParams("ACTION_DISABLE");
                    break;
                case R.id.img_syncdata:
                    mMainPresenter.intentDataSync(mContext);
                    disableParams("ACTION_DISABLE");
                    break;
                case R.id.img_setting:
                    //start activity
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(musicPackageName);
                    if (launchIntent != null) {
                        mContext.startActivity(launchIntent);
                        Intent mIntent = new Intent();
                        mIntent.setAction("ACTION_DISABLE");
                        mContext.sendBroadcast(mIntent);
                    } else {
                        openAppMusic();
                    }
                    mIsPlay = true;
                    settingImgPausePlay();
                    break;
                case R.id.img_prev:
                    Logger.d(TAG, musicPackageName + "==========");
                    if (!TextUtils.isEmpty(musicPackageName)) {
                        mMusicMediaApp.prev(mContext, musicPackageName);
                    }
                    break;
                case R.id.img_pause:
                    Logger.d(TAG, musicPackageName + "==========");
                    if (!TextUtils.isEmpty(musicPackageName)) {
                        mMusicMediaApp.playOrPause(mContext, musicPackageName);
                        settingImgPausePlay();
                    }
                    break;
                case R.id.img_next:
                    Logger.d(TAG, musicPackageName + "==========");
                    if (!TextUtils.isEmpty(musicPackageName)) {
                        mMusicMediaApp.next(mContext, musicPackageName);
                    }
                    break;
            }
        }

        private void openAppMusic() {
            Intent intent = new Intent(mContext, MusicMangerActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            disableParams("ACTION_DISABLE");
        }

        private void settingImgPausePlay() {
            if (mIsPlay) {
                mIsPlay = false;
                mImageButtonPause.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_pause));
            } else {
                mIsPlay = true;
                mImageButtonPause.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_play));
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.img_wifi:
                    mMainPresenter.changeSettingWifi(mContext);
                    disableParams("ACTION_DISABLE");
                    break;
                case R.id.img_bluetooth:
                    mMainPresenter.changeBluetoothSettings(mContext);
                    disableParams("ACTION_DISABLE");
                    break;
                case R.id.img_setting:
                    Intent intent = new Intent(mContext, MusicMangerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    Intent mIntent = new Intent();
                    mIntent.setAction("ACTION_DISABLE");
                    mContext.sendBroadcast(mIntent);
                    break;
            }
            return true;
        }
    }

    public static class MyViewHolderControllerVolume extends MyViewHolderBase implements View.OnClickListener {

        ImageButton mImageButtonNight;
        Button mButtonScreenTimeout;

        ImageButton mImageButtonRotate;
        VerticalSeebar mSeeBarBrightness;
        VerticalSeebar mSeeBerVolume;
        ImageView imgVolume;
        ImageView imgLight;
        AudioManager mAudioManager;

        private VerticalSeebar.OnListenerScrollSeekBar mOnListenerScrollSeekBar;

        public MyViewHolderControllerVolume(View itemView) {
            super(itemView);
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

            mImageButtonRotate = (ImageButton) itemView.findViewById(R.id.img_rotate);
            mImageButtonNight = (ImageButton) itemView.findViewById(R.id.img_night);
            mSeeBarBrightness = (VerticalSeebar) itemView.findViewById(R.id.seebarlight);
            mSeeBerVolume = (VerticalSeebar) itemView.findViewById(R.id.seebarVolume);
            mButtonScreenTimeout = (Button) itemView.findViewById(R.id.btnscreentimeout);
            imgVolume = (ImageView) itemView.findViewById(R.id.img_volume);
            imgLight = (ImageView) itemView.findViewById(R.id.img_light);

            initSeekBar();
            mImageButtonRotate.setOnClickListener(this);
            mButtonScreenTimeout.setOnClickListener(this);
            mImageButtonNight.setOnClickListener(this);

            mSeeBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    disableParams("ACTION_SCROLL_OFF");
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    disableParams("ACTION_SCROLL_ON");
                    Bundle bundle = new Bundle();
                    bundle.putInt("progress", seekBar.getProgress());
                    ChangeActivity.startChangeActivity(mContext, ChangeActivity.ACTION_CHANGE_BRIGHTNESS, bundle);
                    updateImageBright();
                }
            });



            mSeeBerVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    Logger.d(TAG, "onProgressChanged:" + progress);
                    mMainPresenter.setVolumeControls(mContext, mAudioManager, progress);
                    updateImageVolume();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    disableParams("ACTION_SCROLL_OFF");
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Logger.d(TAG, "onStopTrackingTouch:" + seekBar.getProgress());
                    disableParams("ACTION_SCROLL_ON");
                }
            });

            mSeeBerVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            mSeeBerVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }

        public void setOnListenerScrollSeekBar(VerticalSeebar.OnListenerScrollSeekBar onListenerScrollSeekBar) {
            this.mOnListenerScrollSeekBar = onListenerScrollSeekBar;

            mSeeBarBrightness.setOnListenerScrollSeekBar(mOnListenerScrollSeekBar);
            mSeeBerVolume.setOnListenerScrollSeekBar(mOnListenerScrollSeekBar);
        }

        public void initSeekBar() {
            mSeeBerVolume.setKeyProgressIncrement(1);
            mSeeBarBrightness.setKeyProgressIncrement(1);
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            mSeeBerVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            mSeeBerVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            int mBright = mMainPresenter.getBrightness(mContext, mContext.getContentResolver());
            mSeeBarBrightness.setProgress(mBright);
            updateImageVolume();
            updateImageBright();
        }

        private void updateImageRotate() {
            if (android.provider.Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mImageButtonRotate.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_lock_orientation));
                    mImageButtonRotate.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_frame_on));
                } else {
                    mImageButtonRotate.setImageResource(R.drawable.ic_lock_orientation);
                    mImageButtonRotate.setBackgroundResource(R.drawable.ic_frame_on);
                }

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mImageButtonRotate.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_lock_orientation_off));
                    mImageButtonRotate.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_frame_off));
                } else {
                    mImageButtonRotate.setImageResource(R.drawable.ic_lock_orientation_off);
                    mImageButtonRotate.setBackgroundResource(R.drawable.ic_frame_off);
                }
            }
        }

        private void updateImageBright() {
            float progress = (float) mSeeBarBrightness.getProgress() / mSeeBarBrightness.getMax();
            if (progress <= 0.25) {
                imgLight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_light_min));
            } else {
                imgLight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_light_max));
            }

        }

        public void updateImageVolume() {
            float progress = (float) mSeeBerVolume.getProgress() / mSeeBerVolume.getMax();
            if (progress == 0) {
                imgVolume.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_mute));
            } else if (progress <= 0.25) {
                imgVolume.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_min));
            } else if (progress <= 0.5) {
                imgVolume.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_medium));
            } else if (progress <= 1) {
                imgVolume.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_volume_max));
            }
        }

        private void showDialogTimeout() {
            final Dialog mDialog = new Dialog(mContext);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.layout_screentimeout);
            if (Build.VERSION.SDK_INT < 26) {
                mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            } else {
                mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            }
            mDialog.getWindow().setGravity(Gravity.CENTER);

            RecyclerView mRVTimeout = mDialog.findViewById(R.id.recycle_timeout);
            mRVTimeout.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            mRVTimeout.setLayoutManager(layoutManager);
            mRVTimeout.setItemAnimator(new DefaultItemAnimator());

            List<MyTimeOut> myTimeOuts = MyTimeOutData.addTimeOutData();

            MyTimeOutAdapter myTimeOutAdapter = new MyTimeOutAdapter(mContext, myTimeOuts);
            mRVTimeout.setAdapter(myTimeOutAdapter);
            mDialog.show();
        }

        @Override
        public void onClick(View view) {
            {
                int id = view.getId();
                switch (id) {

                    case R.id.img_rotate:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (!Settings.System.canWrite(mContext)) {
                                checkPermisionWriteSetting();
                            } else {
                                mMainPresenter.turnOnOffRotate(mContext, mContext.getContentResolver());
                                updateImageRotate();
                            }
                        } else {
                            mMainPresenter.turnOnOffRotate(mContext, mContext.getContentResolver());
                            updateImageRotate();
                        }
                        break;
                    case R.id.btnscreentimeout:
                        showDialogTimeout();
                        break;
                    case R.id.img_night:
                        mMainPresenter.turnOnorOffSilent(mContext);
                        disableParams("ACTION_SILENT");
                        break;
                }
            }
        }

        @Override
        public void invisibleRotate() {
            if (isAttachView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mImageButtonRotate.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_frame_on));
                } else {
                    mImageButtonRotate.setBackgroundResource(R.drawable.ic_frame_on);
                }
            }
        }

        @Override
        public void visibleRotate() {
            if (isAttachView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mImageButtonRotate.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_frame_off));
                } else {
                    mImageButtonRotate.setBackgroundResource(R.drawable.ic_frame_off);
                }
            }
        }

        public void visiableSilent() {
            if (isAttachView) {
                mImageButtonNight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_night));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mImageButtonNight.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_frame_on));
                } else {
                    mImageButtonNight.setBackgroundResource(R.drawable.ic_frame_on);
                }
            }
        }


        public void invisiableSilent() {
            if (isAttachView) {
                mImageButtonNight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_night_off));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mImageButtonNight.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_frame_off));
                } else {

                    mImageButtonNight.setBackgroundResource(R.drawable.ic_frame_off);
                }
            }
        }
    }

    public static class MyViewHolderControllerCustom extends MyViewHolderControllerBasic {

        private ArrayList<ApplicationInfo> listAppPages;
        private int indexPage;

        public MyViewHolderControllerCustom(View itemView) {
            super(itemView);

        }

        public void setListAppPages(ArrayList<ApplicationInfo> listAppPages) {
            this.listAppPages = listAppPages;
        }

        public void setIndexPage(int postionPage) {
            indexPage = postionPage;

            // visible, invisible into end page

        }

        public void displayIcon() {

            int count = listAppPages.size();

            for (int i = 0; i < 4; i++) {

                if (i < count) {
                    visibleIcon(i);
                    displayIcon(listAppPages.get(i), i);
                } else {
                    hideIcon(i);
                }
            }


        }

        private void onClick(ApplicationInfo applicationInfo) {
            //start activit
            Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName);
            if (launchIntent != null) {
                mContext.startActivity(launchIntent);
                disableParams("ACTION_DISABLE");
            }

        }

        private ApplicationInfo getApplicationInfo(int postion) {
            return listAppPages.get(postion);
        }

        ;

        @Override
        public void onClickItem0() {
            onClick(getApplicationInfo(0));
        }

        @Override
        public void onClickItem1() {
            onClick(getApplicationInfo(1));
        }

        @Override
        public void onClickItem2() {
            onClick(getApplicationInfo(2));
        }

        @Override
        public void onClickItem3() {
            onClick(getApplicationInfo(3));
        }
    }


    public static class MyViewHolderControllerBasic extends MyViewHolderBase implements View.OnClickListener {
        ImageButton mImageButtonFlashLight; // 0
        ImageButton mImageButtonCamera; //3
        ImageButton mImageButtonCalendar; //1
        ImageButton mImageButtonCalculator; //2
        PackageManager mPackageManager;

        public MyViewHolderControllerBasic(View itemView) {
            super(itemView);

            mImageButtonFlashLight = itemView.findViewById(R.id.img_flashlight);
            mImageButtonCamera = itemView.findViewById(R.id.img_camera);
            mImageButtonCalculator = itemView.findViewById(R.id.img_calculator);
            mImageButtonCalendar = itemView.findViewById(R.id.img_clock);

            mImageButtonCalendar.setOnClickListener(this);
            mImageButtonFlashLight.setOnClickListener(this);
            mImageButtonCamera.setOnClickListener(this);
            mImageButtonCalculator.setOnClickListener(this);
            mPackageManager = itemView.getContext().getPackageManager();
        }


        private void displayIconApp(ImageButton view, ApplicationInfo applicationInfo) {
            Drawable appIcon = null;
            try {
                appIcon = mPackageManager.getApplicationIcon(applicationInfo.packageName);
                view.setImageDrawable(appIcon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }


        protected void displayIcon(ApplicationInfo applicationInfo, int positon) {
            switch (positon) {
                case 0:
                    displayIconApp(mImageButtonFlashLight, applicationInfo);

                    break;
                case 1:
                    displayIconApp(mImageButtonCalendar, applicationInfo);
                    break;
                case 2:
                    displayIconApp(mImageButtonCalculator, applicationInfo);

                    break;
                case 3:
                    displayIconApp(mImageButtonCamera, applicationInfo);
                    break;
            }
        }

        protected void hideIcon(int positon) {
            switch (positon) {
                case 0:
                    mImageButtonFlashLight.setVisibility(View.GONE);
                    break;
                case 1:
                    mImageButtonCalendar.setVisibility(View.GONE);
                    break;
                case 2:

                    mImageButtonCalculator.setVisibility(View.GONE);
                    break;
                case 3:
                    mImageButtonCamera.setVisibility(View.GONE);
                    break;
            }
        }

        protected void visibleIcon(int positon) {
            switch (positon) {
                case 0:
                    mImageButtonFlashLight.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    mImageButtonCalendar.setVisibility(View.VISIBLE);
                    break;
                case 2:

                    mImageButtonCalculator.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    mImageButtonCamera.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void visiableFlashLight() {
            if (isAttachView) {
                mImageButtonFlashLight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_flashlight_on));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    mImageButtonFlashLight.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_frame_on));
//                } else {
//                    mImageButtonFlashLight.setBackgroundResource(R.drawable.ic_frame_on);
//                }
            }
        }

        @Override
        public void invisiableFlashLight() {
            if (isAttachView) {
                mImageButtonFlashLight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_flashlight));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mImageButtonFlashLight.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_frame_off));
                } else {

                    mImageButtonFlashLight.setBackgroundResource(R.drawable.ic_frame_off);
                }
            }
        }

        public void onClickItem0() {
            try {
                mMainPresenter.turnOnOffFlashLight(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "You are doing Camera", Toast.LENGTH_SHORT).show();
            }
        }

        public void onClickItem1() {

            Logger.d(TAG, "LICH=================");
            mMainPresenter.openCalendar(mContext);
            disableParams("ACTION_DISABLE");

        }

        public void onClickItem2() {

            mMainPresenter.openCalculator(mContext);
            disableParams("ACTION_DISABLE");
        }

        public void onClickItem3() {
            ChangeActivity.startChangeActivity(mContext, ChangeActivity.ACTION_CHANGE_CAMERA, null);
            disableParams("ACTION_DISABLE");
        }


        @Override
        public void onClick(View view) {
            Logger.d(TAG, "onClick =================");
            int id = view.getId();
            switch (id) {
                case R.id.img_clock:
                    onClickItem1();
                    break;
                case R.id.img_calculator:
                    onClickItem2();


                    break;
                case R.id.img_flashlight:
                    onClickItem0();
                    break;
                case R.id.img_camera:
                    onClickItem3();
                    break;
            }
        }
    }
}