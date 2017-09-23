package centercontrolleros.com.icentercontrolleros.ui.ReOrderListenner;

import android.content.pm.ApplicationInfo;
import java.util.List;

import centercontrolleros.com.icentercontrolleros.model.AppInfor;

public interface OnCustomerListChangedListener {
    void onNoteListChanged(List<AppInfor> favouriteApps);
}
