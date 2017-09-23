package centercontrolleros.com.icentercontrolleros.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.model.SettingEnableHelper;
import centercontrolleros.com.icentercontrolleros.ui.service.MainService;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;

public class MainActivity extends AppCompatActivity {
    public final static int Overlay_REQUEST_CODE = 251;
    public final static int REQUEST_CODE_WRITE_SETTINGS = 200;
    private final static String TAG = MainActivity.class.getSimpleName();

    Switch switchPower;
    private Activity mActivity;
    private SettingEnableHelper mSettingEnableHelper;
    private RelativeLayout relativeLayout_add_app;
    private RelativeLayout relativeLayout_music, relativeLayout_setting;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        addControls();
        addEvents();
    }

    private void addEvents() {
        mSettingEnableHelper = new SettingEnableHelper(this);

        switchPower.setChecked(mSettingEnableHelper.isEnableControlCenter());

        if (mSettingEnableHelper.isEnableControlCenter()) {
            MainService.enableControlCenter(this);
        }

        switchPower.setChecked(true);
        mSettingEnableHelper.enableControlCenter();
        checkDrawOverlayPermission();
        //Thay đổi trạng thái của switch theo service
        switchPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                if (b) {
                    //on
                    mSettingEnableHelper.enableControlCenter();
                    checkDrawOverlayPermission();
                } else {
                    //off
                    mSettingEnableHelper.disableControlCenter();
                    MainService.disableControlCenter(MainActivity.this);
                }
            }
        });

        relativeLayout_add_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ManagerAppActivity.class);
                startActivity(intent);
            }
        });

        relativeLayout_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MusicMangerActivity.class);
                startActivity(intent);
            }
        });

        relativeLayout_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        switchPower = (Switch) findViewById(R.id.switch_power);
        relativeLayout_add_app = (RelativeLayout) findViewById(R.id.relativeLayout_add_app);
        relativeLayout_music = (RelativeLayout) findViewById(R.id.relativeLayout_music);
        relativeLayout_setting = (RelativeLayout) findViewById(R.id.relativeLayout_setting);

    }

    private void checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.System.canWrite(this)) {
                checkpermission();
            }
            if (!Settings.canDrawOverlays(mActivity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, Overlay_REQUEST_CODE);
            }
            if (Settings.System.canWrite(this) && Settings.canDrawOverlays(mActivity)) {
                openFloatingWindow();
            }
        } else {
            openFloatingWindow();
        }
    }

    private void checkpermission() {
        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
    }

    private void openFloatingWindow() {

        Logger.d(TAG, "openFloatingWindow...");
        MainService.enableControlCenter(MainActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Overlay_REQUEST_CODE: {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(mActivity) && Settings.System.canWrite(this)) {
                        openFloatingWindow();
                    } else {
                    }
                } else {
                    openFloatingWindow();
                }
                break;
            }
            case REQUEST_CODE_WRITE_SETTINGS:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.System.canWrite(this) && Settings.canDrawOverlays(mActivity)) {
                        openFloatingWindow();
                    }
                } else {
                    openFloatingWindow();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!mSettingEnableHelper.isEnableControlCenter()) {
            stopMainService();
        }
    }

    @Override
    protected void onRestart() {
        checkDrawOverlayPermission();
        super.onRestart();
    }

    private void stopMainService() {
        Intent intent = new Intent(this, MainService.class);
        stopService(intent);
    }

}
