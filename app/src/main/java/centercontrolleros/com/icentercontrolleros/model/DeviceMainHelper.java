package centercontrolleros.com.icentercontrolleros.model;

import android.Manifest;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Duy on 8/28/2017.
 */

public class DeviceMainHelper {
    private int brightness;
    private Context mContext;
    private OnChangeMainDevice mOnChangeDevice;
    private WifiManager mWifiManager;
    private boolean toggle = false;
    private boolean enabled = false;
    private boolean mIsSilent = false;
//    Permission permission;
    private Camera camera = null;


    //Window object, that will store a reference to the current window
    public DeviceMainHelper(Context context) {
        this.mContext = context;
//        permission = new Permission();
    }

    public void changeMainState(OnChangeMainDevice onChangeDevice) {
        this.mOnChangeDevice = onChangeDevice;
    }

    public void changeIconTimeOut(Context context, long deplay) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, (int) deplay);

    }
    public void changVolume(Context context, AudioManager mAudioManager, int progress) {

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

    }

    public void turnSilentMode(Context context){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager n = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (n.isNotificationPolicyAccessGranted()) {
                modeSilent(context);
            } else {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                context.startActivity(intent);
            }
        } else {
            modeSilent(context);
        }
    }
    private void modeSilent(Context context){
        mIsSilent = !mIsSilent;
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mIsSilent) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            mOnChangeDevice.stateModeOnSilent();
        } else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mOnChangeDevice.stateModeOffSilent();
        }
    }

    public void turnWifi(Context context) {
        if (context != null) {
            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration wc = new WifiConfiguration();
            wc.SSID = "\"YOUR_SSID\"";
            wc.preSharedKey = "\"YOUR_PASSWORD\"";
            wc.status = WifiConfiguration.Status.ENABLED;
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            int netId = mWifiManager.addNetwork(wc);
            mWifiManager.setWifiEnabled(true);
            boolean isEnablel = mWifiManager.isWifiEnabled();
            if (isEnablel) {
                mWifiManager.enableNetwork(netId, true);
                mOnChangeDevice.stateOnWifi();
                mWifiManager.setWifiEnabled(false);
            } else {
                mOnChangeDevice.stateOffWifi();
            }
        }
    }

    public void dataSynchronization(Context context) {
        try {
            openSettings(context, Settings.ACTION_SYNC_SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnWifiSetting(Context context) {
        try {
            openSettings(context, Settings.ACTION_WIFI_SETTINGS);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    public int getBrightness(Context context, ContentResolver cResolver) {
        if (context != null) {
            cResolver = context.getContentResolver();
            try {
                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return brightness;
        }
        return 0;
    }

    public void changeSettingsSystem(Context context) {
        try {
            openSettings(context, Settings.ACTION_SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void changeBrightness(Context context, ContentResolver cResolver, Window window, int brightness) {
        if (context != null) {
            cResolver = context.getContentResolver();
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);

            WindowManager.LayoutParams layoutpars = window.getAttributes();
            layoutpars.screenBrightness = (float) brightness / 255;
            window.setAttributes(layoutpars);
        }

    }

    public void turnAirPlanemode(Context context) {
        try {
            openSettings(context, Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openSettings(Context context, String action) {
        if (context != null) {
            Intent intent = new Intent(action);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    public void turnOnOffBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

        } else {
            boolean isEnabled = bluetoothAdapter.isEnabled();
            if (!isEnabled) {
                bluetoothAdapter.enable();
                mOnChangeDevice.stateModeOnBluetooth();
            } else if (isEnabled) {
                bluetoothAdapter.disable();
                mOnChangeDevice.stateModeOffBluetooth();

            }
        }
    }

    public void changeSettingBluetooth(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

        } else {
            openSettings(context, Settings.ACTION_BLUETOOTH_SETTINGS);
        }
    }

    public void turnCamera(Context context) {
        boolean isSupportCamera = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        openSettings(context,"android.media.action.IMAGE_CAPTURE");
        if (isSupportCamera) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            } else {
                openSettings(context, "android.media.action.IMAGE_CAPTURE");
            }
        }
    }

    public void turnMobileData(Context context) {

        openSettings(context, Settings.ACTION_DATA_ROAMING_SETTINGS);

    }

    public void autoBrightness(Context context, boolean value) {
        if (value) {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        } else {

            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
    }

    public void turnRotate(Context context) {
        enabled = !enabled;
        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
        if (android.provider.Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1)
            mOnChangeDevice.stateModeOnRotate();
        else
            mOnChangeDevice.stateModeOffRotate();
    }
    @SuppressWarnings("deprecation")
    public void turnFlashLight(Context context) {
        boolean isFlash = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (isFlash) {
            toggle = !toggle;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                    if (cameraManager != null && cameraManager.getCameraIdList().length > 0) {
                        if (cameraManager.getCameraCharacteristics(cameraManager.getCameraIdList()[0]).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                            cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], toggle);
                        }
                    }
                    if (toggle) {
                        mOnChangeDevice.stateModeOnFlashLight();
                    } else {
                        mOnChangeDevice.stateModeOffFlashLight();
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            } else {

                if (toggle) {
                    if (camera == null) {
                        camera = Camera.open();
                    }
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();
                    mOnChangeDevice.stateModeOnFlashLight();

                } else {
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.stopPreview();
                    if (camera != null) {
                        camera.release();
                        camera = null;
                    }
                    mOnChangeDevice.stateModeOffFlashLight();
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void changeModeOffFlash(Context context) {
        boolean isFlash = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (isFlash) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                    if (cameraManager != null && cameraManager.getCameraIdList().length > 0) {
                        if (cameraManager.getCameraCharacteristics(cameraManager.getCameraIdList()[0]).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                            cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], false);
                        }
                    }

                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            } else {
                if (camera == null) {
                    camera = Camera.open();
                }
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                camera.stopPreview();
                if (camera != null) {
                    camera.release();
                    camera = null;
                }
            }

            toggle = false;
            mOnChangeDevice.stateModeOffFlashLight();
        }
    }


    public void turnCalculator(Context context) {
        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

        final PackageManager pm = context.getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        for (PackageInfo pi : packs) {
            if (pi.packageName.toString().toLowerCase().contains("calcul")) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("appName", pi.applicationInfo.loadLabel(pm));
                map.put("packageName", pi.packageName);
                items.add(map);
            }
        }
        if (items.size() >= 1) {
            String packageName = (String) items.get(0).get("packageName");
            Intent i = pm.getLaunchIntentForPackage(packageName);
            if (i != null)
                context.startActivity(i);
        } else {
        }
    }

    public void turnCalendar(Context context) {
        openSettings(context, AlarmClock.ACTION_SHOW_ALARMS);
    }

    public interface OnChangeMainDevice {
        void stateOnWifi();

        void stateOffWifi();

        void stateModeOnAirPlane();

        void stateModeOffAirPlane();

        void stateModeOnBluetooth();

        void stateModeOffBluetooth();

        void stateModeOnMobileData();

        void stateModeOffMobileData();

        void stateModeOnRotate();

        void stateModeOffRotate();

        void stateModeCamera();

        void stateModeOnFlashLight();

        void stateModeOffFlashLight();

        void stateModeOnSilent();
        void stateModeOffSilent();

    }
}
