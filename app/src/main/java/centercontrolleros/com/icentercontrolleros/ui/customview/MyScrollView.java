package centercontrolleros.com.icentercontrolleros.ui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;

/**
 * Created by VuDuc on 8/30/2017.
 */

public class MyScrollView extends NestedScrollView {
    public static final String TAG = MyScrollView.class.getSimpleName();
    private boolean enableScrolling = true;
    private float starty;
    private OnListenerMyScrollView mOnListenerMyScrollView;

    public void setOnListenerMyScrollView(OnListenerMyScrollView onListenerMyScrollView){
        this.mOnListenerMyScrollView = onListenerMyScrollView;
    }

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if(scrollingEnabled())
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    private boolean scrollingEnabled() {
        return enableScrolling;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mOnListenerMyScrollView!=null)
                    mOnListenerMyScrollView.onDown(ev);
                starty = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                float endY = ev.getY();
                if(mOnListenerMyScrollView!=null)
                    mOnListenerMyScrollView.onMove(ev);
                if (!canScrollVertically(-1)) {
                    if (endY <= starty) {
                        //Move up
                        setScrolling(true);
                    } else {
                        //Move down
                        setScrolling(false);
                    }
                } else {
                    setScrolling(true);
                }

                if (scrollingEnabled()) {
                    return super.onTouchEvent(ev);
                } else {
                    setScrolling(true);
                    return false;
                }

            case MotionEvent.ACTION_UP:
                if(mOnListenerMyScrollView!=null)
                    mOnListenerMyScrollView.onUp(ev);
                break;


        }


        Log.d(TAG,"onTouchEvent:"+ev.getY());
        return super.onTouchEvent(ev);
    }

    public void setScrolling(boolean enableScrolling) {
       // this.enableScrolling = enableScrolling;
    }

    public void disableSrolling(){
        enableScrolling = false;
    }

    public void enableScrolling(){
        enableScrolling = true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    public static interface OnListenerMyScrollView{
        public void onUp(MotionEvent ev);
        public void onDown(MotionEvent ev);
        public void onMove(MotionEvent ev);
    }
}
