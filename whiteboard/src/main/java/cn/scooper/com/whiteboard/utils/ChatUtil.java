package cn.scooper.com.whiteboard.utils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.whiteboard.WhiteBoardApplication;

/**
 * Created by aguaiSir on 2017/5/14.
 */

public class ChatUtil {
    private static final String TAG = "ChatUtil";

    public static void invite(final String meetingid, final String userid) {
        new Thread(){
            @Override
            public void run() {
                try {
                    LogUtil.e(TAG,"invite  userid:"+userid+"meetingid:"+meetingid);
                    EMGroupInfo groupInfo = WhiteBoardApplication.getInstance().getGroupByMeetingId(meetingid);
                    if (groupInfo==null){
                        LogUtil.e(TAG,"未找到群组");
                        return;
                    }
                    EMClient.getInstance().groupManager().inviteUser(groupInfo.getGroupId(), new String[]{userid}, null);//需异步处理
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    public static void createMeeting(final String meetingId) {
        new Thread(){
            @Override
            public void run() {
                EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                option.maxUsers = 200;
                option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                try {
                    EMClient.getInstance().groupManager().createGroup(meetingId, "test", new String[0], "", option);
                    WhiteBoardApplication.getInstance().getAllGroup();
                } catch (HyphenateException e) {
                    LogUtil.e(TAG,"创建失败"+e.getDescription());
                }
            }
        }.start();
    }

    public static EMMessage sendMsg(String content, String meetingid) {
        LogUtil.e(TAG,"sendMsg"+content+meetingid);

        EMGroupInfo groupByMeetingId = WhiteBoardApplication.getInstance().getGroupByMeetingId(meetingid);
        if (groupByMeetingId==null){
            WhiteBoardApplication.getInstance().getAllGroup();
            LogUtil.e(TAG,"未找到群组");
            return null;
        }
        LogUtil.e(TAG,"sendMsg"+groupByMeetingId.getGroupId()+content);
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, groupByMeetingId.getGroupId());
        //如果是群聊，设置chattype，默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        return message;
    }
}
