package centercontrolleros.com.icentercontrolleros.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.model.DeviceMainHelper;
import centercontrolleros.com.icentercontrolleros.presenter.MainPresenter;
import centercontrolleros.com.icentercontrolleros.ui.model.MyTimeOut;
import centercontrolleros.com.icentercontrolleros.ui.untils.SharedPref;
import centercontrolleros.com.icentercontrolleros.ui.view.ViewMainListender;

/**
 * Created by VuDuc on 9/8/2017.
 */

public class MyTimeOutAdapter extends RecyclerView.Adapter<MyTimeOutAdapter.ViewHolder> implements ViewMainListender {

    public static final String TIME_OUT_CHECKED = "TIME_OUT_CHECKED";

    private MainPresenter mMainPresenter;
    private DeviceMainHelper mDeviceMainHelper;

    private Context mContext;
    private List<MyTimeOut> mMyTimeOuts;

    private int lastChecked = -1;

    public MyTimeOutAdapter(Context context, List<MyTimeOut> myTimeOuts) {
        mContext = context;
        mMyTimeOuts = myTimeOuts;

        mDeviceMainHelper = new DeviceMainHelper(mContext);
        mMainPresenter = new MainPresenter(mDeviceMainHelper, this);

        lastChecked = SharedPref.getInstance(mContext).getInt(TIME_OUT_CHECKED, -1);
        if (lastChecked != -1) {
            mMyTimeOuts.get(lastChecked).setCkb(true);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_screentimeout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MyTimeOut myTimeOut = mMyTimeOuts.get(position);

        holder.txt_timeout.setText(myTimeOut.getTxtTimeOut());
        holder.ckb_timeout.setChecked(myTimeOut.isCkb());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long mDelay = 0;
                switch (position) {
                    case 0:
                        mDelay = 15000;
                        break;
                    case 1:
                        mDelay = 30000;
                        break;
                    case 2:
                        mDelay = 60000;
                        break;
                    case 3:
                        mDelay = 120000;
                        break;
                    case 4:
                        mDelay = 600000;
                        break;
                    case 5:
                        mDelay = 1800000;
                        break;
                }

                if (lastChecked != -1) {
                    if (position != lastChecked) {
                        mMyTimeOuts.get(lastChecked).setCkb(false);
                        lastChecked = position;
                        myTimeOut.setCkb(true);
                        notifyDataSetChanged();
                    }
                } else {
                    lastChecked = position;
                    myTimeOut.setCkb(true);
                    notifyDataSetChanged();
                }
                SharedPref.getInstance(mContext).putInt(TIME_OUT_CHECKED, lastChecked);
                mMainPresenter.displayTimeout(mContext, mDelay);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ((null != mMyTimeOuts) ? mMyTimeOuts.size() : 0);
    }

    @Override
    public void visibleWifi() {

    }

    @Override
    public void invisibleWifi() {

    }

    @Override
    public void visibleAirPlane() {

    }

    @Override
    public void invisibleAirPlane() {

    }

    @Override
    public void visibleBluetooth() {

    }

    @Override
    public void invisibleBluetooth() {

    }

    @Override
    public void visibleMobileData() {

    }

    @Override
    public void invisibleMobileData() {

    }

    @Override
    public void invisibleRotate() {

    }

    @Override
    public void visibleRotate() {

    }

    @Override
    public void visiableCamera() {

    }

    @Override
    public void visiableFlashLight() {

    }

    @Override
    public void invisiableFlashLight() {

    }

    @Override
    public void visiableSilent() {

    }

    @Override
    public void invisiableSilent() {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView txt_timeout;
        CheckBox ckb_timeout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            txt_timeout = itemView.findViewById(R.id.txt_timeout);
            ckb_timeout = itemView.findViewById(R.id.ckb_timeout);

            ckb_timeout.setClickable(false);
        }
    }
}
