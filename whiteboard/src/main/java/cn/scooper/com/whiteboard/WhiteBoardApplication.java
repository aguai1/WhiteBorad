package cn.scooper.com.whiteboard;

import android.app.Application;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import cn.scooper.com.easylib.BaseApplication;
import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.whiteboard.relogic.imagecache.WBImageLoader;
import cn.scooper.com.whiteboard.utils.ChatUtil;

/**
 * Created by Aguai on 2016/11/20.
 * 电子白板Application
 */
public class WhiteBoardApplication extends BaseApplication {
    public static final String TAG="WhiteBoardApplication";
    private static WhiteBoardApplication instance;
    private MeetingContext meetingContext;
    public static WhiteBoardApplication getInstance() {
        return instance;
    }
    private List<EMGroupInfo> allPublicGroup;

    private List<String> createMeetingList;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //环信初始化
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(true);
        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        meetingContext = new MeetingContext(getApplicationContext());
        meetingContext.startEventListener();
        createMeetingList=new ArrayList<>();
        WBImageLoader.initLoader(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        meetingContext.stopEventListener();
//        for(final EMGroupInfo emGroupInfo:allPublicGroup){
//            for (String s:createMeetingList){
//                if (s.equals(emGroupInfo.getGroupName())){
//                    new Thread(){
//                        @Override
//                        public void run() {
//                            try {
//                                EMClient.getInstance().groupManager().destroyGroup(emGroupInfo.getGroupId());//需异步处理
//                                LogUtil.e(TAG, "destroyGroup" + emGroupInfo.getGroupName());
//                            } catch (HyphenateException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();
//                }
//            }
//        }
    }

    public MeetingContext getMeetingContext() {
        return meetingContext;
    }

    public EMGroupInfo getGroupByMeetingId(String meetingid){
        if (allPublicGroup==null){
            return null;
        }
        for (EMGroupInfo emGroupInfo : allPublicGroup) {
            LogUtil.e(TAG, "emGroupInfo" + emGroupInfo.getGroupName());
            if (emGroupInfo.getGroupName().equals(meetingid)) {
                LogUtil.e(TAG, "meetingid=getGroupName");
                return emGroupInfo;
            }
        }
        return null;
    }

    public List<String> getCreateMeetingList() {
        return createMeetingList;
    }


    public void getAllGroup() {

        new Thread(){
            @Override
            public void run() {
                EMCursorResult<EMGroupInfo> groups = null;
                try {
                    groups = EMClient.getInstance().groupManager().getPublicGroupsFromServer(100, null);
                } catch (HyphenateException e) {
                    LogUtil.e(TAG, "获取公开群组失败"+e.toString());

                }
                assert groups != null;
                allPublicGroup=groups.getData();
            }
        }.start();
    }
}
