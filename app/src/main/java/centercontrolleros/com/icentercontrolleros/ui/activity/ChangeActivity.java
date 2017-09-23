package centercontrolleros.com.icentercontrolleros.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import centercontrolleros.com.icentercontrolleros.model.DeviceMainHelper;
import centercontrolleros.com.icentercontrolleros.presenter.MainPresenter;
import centercontrolleros.com.icentercontrolleros.ui.service.MainService;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;
import centercontrolleros.com.icentercontrolleros.ui.view.ViewMainListender;

/**
 * Created by Duy on 8/30/2017.
 */

public class ChangeActivity extends Activity {
    public final static int ACTION_CHANGE_BRIGHTNESS = 20;
    public final static int ACTION_FINISH_TRANPARENT = 21;
    public final static int ACTION_CHANGE_CAMERA = 23;
    public final static int PERMISSION_ALL = 1;
    public final String TAG = ChangeActivity.class.getSimpleName();
    public final int REQUEST_CODE_WRITE_SETTINGS = 100;
    Window mWindow;
    ContentResolver mContentResolver;
    DeviceMainHelper mDeviceMainHelper;
    MainPresenter mMainPresenter;
    Context mContext;
    ViewMainListender mViewMainListender = new ViewMainListender() {
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
    };

    public static void startChangeActivity(Context context, int action, Bundle data) {
        Intent intent = new Intent(context, ChangeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Action_change", action);
        if (data != null)
            intent.putExtra("data", data);
        context.startActivity(intent);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        disableTouch();
        Logger.d(TAG, "onCreate...");
        init();
        Intent intent = getIntent();
        parserData(intent);
    }

    private void init() {
        mWindow = getWindow();
        mContentResolver = getContentResolver();
        mDeviceMainHelper = new DeviceMainHelper(this);
        mMainPresenter = new MainPresenter(mDeviceMainHelper, mViewMainListender);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        disableTouch();
        Logger.d(TAG, "onNewIntent...");
        parserData(intent);
    }

    private void parserData(Intent intent) {
        int action = 0;

        if (intent != null)
            action = intent.getIntExtra("Action_change", 0);
        switch (action) {
            case ACTION_CHANGE_BRIGHTNESS:
                Bundle bundle = intent.getBundleExtra("data");
                int progress = bundle.getInt("progress", 0);
                Logger.d(TAG, progress + ".....");
                changeBrightness(progress);
                break;
            case ACTION_FINISH_TRANPARENT:
                Bundle bundleFinish = intent.getBundleExtra("data");
                boolean isCheck = bundleFinish.getBoolean("On", false);
                if (isCheck) {
                    finish();
                }
                break;
            case ACTION_CHANGE_CAMERA:
                startCamera();
                break;
        }
    }

    public void startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            requestPermision();
        } else {
            mMainPresenter.turnOnOffCamera(this);
        }
        finish();
    }

    private void disableTouch() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void changeBrightness(int progress) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                checkPermisionWriteSetting();
            } else {
                mMainPresenter.updateBrightness(getApplicationContext(), mWindow, mContentResolver, progress);
            }
        } else {
            mMainPresenter.updateBrightness(getApplicationContext(), mWindow, mContentResolver, progress);
        }
        finish();
    }

    private void requestPermision() {

        String[] PERMISSIONS = {Manifest.permission.CAMERA};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_WRITE_SETTINGS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(this)) {
                        checkPermisionWriteSetting();
                    }
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Logger.d(TAG,"Show in dialog");
        if (requestCode == PERMISSION_ALL) {
            Logger.d(TAG, grantResults[0] + "");
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMainPresenter.turnOnOffCamera(this);
            } else {
                requestPermision();
            }
        }
    }

    private void checkPermisionWriteSetting() {
        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
