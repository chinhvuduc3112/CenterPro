package centercontrolleros.com.icentercontrolleros.ui.model;

import android.content.pm.ResolveInfo;

/**
 * Created by VuDuc on 9/4/2017.
 */

public class MyResolveInfo {

    private ResolveInfo mResolveInfo;

    private boolean check;

    public MyResolveInfo(ResolveInfo resolveInfo) {
        mResolveInfo = resolveInfo;
        check = false;
    }

    public ResolveInfo getResolveInfo() {
        return mResolveInfo;
    }

    public void setResolveInfo(ResolveInfo resolveInfo) {
        mResolveInfo = resolveInfo;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
