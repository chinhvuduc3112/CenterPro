package centercontrolleros.com.icentercontrolleros.ui.model;

/**
 * Created by VuDuc on 9/8/2017.
 */

public class MyTimeOut {
    private String txtTimeOut;
    private boolean ckb;

    public MyTimeOut(String txtTimeOut, boolean ckb) {
        this.txtTimeOut = txtTimeOut;
        this.ckb = ckb;
    }

    public String getTxtTimeOut() {
        return txtTimeOut;
    }

    public void setTxtTimeOut(String txtTimeOut) {
        this.txtTimeOut = txtTimeOut;
    }

    public boolean isCkb() {
        return ckb;
    }

    public void setCkb(boolean ckb) {
        this.ckb = ckb;
    }
}
