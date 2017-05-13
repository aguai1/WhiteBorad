package cn.scooper.com.whiteboard.relogic.minaclient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.whiteboard.WhiteBoardApplication;
import cn.scooper.com.whiteboard.db.domain.ShapeBean;
import cn.scooper.com.whiteboard.db.helper.ShapeHelper;
import cn.scooper.com.whiteboard.relogic.Const;
import cn.scooper.com.whiteboard.service.CommIntentService;
import cn.scooper.com.whiteboard.utils.AnalysicShapeEventutils;
import cn.scooper.com.whiteboard.utils.Base64Utils;
import cn.scooper.com.whiteboard.utils.StringUtils;
import cn.scooper.com.whiteboard.utils.TypeToBytesUtils;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.AbsShape;

import static cn.scooper.com.whiteboard.utils.Base64Utils.encode;

/**
 * Created by zhenglikun on 2016/11/23.
 */

public enum Request {
    IINSTANCE;
    public static String userName;
    public static int userId;
    public static String meetingId;
    private int lastUpdateId;


    public void login(String userName, String psd) {
        Request.userName = userName;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("op", "login");
        map.put("user", userName);
        map.put("pwd", psd);
        doAction(map);
    }

    /**
     * 用户登出
     */
    public void logout() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("op", "logout");
        doAction(map);
    }

    /**
     * 创建白板会议,meetingType
     */
    public void creatMeeting(String meetingName, int meetingType) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("op", "create");
        params.put("meeting_name", meetingName);
        params.put("meetingType", meetingType + "");
        doAction(params);

    }

    /**
     * 加入这个会议
     *
     * @param meetingId
     */
    public void join(String meetingId) {
        Request.meetingId = meetingId;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("op", "join");
        map.put("meeting_id", meetingId);
        doAction(map);
    }

    /**
     * 邀请用户加入会议
     */
    public void invite(String meetingId, String user) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("op", "invite");
        map.put("meeting_id", meetingId);
        map.put("users", user);
        doAction(map);
    }

    /**
     * 用户拒绝加入此会议
     */
    public void reject(String meetingId, String reason) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("op", "reject");
        map.put("meeting_id", meetingId);
        map.put("reason", reason);
        doAction(map);
    }

    /**
     * 用户退出该会议
     */
    public void exit(int delete) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("op", "exit");
        map.put("meeting_id", meetingId);
        map.put("delete", delete + "");
        doAction(map);
    }

    /**
     * 查询会议成员
     */
    public void queryMember(String meetingId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("op", "members");
        map.put("meeting_id", meetingId);
        doAction(map);
    }

    /**
     * 将制定用户请出会议
     */
    public void kickout(String meetingId, String uid) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("op", "kickout");
        map.put("uid", uid);
        map.put("meeting_id", meetingId);
        doAction(map);
    }

    /**
     * 白板操作,非json字符串请求
     */
    public void whiteboard(String meetingId, int toUid, String wbData) {


        String dataContent = StringUtils.getStringYH("meeting_id", meetingId) + "," + StringUtils.getStringYH("to_uid", toUid) + "," +
                StringUtils.getStringYH("wb_data", wbData);
        String data = StringUtils.getStringYHFIR("data", StringUtils.getStringKH(dataContent));
        String requset = data + "," + StringUtils.getStringYH("op", "whiteboard") + "," + StringUtils.getStringYH("token", MinaClient.INSTANCE.getToken());
        doAction(StringUtils.getStringKH(requset));
    }

    /**
     * 发出命令1
     */
    public void sendCommonFir(String meetingId, int toUid, int pageId, int objId) {
        byte[] pageBytes = TypeToBytesUtils.intToByte4(pageId);
        byte[] commondBytes = TypeToBytesUtils.intToByte4(Const.DATA_TRANS_ADDOBJECT);
        byte[] objBytes = TypeToBytesUtils.intToByte4(objId);
        String content = Base64Utils.encode(TypeToBytesUtils.byteMerger(commondBytes, objBytes, pageBytes));
        whiteboard(meetingId, toUid, content);
    }

    public void connectServer(String host, int port) {
        Intent intent = new Intent(WhiteBoardApplication.getInstance(), CommIntentService.class);
        intent.putExtra(CommIntentService.EXTRA_HOST, host);
        intent.putExtra(CommIntentService.EXTRA_PORT, port);
        intent.setAction(CommIntentService.EXTRA_CONNECT);
        WhiteBoardApplication.getInstance().startService(intent);
    }

    /**
     * 发送命令3
     */
    private void sendShapeBeanComThird(ShapeBean bean, String formIdStr) {
        try {
            int formId = Integer.parseInt(formIdStr);
            int pageId = Integer.parseInt(bean.getPageId());
            byte[] pageBytes = TypeToBytesUtils.intToByte4(pageId);
            byte[] commondBytes = TypeToBytesUtils.intToByte4(Const.DATA_TRANS_OBJRESPONSE);
            byte[] objBytes = TypeToBytesUtils.intToByte4(new BigInteger(bean.getServiceShapeId()).intValue());
            byte[] content = AnalysicShapeEventutils.analysicShapeBean(bean);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            os.write(commondBytes);
            os.write(objBytes);
            os.write(pageBytes);
            os.write(content);
            String result = Base64Utils.encode(os.toByteArray());
            whiteboard(Request.meetingId, formId, result);
        } catch (Exception e) {
            Log.e("Request", e.toString());
        }

    }


    public void stopConnectServer() {
        Intent intent = new Intent(WhiteBoardApplication.getInstance(), CommIntentService.class);
        intent.setAction(CommIntentService.EXTRA_END);
        WhiteBoardApplication.getInstance().startService(intent);
    }

    /**
     * 发送命令5,  清空当前页
     */
    public void sendComDeleteAll(int pageId, int userId) {
        try {
            byte[] pageBytes = TypeToBytesUtils.intToByte4(pageId);
            byte[] commondBytes = TypeToBytesUtils.intToByte4(Const.DATA_TRANS_DELETE_ALL);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(commondBytes);
            os.write(TypeToBytesUtils.intToByte4(0));
            os.write(pageBytes);
            String result = Base64Utils.encode(os.toByteArray());
            whiteboard(Request.meetingId, userId, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送命令14    创建新page
     */
    public void sendPageCom(int pageId, int toUid, int type) {
        if (type != Const.PAGE_CREATE && type != Const.PAGE_NEXT) {
            return;
        }
        try {
            byte[] pageBytes = TypeToBytesUtils.intToByte4(pageId);
            byte[] commondBytes = TypeToBytesUtils.intToByte4(Const.DATA_TRANS_PAGE);
            byte typeMod = TypeToBytesUtils.intToByte4(type)[0];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(commondBytes);
            os.write(TypeToBytesUtils.intToByte4(0));
            os.write(TypeToBytesUtils.intToByte4(0));
            os.write(typeMod);
            os.write(pageBytes);
            String result = Base64Utils.encode(os.toByteArray());
            whiteboard(Request.meetingId, toUid, result);
            os.write(commondBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void doAction(HashMap<String, String> params) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CommIntentService.EXTRA_PARAM_MAP, params);
        Intent intent = new Intent(WhiteBoardApplication.getInstance(), CommIntentService.class);
        intent.setAction(CommIntentService.EXTRA_COMMIT);
        intent.putExtra(CommIntentService.EXTRA_DATA, bundle);
        WhiteBoardApplication.getInstance().startService(intent);
    }


    public void doAction(String params) {
        Intent intent = new Intent(WhiteBoardApplication.getInstance(), CommIntentService.class);
        intent.setAction(CommIntentService.EXTRA_COMMIT_BY_STRING);
        intent.putExtra(CommIntentService.EXTRA_PARAM_STRING, params);
        WhiteBoardApplication.getInstance().startService(intent);
    }

    /**
     * 向服务器发送 新事件广播消息
     */
    public void sendNewShapeMsg(final AbsShape pathShape) {
        try {
            int pageId = Integer.parseInt(pathShape.getMeetingPage());
            int objId = Integer.parseInt(pathShape.getServiceId());
            sendCommonFir(meetingId, -1, pageId, objId);
        } catch (Exception e) {
            LogUtil.e("REQUEST", "pageid=null" + e.toString());
        }
    }

    /**
     * 发送消息体
     *
     * @param fromId
     * @param pageId
     * @param objId
     */
    public void sendShapeBodyMsg(int fromId, String pageId, int objId) {
        if (lastUpdateId == objId)
            return;
        lastUpdateId = objId;
        ShapeHelper shapeHelper = new ShapeHelper(WhiteBoardApplication.getInstance());
        ShapeBean shapeBean = shapeHelper.getShapesMeetingPageObjId(Request.meetingId, pageId, objId + "");
        if (shapeBean != null) {
            sendShapeBeanComThird(shapeBean, "-1");
        }
    }

    /**
     * 发送shape消息指定用户
     */
    public void sendShapeMsg(ShapeBean shapeBean, String userId) {
        sendShapeBeanComThird(shapeBean, userId);
    }


}
