package cn.scooper.com.whiteboard.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by aguaiSir on 2017/5/14.
 */

public class ChatRecyclerView extends RecyclerView {
    private boolean isOpMode=false;
    public ChatRecyclerView(Context context) {
        super(context);
    }

    public ChatRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (isOpMode){
            return false;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isOpMode){
            return false;
        }
        return super.onTouchEvent(e);
    }

    public void setOpMode(boolean opMode) {
        isOpMode = opMode;
    }

    public boolean isOpMode() {
        return isOpMode;
    }
}
