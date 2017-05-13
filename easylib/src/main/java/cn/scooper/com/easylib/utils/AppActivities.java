package cn.scooper.com.easylib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.NoSuchElementException;
import java.util.Stack;


/**
 * 当前进程 Activity 管理，用于安全退出程序。
 * （关闭所有打开的 Activity 再退出）
 * （需要事先在Activity的onCreate中调用 AppActivities.add 方法，以及结束时使用 AppActivities.finish 来结束）
 * <p>
 * Created by jiangwj on 2014/7/29.
 */
public final class AppActivities {

    private static AppActivities _instance = new AppActivities();
    // 当前程序中运行的所有 activities
    private Stack<Activity> allActivities = new Stack<Activity>();

    public static AppActivities instance() {
        return _instance;
    }

    /**
     * 将 Activity 添加到列表
     *
     * @param act
     */
    public void add(Activity act) {
        allActivities.add(act);
    }

    /**
     * 将指定 Activity 移除
     */
    public void remove(Activity act) {
        if (act != null) {
            allActivities.remove(act);
        }
    }

    /**
     * 结束一个 Activity，并将其移除
     */
    public void finish(Activity act) {
        if (act != null) {
            act.finish();
            allActivities.remove(act);
            Log.e("finish", "finish 调用");
        }
    }

    /**
     * 使用 requestCode 结束一个 Activity
     *
     * @param act
     * @param requestCode
     */
    public void finish(Activity act, int requestCode) {
        if (act != null) {
            act.finishActivity(requestCode);
            allActivities.remove(act);
        }
    }

    /**
     * 获取当前 activity
     *
     * @return
     */
    public Activity current() {
        if (allActivities.size() == 0) return null;
        try {
            return allActivities.lastElement();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    /**
     * 从当前Activity一直结束到指定Activity
     *
     * @param clz class of Activity
     */
    public void finishTo(Context context, Class<? extends Activity> clz) {
        context = context.getApplicationContext();
        Activity cur = current();
        while (cur != null) {
            if (cur.getClass().equals(clz)) {
                return;
            }
            finish(cur);
            cur = current();
        }
        // not found in list
        context.startActivity(new Intent(context, clz));
    }

    /**
     * 完全退出程序
     */
    public void finishAll() {
        // 先关闭所有打开的 Activity
        for (Activity act : allActivities) {
            act.finish();
        }
        allActivities.clear();
    }

}
