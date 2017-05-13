package cn.scooper.com.easylib.utils;

import android.util.Log;

import cn.scooper.com.easylib.setting.Setting;

/**
 * Created by 阿怪 on 2015/8/10.
 * LogUtil管理
 */
public class LogUtil {


    public static void v(String tag, String msg) {
        if (Setting.isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (Setting.isDebug) {
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (Setting.isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (Setting.isDebug) {
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (Setting.isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (Setting.isDebug) {
            Log.i(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (Setting.isDebug) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (Setting.isDebug) {
            Log.w(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (Setting.isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (Setting.isDebug) {
            Log.e(tag, msg, tr);
        }
    }

    public static void wtf(String tag, String msg) {
        if (Setting.isDebug) {
            Log.wtf(tag, msg);
        }
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        if (Setting.isDebug) {
            Log.wtf(tag, msg, tr);
        }
    }
}  