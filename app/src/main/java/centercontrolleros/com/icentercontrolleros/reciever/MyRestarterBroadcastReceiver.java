package centercontrolleros.com.icentercontrolleros.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import centercontrolleros.com.icentercontrolleros.model.SettingEnableHelper;
import centercontrolleros.com.icentercontrolleros.ui.service.MainService;

/**
 * Created by VuDuc on 8/18/2017.
 */

public class MyRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SettingEnableHelper setttingHelper = new SettingEnableHelper(context);

        if (setttingHelper.isEnableControlCenter())
            MainService.enableControlCenter(context);
    }
}
