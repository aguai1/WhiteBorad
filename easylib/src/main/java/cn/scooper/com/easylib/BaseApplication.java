package cn.scooper.com.easylib;

import android.app.Activity;
import android.app.Application;
import com.squareup.leakcanary.LeakCanary;

import cn.scooper.com.easylib.broadcast.NetworkManager;
import cn.scooper.com.easylib.utils.AppActivities;
import cn.scooper.com.easylib.utils.CrashHandler;
import cn.scooper.com.easylib.utils.ToastUtils;

/**
 * Created by aguaiSir on 2017/5/7.
 */

public class BaseApplication extends Application {
    private static BaseApplication mInstance;
    private NetworkManager networkManager = new NetworkManager();
    public static BaseApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance == null) {
            mInstance = this;
        }
        if (BuildConfig.DEBUG) {
            //内存泄露
            LeakCanary.install(this);
        }
        networkManager.startup(this);
        ToastUtils.register(this);
        CrashHandler.getInstance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (networkManager != null) {
            networkManager.shutdown(this);
        }
    }

    public Activity getCurrentActivity() {
        return AppActivities.instance().current();
    }

}
