package cn.scooper.com.easylib.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 禁用手势滑动
 * Created by jiangwj on 2014/12/3.
 */
public class NoDragViewPager extends ViewPager {

    public NoDragViewPager(Context context) {
        super(context);
    }

    public NoDragViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
