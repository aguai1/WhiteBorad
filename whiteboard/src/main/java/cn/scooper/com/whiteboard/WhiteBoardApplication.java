package cn.scooper.com.whiteboard;

import android.app.Application;

import cn.scooper.com.easylib.BaseApplication;
import cn.scooper.com.whiteboard.relogic.imagecache.WBImageLoader;

/**
 * Created by Aguai on 2016/11/20.
 * 电子白板Application
 */
public class WhiteBoardApplication extends BaseApplication {
    private static WhiteBoardApplication instance;
    private MeetingContext meetingContext;

    public static WhiteBoardApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        meetingContext = new MeetingContext(getApplicationContext());
        meetingContext.startEventListener();
        WBImageLoader.initLoader(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        meetingContext.stopEventListener();
    }

    public MeetingContext getMeetingContext() {
        return meetingContext;
    }
}
