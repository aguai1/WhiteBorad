package cn.scooper.com.whiteboard.event;

import cn.scooper.com.easylib.rxbus.Event;

/**
 * Created by Aguai on 2016/11/23
 * 邀请事件
 */

public class InviteEvent extends Event {

    public String fromUserId;
    public String meetingId;
    public String meetingName;
}
