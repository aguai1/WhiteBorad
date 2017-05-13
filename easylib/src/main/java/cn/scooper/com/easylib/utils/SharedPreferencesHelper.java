package cn.scooper.com.easylib.utils;

import android.content.Context;

/**
 * SharedPreferences数据处理类
 */
public class SharedPreferencesHelper {

    public static final String IS_FIRST_IN = "is_first_in";
    private static final String SP_LOCAL_USERINFO="sp_local_userinfo";

    private static SharedPreferencesHelper staticInstance;
    private static Object localUserInfo;

    public static SharedPreferencesHelper getInstance() {
        if (staticInstance == null) {
            staticInstance = new SharedPreferencesHelper();
        }
        return staticInstance;
    }

    public  String getLocalUserInfo() {
        return (String) SharedPreferencesUtils.get(SP_LOCAL_USERINFO,"");
    }
    public  void setLocalUserInfo(String string) {
        SharedPreferencesUtils.put(SP_LOCAL_USERINFO,string);
    }

    public boolean getIsFirstIn() {
        return (boolean) SharedPreferencesUtils.get(IS_FIRST_IN, true);
    }

    public void setIsFirstIn(boolean isfirst) {
        SharedPreferencesUtils.put(IS_FIRST_IN, isfirst);
    }
}
