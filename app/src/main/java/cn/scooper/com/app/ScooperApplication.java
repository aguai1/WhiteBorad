package cn.scooper.com.app;

import cn.scooper.com.whiteboard.WhiteBoardApplication;

public class ScooperApplication extends WhiteBoardApplication {
    private static WhiteBoardApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    public static WhiteBoardApplication getInstance() {
        return instance;
    }
}
