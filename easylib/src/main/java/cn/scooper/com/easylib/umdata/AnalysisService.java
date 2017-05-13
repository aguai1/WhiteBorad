package cn.scooper.com.easylib.umdata;


import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by Aguai on 2016/11/18.
 * 友盟事件发送
 */
public class AnalysisService {
    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    ////统计页面，"pageName"为页面名称，可自定义
    public static void onPageStart(String pageName) {
        MobclickAgent.onPageStart(pageName);
    }

    public static void onPageEnd(String pageName) {
        MobclickAgent.onPageEnd(pageName);
    }


    /**
     * 发送网络连接失败事件
     */
    public static void sendConnectionFailedEvent(Context context) {
        MobclickAgent.onEvent(context, AnalysisConstants.CONNECTION_FAILED);
    }

    /**
     * 绑定登录用户
     *
     * @param uid 用户id
     */
    public static void login(int uid) {
        MobclickAgent.onProfileSignIn("" + uid);
    }

    /**
     * 解绑登录用户
     */
    public static void logout() {
        MobclickAgent.onProfileSignOff();
    }
}
