package cn.scooper.com.easylib.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Aguai on 2016/11/20.
 * Toast操作统一调用器
 */
public class ToastUtils {

    public static Context sContext;


    public static void register(Context context) {
        sContext = context.getApplicationContext();
    }


    private static boolean check() {
        return sContext != null;
    }


    public static void showShort(@android.support.annotation.StringRes int resId) {
        if (check()) {
            Toast.makeText(sContext, resId, Toast.LENGTH_SHORT).show();
        }

    }


    public static void showShort(String message) {
        if (check()) {
            Toast.makeText(sContext, message, Toast.LENGTH_SHORT).show();
        }

    }


    public static void showLong(@android.support.annotation.StringRes int resId) {
        if (check()) {
            Toast.makeText(sContext, resId, Toast.LENGTH_LONG).show();
        }

    }


    public static void showLong(String message) {
        if (check()) {
            Toast.makeText(sContext, message, Toast.LENGTH_LONG).show();
        }
    }
}
