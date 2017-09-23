package centercontrolleros.com.icentercontrolleros.ui.customview;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Adapter;

import java.util.ArrayList;

import centercontrolleros.com.icentercontrolleros.model.AppInfor;
import centercontrolleros.com.icentercontrolleros.ui.adapter.CustomRecyleAdapter;

/**
 * Created by mr.logic on 9/1/2017.
 */

public class MyRecycleView extends RecyclerView {

    private final static String TAG = MyRecycleView.class.getSimpleName();

    private OnListenerMyRecycleView mOnListenerMyRecycleView;

    public void setOnListenerMyRecycleView(OnListenerMyRecycleView onListenerMyRecycleView){
        this.mOnListenerMyRecycleView = onListenerMyRecycleView;
    }

    public MyRecycleView(Context context) {
        super(context);
    }

    public MyRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


        setOnFlingListener(new OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                Log.d(TAG,"onFling..."+velocityY);

                if(velocityY !=0 && velocityY < 0 ) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
                    if (mOnListenerMyRecycleView != null&& isMinScrolled)
                        mOnListenerMyRecycleView.onMinScrolling();
                    else if(( linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0 )&& (linearLayoutManager.findFirstVisibleItemPosition() == 0)){
                        if (mOnListenerMyRecycleView != null)
                            mOnListenerMyRecycleView.onMinScrolling();
                    }
                }
                return false;
            }
        });

        enableScrollRecyleview();

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState != SCROLL_STATE_DRAGGING)
                    isScrolled = false;

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG,"onScrolled dy:"+dy);
                isScrolled = dy !=0;
            }
        });
    }

    boolean isScrolled = false;

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if(state == SCROLL_STATE_DRAGGING){
            if(mOnListenerMyRecycleView!=null)
                mOnListenerMyRecycleView.onScrolling();
        }else if(state == SCROLL_STATE_SETTLING){
            if(mOnListenerMyRecycleView!=null)
                mOnListenerMyRecycleView.onFling();

        }else{

            if(mOnListenerMyRecycleView!=null)
                mOnListenerMyRecycleView.onUp();
        }


        Log.d(TAG,"onScrollStateChanged..."+state);
    }


    public  void updateData(ArrayList<AppInfor> applicationInfos){
        Object adapter =  getAdapter();
        if(adapter!=null) {
            CustomRecyleAdapter customRecyleAdapter = (CustomRecyleAdapter) adapter;
            customRecyleAdapter.updateData(applicationInfos);
        }
    }

    public void updateUIMusic(){
        Object adapter =  getAdapter();
        if(adapter!=null) {
            CustomRecyleAdapter customRecyleAdapter = (CustomRecyleAdapter) adapter;
            customRecyleAdapter.updateUI();
        }
    }
    public void updateUIWifi(){
        Object adapter =  getAdapter();
        if(adapter!=null) {
            CustomRecyleAdapter customRecyleAdapter = (CustomRecyleAdapter) adapter;
            customRecyleAdapter.updateUI();
        }
    }


    boolean isMinScrolled = false;

    private boolean isMinScrollReached(){

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();


//        Log.d(TAG,"findFirstCompletelyVisibleItemPosition:"+linearLayoutManager.findFirstCompletelyVisibleItemPosition());
//        Log.d(TAG,"findLastVisibleItemPosition:"+linearLayoutManager.findLastVisibleItemPosition());
//        Log.d(TAG,"findFirstVisibleItemPosition:"+linearLayoutManager.findFirstVisibleItemPosition());
//        Log.d(TAG,"findLastCompletelyVisibleItemPosition:"+linearLayoutManager.findLastCompletelyVisibleItemPosition());
     //   boolean isMinscrolling =  computeVerticalScrollOffset() == 0 && getScrollState() == SCROLL_STATE_DRAGGING;

        boolean isMinscrolling = isScrolled&&( linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0 )&& (linearLayoutManager.findFirstVisibleItemPosition() == 0) && getScrollState() == SCROLL_STATE_DRAGGING;

        Log.d(TAG,"isMinscrolling:"+isMinscrolling);
        isMinScrolled = isMinscrolling;

        return isMinscrolling;
    }

     private boolean isMaxScrollReached() {
        int maxScroll = computeVerticalScrollRange();
         Log.d(TAG,"isMaxScrollReached:"+maxScroll);
         Log.d(TAG,"isMaxScrollReached:"+computeVerticalScrollOffset());
         Log.d(TAG,"isMaxScrollReached:"+computeVerticalScrollExtent());
        int currentScroll = computeVerticalScrollOffset() + computeVerticalScrollExtent();

         Log.d(TAG,"isMaxScrollReached:"+currentScroll);

        return currentScroll >= maxScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        Log.d(TAG,"onTouchEvent y:"+e.getY() + isMinScrollReached());


        if(isEnableScrollRecyleview)
           return super.onTouchEvent(e);
        else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        Log.d(TAG,"onInterceptTouchEvent y:"+e.getY() + isMinScrollReached());
        if(!isEnableScrollRecyleview)
            return false;
        return super.onInterceptTouchEvent(e);
    }

    boolean isEnableScrollRecyleview = true;

    public boolean isEnableScrollRecyleview(){
        return isEnableScrollRecyleview;
    }

    public void disableScrollRecylceview(){
//        setLayoutFrozen(true);
        stopScroll();
        isEnableScrollRecyleview = false;
    }
    public void enableScrollRecyleview(){
//        setLayoutFrozen(false);
        isEnableScrollRecyleview = true;

    }


    public static interface OnListenerMyRecycleView{
        public void onScrolling();
        public void onFling();
        public void onUp();
        public void onMinScrolling();
    }
}
