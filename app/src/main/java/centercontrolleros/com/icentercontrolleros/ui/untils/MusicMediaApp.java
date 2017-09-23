package centercontrolleros.com.icentercontrolleros.ui.untils;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * Created by VuDuc on 9/6/2017.
 */

public class MusicMediaApp {
    public void sendKeyPressBroadcastParcelable(Context context, int keycode, String packageName) {
        sendKeyPressBroadcastParcelable(context, KeyEvent.ACTION_DOWN, keycode, packageName);
        sendKeyPressBroadcastParcelable(context, KeyEvent.ACTION_UP, keycode, packageName);
    }

    public void sendKeyPressBroadcastParcelable(Context context, int action, int keycode, String packageName) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(0, 0, action, keycode, 0));
        if (packageName != null)
            intent.setPackage(packageName);

        context.sendOrderedBroadcast(intent, null);
    }

    public void playOrPause(Context context,String pkgName){
        sendKeyPressBroadcastParcelable(context,KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,pkgName);
    }

    public void next(Context context,String pkgName){
        sendKeyPressBroadcastParcelable(context,KeyEvent.KEYCODE_MEDIA_NEXT,pkgName);
    }

    public void prev(Context context,String pkgName){
        sendKeyPressBroadcastParcelable(context,KeyEvent.KEYCODE_MEDIA_PREVIOUS,pkgName);
    }
}
