package centercontrolleros.com.icentercontrolleros.ui.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;

/**
 * Created by Duy on 8/28/2017.
 */

@SuppressLint("AppCompatCustomView")
public class VerticalSeebar extends SeekBar {

    private static final String TAG = VerticalSeebar.class.getSimpleName();

    private OnListenerScrollSeekBar mOnListenerScrollSeekBar;

    public VerticalSeebar(Context context) {
        super(context);
    }

    public VerticalSeebar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeebar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnListenerScrollSeekBar(OnListenerScrollSeekBar onListenerScrollSeekBar) {
        this.mOnListenerScrollSeekBar = onListenerScrollSeekBar;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        super.onDraw(canvas);
    }


    public boolean onTouchEvent(MotionEvent event) {
//        if(!isEnabled()){
//            return false;
//        }

        boolean result = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Logger.d(TAG, "ACTION_DOWN =========");
//                disableParams("ACTION_SCROLL_ON");
                if (mOnListenerScrollSeekBar != null) {
                    mOnListenerScrollSeekBar.onDown();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOnListenerScrollSeekBar != null) {
                    mOnListenerScrollSeekBar.onMove();
                }
                Logger.d(TAG, "ACTION_MOVE =========");
//                disableParams("ACTION_SCROLL_ON");
                mySetProgress(event);
                break;
            case MotionEvent.ACTION_UP:
                if (mOnListenerScrollSeekBar != null) {
                    mOnListenerScrollSeekBar.onUp();
                }
                Logger.d(TAG, "ACTION_UP =========");
//                disableParams("ACTION_SCROLL_OFF");
                mySetProgress(event);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return result;
    }


    private void mySetProgress(MotionEvent event) {
        int i;
        i = getMax() - (int) (getMax() * event.getRawY() / getHeight());
        if (i < 0) {
            i = 0;
        }
        if (i > getMax()) {
            i=getMax();
        }
        setProgress(i);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }


    public static interface OnListenerScrollSeekBar {
        public void onMove();

        public void onDown();

        public void onUp();
    }

//    private void disableParams(String action) {
//        Intent intent = new Intent();
//        intent.setAction(action);
//        getContext().sendBroadcast(intent);
//    }

}
