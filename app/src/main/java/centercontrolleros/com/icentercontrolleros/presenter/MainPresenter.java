package centercontrolleros.com.icentercontrolleros.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.view.Window;

import centercontrolleros.com.icentercontrolleros.model.DeviceMainHelper;
import centercontrolleros.com.icentercontrolleros.ui.view.ViewMainListender;

/**
 * Created by Duy on 8/28/2017.
 */

public class MainPresenter implements DeviceMainHelper.OnChangeMainDevice {
    boolean isEnableWifi = false;
    private DeviceMainHelper mDeviceHelper;
    private ViewMainListender viewUserListender;


    public MainPresenter(DeviceMainHelper deviceHelper, ViewMainListender mViewMainListender) {
        this.mDeviceHelper = deviceHelper;
        this.viewUserListender = mViewMainListender;
        mDeviceHelper.changeMainState(this);

    }

    public void intentDataSync(Context context) {
        mDeviceHelper.dataSynchronization(context);
    }

    public void changeAutoBright(Context context, boolean value) {
        mDeviceHelper.autoBrightness(context, value);
    }

    public void turnOnSettingsSystem(Context context) {
        mDeviceHelper.changeSettingsSystem(context);
    }

    public void changeSettingWifi(Context context) {
        mDeviceHelper.turnWifiSetting(context);
    }

    public void turnOnOffWifi(Context context) {
        mDeviceHelper.turnWifi(context);
    }

    public void turnOnOffAirPlane(Context context) {
        mDeviceHelper.turnAirPlanemode(context);
    }

    public void turnOnOffBluetooth(Context context) {
        mDeviceHelper.turnOnOffBluetooth();
    }

    public void turnOnOffMobileData(Context context) {
        mDeviceHelper.turnMobileData(context);
    }

    public void displayTimeout(Context context, long address) {
        mDeviceHelper.changeIconTimeOut(context, address);
    }

    public void turnOnOffRotate(Context context, ContentResolver contentResolver) {
        mDeviceHelper.turnRotate(context);
    }

    public void turnOnOffCamera(Context context) {
        mDeviceHelper.turnCamera(context);
    }

    public int getBrightness(Context context, ContentResolver contentResolver) {
        return mDeviceHelper.getBrightness(context, contentResolver);
    }

    public void updateBrightness(Context context, Window window, ContentResolver contentResolver, int brightness) {

        mDeviceHelper.changeBrightness(context, contentResolver, window, brightness);

    }
    public void setVolumeControls(Context context, AudioManager mAudioManager, int progress) {
        mDeviceHelper.changVolume(context, mAudioManager, progress);
    }
    public void changeBluetoothSettings(Context context) {
        mDeviceHelper.changeSettingBluetooth(context);
    }

    public void turnOnorOffSilent(Context context){
        mDeviceHelper.turnSilentMode(context);
    }

    public void turnOnOffFlashLight(Context context) {
        mDeviceHelper.turnFlashLight(context);
    }

    public void turnOffFlashLight(Context mContext) {
        mDeviceHelper.changeModeOffFlash(mContext);
    }

    public void openCalculator(Context context) {
        mDeviceHelper.turnCalculator(context);
    }

    public void openCalendar(Context context) {
        mDeviceHelper.turnCalendar(context);
    }

    @Override
    public void stateOnWifi() {
        viewUserListender.invisibleWifi();
    }

    @Override
    public void stateOffWifi() {

        viewUserListender.visibleWifi();
    }

    @Override
    public void stateModeOnAirPlane() {
        viewUserListender.invisibleAirPlane();
    }

    @Override
    public void stateModeOffAirPlane() {
        viewUserListender.visibleAirPlane();
    }

    @Override
    public void stateModeOnBluetooth() {
        viewUserListender.visibleBluetooth();
    }

    @Override
    public void stateModeOffBluetooth() {
        viewUserListender.invisibleBluetooth();
    }

    @Override
    public void stateModeOnMobileData() {
        viewUserListender.visibleMobileData();
    }

    @Override
    public void stateModeOffMobileData() {
        viewUserListender.invisibleMobileData();
    }

    @Override
    public void stateModeOnRotate() {
        viewUserListender.invisibleRotate();
    }

    @Override
    public void stateModeOffRotate() {
        viewUserListender.visibleRotate();
    }

    @Override
    public void stateModeCamera() {
        viewUserListender.visiableCamera();
    }

    @Override
    public void stateModeOnFlashLight() {
        viewUserListender.visiableFlashLight();
    }

    @Override
    public void stateModeOffFlashLight() {
        viewUserListender.invisiableFlashLight();
    }

    @Override
    public void stateModeOnSilent() {
        viewUserListender.visiableSilent();
    }

    @Override
    public void stateModeOffSilent() {
        viewUserListender.invisiableSilent();
    }


}
