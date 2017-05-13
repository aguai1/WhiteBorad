package cn.scooper.com.easylib.utils;

import com.google.gson.Gson;

/**
 * Created by 阿怪 on 2015/8/10.
 * 目前该类功能只是持有Gson的单例,
 */

public class JsonUtil {

    private static Gson mGson;

    public static Gson getParser() {
        if (mGson == null) {
            synchronized (JsonUtil.class) {
                if (mGson == null) {
                    mGson = new Gson();
                }
            }
        }
        return mGson;
    }
}
