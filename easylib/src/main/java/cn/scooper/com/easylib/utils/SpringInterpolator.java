package cn.scooper.com.easylib.utils;

import android.view.animation.Interpolator;


public class SpringInterpolator implements Interpolator {

    private float mFactor = 0.4f;

    public SpringInterpolator(float factor) {
        mFactor = factor;
    }

    public SpringInterpolator() {
    }

    @Override
    public float getInterpolation(float input) {
        return (float) (Math.pow(2, -10 * input) * Math.sin((input - mFactor / 4) * (2 * Math.PI) / mFactor) + 1);
    }
}
