package centercontrolleros.com.icentercontrolleros.ui.model;

import android.content.pm.ApplicationInfo;

import java.util.ArrayList;

/**
 * Created by mr.logic on 9/1/2017.
 */

public class ItemRecycleViewControl {

    public int typeView;

    public static ApplicationInfo getAppInfor(ArrayList<ApplicationInfo> listApps, int indexpage, int position){

        int index =  Math.min(indexpage*4 + position, listApps.size() -1 );

        ApplicationInfo info = listApps.get(index);
        return info;
    }

}
