package cn.scooper.com.whiteboard.utils;


import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class AnimUtil {
    /**
     * 关闭对话框
     */
    public static void startCloseAnim(final View rv_dlg, final ViewGroup rv_parent) {

        Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                1.0f);
        anim.setDuration(500);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rv_parent.removeView(rv_dlg);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setDuration(300);
        rv_dlg.startAnimation(anim);
    }


    /**
     * 打开对话框
     *
     * @param arcview
     */
    public static void startOpenAnim(View arcview, final View rv_dlg) {
        int[] loc = new int[2];
        arcview.getLocationOnScreen(loc);
        Animation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                1.0f);
        anim.setDuration(300);
        rv_dlg.startAnimation(anim);
    }


}
