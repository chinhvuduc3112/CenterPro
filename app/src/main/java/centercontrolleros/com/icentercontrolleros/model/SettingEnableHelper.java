package centercontrolleros.com.icentercontrolleros.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by VuDuc on 8/30/2017.
 */

public class SettingEnableHelper {
    private static final String SWITCH_STATE = "SwitchState";
    SharedPreferences sharedPreferences;
    private Context context;

    public SettingEnableHelper(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        sharedPreferences = context.getSharedPreferences("SwitchStatus", Context.MODE_PRIVATE);
    }

    public void enableControlCenter() {
        sharedPreferences.edit().putBoolean(SWITCH_STATE, true).commit();
    }

    public void disableControlCenter() {
        sharedPreferences.edit().putBoolean(SWITCH_STATE, false).commit();
    }

    public boolean isEnableControlCenter() {
        return sharedPreferences.getBoolean(SWITCH_STATE, false);
    }
}
