package cn.scooper.com.whiteboard.relogic.minaclient;

import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import cn.scooper.com.easylib.rxbus.RxBus;
import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.easylib.utils.ToastUtils;
import cn.scooper.com.whiteboard.db.domain.ShapeBean;
import cn.scooper.com.whiteboard.db.domain.UserBean;
import cn.scooper.com.whiteboard.event.InviteEvent;
import cn.scooper.com.whiteboard.event.MyEventBus;
import cn.scooper.com.whiteboard.event.NotifyEvent;
import cn.scooper.com.whiteboard.event.QueryMemberEvent;
import cn.scooper.com.whiteboard.event.ShapeEvent;
import cn.scooper.com.whiteboard.event.UserOpEvent;
import cn.scooper.com.whiteboard.relogic.Const;
import cn.scooper.com.whiteboard.utils.Base64Utils;
import cn.scooper.com.whiteboard.utils.GetShapeEventUtils;
import cn.scooper.com.whiteboard.utils.StringUtils;
import cn.scooper.com.whiteboard.utils.TypeToBytesUtils;
import cn.scooper.com.whiteboard.vo.UserVO;

/**
 * Created by zhenglikun on 2016/11/23.
 */

public enum NotifyApp {
    INSTANCE;
    private String meetingId;
    private String wbData;
    private int fromUid;

    public void dealWith(String op, String type, String data) {

        switch (op) {
            case Const.EVENT_ACTIVE:
                break;
            case Const.EVENT_CREATE:
                Request.meetingId = StringUtils.getStringContent(data, "meeting_id");
                Request.meetingName = StringUtils.getStringContent(data, "meeting_name");

                NotifyEvent notifyEvent = new NotifyEvent();
                notifyEvent.eventType = NotifyEvent.NOTIFY_CREATE_SUCCEED;
                notifyEvent.meetingId = Request.meetingId;
                notifyEvent.meetingName =  Request.meetingName;;
                RxBus.get().send(notifyEvent);
                break;
            case Const.EVENT_EXIT:
                break;
            case Const.EVENT_INVITE:
                break;
            case Const.EVENT_JOIN:
                break;
            case Const.EVENT_REJECT:
                break;
            case Const.EVENT_KICKOUT:
                break;
            case Const.EVENT_LOGIN:
                UserVO object = UserVO.build(data);
                UserOpEvent event = new UserOpEvent();
                event.vo = object;
                RxBus.get().send(event);
                Request.userId = object.uid;
                break;
            case Const.EVENT_LOGINOUT:
                break;
            case Const.EVENT_MEMBERS:
                dealWithMember(data);
                break;
            case Const.EVENT_NOTIFY:
                opNotify(type, data);
                break;
            case Const.EVENT_WHITEBOARD:
                break;
        }

    }

    private void opNotify(String type, String data) {
        switch (type) {
            case Const.NOTIFY_JOIN:
                int creator_id = StringUtils.getIntContent(data, "creator_id");
                if (creator_id != Request.userId) return;
                NotifyEvent notifyEvent = new NotifyEvent();
                notifyEvent.eventType = NotifyEvent.NOTIFY_USER_JOIN;
                notifyEvent.meetingId = Request.meetingId;
                notifyEvent.userId = StringUtils.getIntContent(data, "uid") + "";
                RxBus.get().send(notifyEvent);
                break;
            case Const.NOTIFY_EXIT:
                break;
            case Const.NOTIFY_FORCE_EXIT:
                dealWithForceExit();
                break;
            //用户邀请
            case Const.NOTIFY_REJECT:

                break;
            case Const.NOTIFY_INVITE:
//                Request.IINSTANCE.join(StringUtils.getStringContent(data, "meeting_id"));
                Request.meetingId = StringUtils.getStringContent(data, "meeting_id");
                InviteEvent inviteEvent = new InviteEvent();
                inviteEvent.fromUserId = StringUtils.getStringContent(data, "uid");
                inviteEvent.meetingId = StringUtils.getStringContent(data, "meeting_id");
                inviteEvent.meetingName = StringUtils.getStringContent(data, "meeting_name");
                RxBus.get().send(inviteEvent);
                LogUtil.e("NotifyApp", "NOTIFY_INVITE:" + meetingId);
                break;
            case Const.NOTIFY_WHITEBOARD:
                meetingId = StringUtils.getStringContent(data, "meeting_id");
                wbData = StringUtils.getStringContent(data, "wb_data");
                fromUid = StringUtils.getIntContent(data, "from_uid");
                dealWithWhiteBoard(wbData, fromUid);
                break;
        }

    }

    private void dealWithMember(String data) {
        List<String> mListName = StringUtils.getStringArrContent(data, "uname");
        List<Integer> mListUid = StringUtils.getIntArrContent(data, "uid");
        QueryMemberEvent event = new QueryMemberEvent();
        for (int i = 0; i < mListName.size(); i++) {
            UserBean userBean = new UserBean();
            userBean.setId(mListUid.get(i));
            userBean.setName(mListName.get(i));
            event.users.add(userBean);
        }
        RxBus.get().send(event);
    }

    private void dealWithForceExit() {
        NotifyEvent event = new NotifyEvent();
        event.eventType = NotifyEvent.NOTIFY_FORCE_EXIT;
        RxBus.get().send(event);
    }

    private void dealWithWhiteBoard(String msg, int fromUid) {
        byte[] bytes = Base64.decode(msg, Base64.DEFAULT);
        BigInteger commandId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(bytes, 0)));
        BigInteger objId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(bytes, 4)));
        BigInteger pageId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(bytes, 8)));
        byte[] data = Arrays.copyOfRange(bytes, 12, bytes.length);
        byte[] objBytes = splitByteArray(bytes, 4);

        switch (commandId.intValue()) {
            case Const.DATA_TRANS_ADDOBJECT://服务器有新数据向服务器发送请求对象
                dealDataAdd(objId, pageId, objBytes, fromUid);
                break;
            case Const.DATA_TRANS_OBJREQUEST://发送对象给服务器
                // Request.IINSTANCE.sendShapeBodyMsg(fromUid,pageId.intValue()+"",objId.intValue());
                Request.IINSTANCE.sendShapeBodyMsg(fromUid, pageId.intValue() + "", objId.intValue());
                break;
            case Const.DATA_TRANS_OBJRESPONSE://增加对象事件
                dealDataResponse(objId, pageId, objBytes, fromUid, data);
                break;
            case Const.DATA_TRANS_DELETE_OBJECT:
                deleteObj(pageId, data);
                break;
            case Const.DATA_TRANS_DELETE_ALL:
                deleteObjAll(pageId);
                break;
            case Const.DATE_TRANS_MOVEOBJ:
                moveObj(objId, pageId, fromUid, data);
                break;
            case Const.DATA_TRANS_OBJ_RESIZE:
                changeObjSize(objId, pageId, fromUid, data);
                break;
            case Const.DATA_TRANS_COLORREF_CHANGED:
                changeObjColor(objId, pageId, fromUid, data);
                break;
            case Const.DATA_TRANS_PAGE:
                dealWithPageEvent(data);
                break;
            case Const.DATA_TRANS_LINE_WIDTH_CHANGED:
                dealLineWidth(pageId, fromUid, data);
                break;

        }
    }

    private void dealLineWidth(BigInteger pageId, int fromId, byte[] data) {

        int width = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 0))).intValue();
        for (int i = 4; i < data.length; i += 4) {
            BigInteger objId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, i)));
            ShapeEvent event = new ShapeEvent();
            event.shapeBean = new ShapeBean();
            event.opType = ShapeEvent.OP_ADJUST_WIDTH;
            event.shapeBean.setPageId(pageId.intValue() + "");
            event.shapeBean.setServiceShapeId(objId.toString());
            event.shapeBean.setWidth(width);
            MyEventBus.get().sendEvent(event);
        }
    }

    private void dealWithPageEvent(byte[] data) {
        NotifyEvent event = new NotifyEvent();
        if (data[0] == 1) {
            event.eventType = NotifyEvent.NOTIFY_PAGE_ADD;
        } else if (data[0] == 2) {
            event.eventType = NotifyEvent.NOTIFY_PAGE_NEXT;
        }
        int pageId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 1))).intValue();
        event.pageId = pageId + "";
        RxBus.get().send(event);
    }

    /**
     * 修改颜色
     *
     * @param objId
     * @param pageId
     * @param fromId
     * @param content
     */
    private void changeObjColor(BigInteger objId, BigInteger pageId, int fromId, byte[] content) {
        List<ShapeEvent> events = GetShapeEventUtils.changeObjColor(pageId, content);
        for (ShapeEvent event : events)
            MyEventBus.get().sendEvent(event);

    }

    /**
     * 调整对象大小
     */
    private void changeObjSize(BigInteger objId, BigInteger pageId, int fromId, byte[] content) {
        ShapeEvent events = GetShapeEventUtils.changeObjSize(content);
        events.shapeBean.setServiceShapeId(objId.toString());
        events.shapeBean.setPageId(pageId.intValue() + "");
        MyEventBus.get().sendEvent(events);
    }


    /**
     * 移动对象
     */
    private void moveObj(BigInteger objId, BigInteger pageId, int fromId, byte[] content) {
        List<ShapeEvent> events = null;
        try {
            events = GetShapeEventUtils.generateMoveShapeEvent(pageId, content);
            for (ShapeEvent event : events) {
                MyEventBus.get().sendEvent(event);
                event.shapeBean.setPageId(pageId.intValue() + "");
                event.shapeBean.setServiceShapeId(objId.toString());
            }
        } catch (Exception e) {
            Log.e("NotifyApp", e.toString());
        }
    }

    /**
     * 删除对象
     *
     * @param pageId
     */
    private void deleteObj(BigInteger pageId, byte[] data) {
        for (int i = 0; i < data.length; i += 4) {
            BigInteger objId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, i)));
            ShapeEvent event = new ShapeEvent();
            event.shapeBean = new ShapeBean();
            event.opType = ShapeEvent.OP_DELETE;
            event.shapeBean.setPageId(pageId.intValue() + "");
            event.shapeBean.setServiceShapeId(objId.toString());
            MyEventBus.get().sendEvent(event);
        }
    }

    /**
     * 删除所有对象
     *
     * @param pageId
     */
    private void deleteObjAll(BigInteger pageId) {
        NotifyEvent notifyEvent = new NotifyEvent();
        notifyEvent.eventType = NotifyEvent.NOTIFY_DELETE_ALL_OBJ;
        notifyEvent.pageId = pageId + "";
        RxBus.get().send(notifyEvent);
    }

    private byte[] splitByteArray(byte[] arr, int start) {

        if (start + 4 > arr.length) {
            return null;
        }
        byte[] newBytes = new byte[4];

        for (int i = 0; i < 4; i++) {
            newBytes[i] = arr[i + start];
        }
        return newBytes;
    }

    /**
     * 向服务器发送请求对象响应
     */
    private void dealDataAdd(BigInteger objId, BigInteger pageId, byte[] objBytes, int fromId) {

        byte[] pageBytes = TypeToBytesUtils.intToByte4(pageId.intValue());
        byte[] commondBytes = TypeToBytesUtils.intToByte4(Const.DATA_TRANS_OBJREQUEST);

        String content = Base64Utils.encode(TypeToBytesUtils.byteMerger(commondBytes, objBytes, pageBytes));
        LogUtil.e("NotifyApp", content);
        Request.IINSTANCE.whiteboard(meetingId, fromId
                , content);
    }

    /**
     * 处理服务器发送对象响应
     *
     * @param objId
     * @param pageId
     * @param objBytes
     * @param fromId
     */
    private void dealDataResponse(BigInteger objId, BigInteger pageId, byte[] objBytes, int fromId, byte[] content) {

        BigInteger typeId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(content, 0)));
        int dataSize = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(content, 4))).intValue();
        byte[] data = Arrays.copyOfRange(content, 8, content.length);
        ShapeEvent event = null;
        try {
            event = GetShapeEventUtils.generateShapeEvent(typeId, dataSize, data);
            event.opType = ShapeEvent.OP_ADD;
            event.shapeBean.setMeetingId(Request.meetingId);
            event.shapeBean.setPageId(pageId + "");
            MyEventBus.get().sendEvent(event);
        } catch (Exception e) {
            LogUtil.e("NotifyApp", e.toString());
        }
    }
}
