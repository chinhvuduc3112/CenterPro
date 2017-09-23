package centercontrolleros.com.icentercontrolleros.ui.untils;

import android.util.Log;

/**
 * Created by VuDuc on 8/30/2017.
 */

public class Logger {
    private final static boolean DEBUG = true;

    public static void d(String tag, String msg) {
        if (DEBUG)
            Log.d(tag, msg);
    }
}
