package cn.scooper.com.whiteboard.event;

import cn.scooper.com.easylib.rxbus.Event;

/**
 * Created by Aguai on 2016/11/23.
 * 通知事件
 */
public class NotifyEvent extends Event {
    public static final int NOTIFY_DELETE_ALL_OBJ = 1;
    public static final int NOTIFY_CONNECT_ERROR = 2;
    public static final int NOTIFY_CONNECT_CLOSE = 8;

    public static final int NOTIFY_FORCE_EXIT = 3;
    public static final int NOTIFY_PAGE_ADD = 4;
    public static final int NOTIFY_PAGE_NEXT = 5;
    public static final int NOTIFY_CREATE_SUCCEED = 6;
    public static final int NOTIFY_USER_JOIN = 7;
    public int eventType;
    public String pageId;
    public String meetingId;
    public String userId;
}
