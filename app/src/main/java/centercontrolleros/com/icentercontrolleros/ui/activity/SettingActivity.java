package centercontrolleros.com.icentercontrolleros.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;

/**
 * Created by VuDuc on 9/19/2017.
 */

public class SettingActivity extends PreferenceActivity {

    public static final String BOTTOM_POSITION_CHANGE = "BOTTOM_POSITION_CHANGE";
    public static final String BOTTOM_SIZE_CHANGE = "BOTTOM_SIZE_CHANGE";
    public static final String BACKGROUND_CHANGE = "BACKGROUND_CHANGE";
    private static final String TAG = SettingActivity.class.getSimpleName();
    private ListPreference prefUpdatePosion;
    private ListPreference prefUpdateSize;
    private ListPreference prefUpdateBackground;
    private Context mContext;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        addPreferencesFromResource(R.xml.pref_bottom_settings);
        addControls();
        addEvents();
    }

    private void addEvents() {
        prefUpdatePosion.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Logger.d(TAG, "prefUpdatePosion........");
                Intent intent = new Intent();
                intent.setAction(BOTTOM_POSITION_CHANGE);
                mContext.sendBroadcast(intent);
                return true;
            }
        });

        prefUpdateSize.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Logger.d(TAG, "mSeekBarPreference........");
                Intent intent = new Intent();
                intent.setAction(BOTTOM_SIZE_CHANGE);
                mContext.sendBroadcast(intent);
                return true;
            }
        });

        prefUpdateBackground.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Intent intent = new Intent();
                intent.setAction(BACKGROUND_CHANGE);
                mContext.sendBroadcast(intent);
                return true;
            }
        });
    }

    private void addControls() {
        prefUpdatePosion = (ListPreference) findPreference("prefUpdatePosion");
        prefUpdateSize = (ListPreference) findPreference("prefUpdateSize");
        prefUpdateBackground = (ListPreference) findPreference("prefUpdateBackground");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
