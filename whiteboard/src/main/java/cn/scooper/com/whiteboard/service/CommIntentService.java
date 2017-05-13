package cn.scooper.com.whiteboard.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.scooper.com.easylib.rxbus.RxBus;
import cn.scooper.com.whiteboard.event.NotifyEvent;
import cn.scooper.com.whiteboard.relogic.Const;
import cn.scooper.com.whiteboard.relogic.minaclient.MinaClient;
import cn.scooper.com.whiteboard.utils.TypeToBytesUtils;

/**
 * Created by zhenglikun on 2016/11/21.
 */

public class CommIntentService extends IntentService {

    public static final String EXTRA_DATA = "extraData";
    public static final String EXTRA_PARAM_MAP = "extraParamMap";
    public static final String EXTRA_PARAM_STRING = "extraParamString";
    public static final String EXTRA_CONNECT = "extraConnect";
    public static final String EXTRA_COMMIT = "extraCommitByJson";
    public static final String EXTRA_COMMIT_BY_STRING = "extraCommitByString";
    public static final String EXTRA_END = "extraEnd";

    public static final String EXTRA_HOST = "extraHost";
    public static final String EXTRA_PORT = "extraPort";
    //包含不需要token的请求
    private Set<String> ignoreTokenPaths;

    public CommIntentService() {
        super("CommIntentService");
        ignoreTokenPaths = new HashSet<String>();
        ignoreTokenPaths.add(Const.EVENT_ACTIVE);
        ignoreTokenPaths.add(Const.EVENT_LOGIN);
        ignoreTokenPaths.add(Const.EVENT_NOTIFY);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(EXTRA_CONNECT)) {
            String host = intent.getStringExtra(EXTRA_HOST);
            int port = intent.getIntExtra(EXTRA_PORT, 6888);
            try {
                MinaClient.INSTANCE.connectToServer(host, port);
            } catch (Exception e) {
                NotifyEvent notifyEvent = new NotifyEvent();
                notifyEvent.eventType = NotifyEvent.NOTIFY_CONNECT_ERROR;
                RxBus.get().send(notifyEvent);
            }
        } else if (intent.getAction().equals(EXTRA_COMMIT)) {
            Bundle extras = intent.getExtras();
            if (extras == null) return;
            Bundle data = extras.getBundle(EXTRA_DATA);
            HashMap<String, String> params = (HashMap<String, String>) data.getSerializable(EXTRA_PARAM_MAP);
            Log.e("msg", "request :" + new JSONObject(params).toString());
            sendMsg(params);

        } else if (intent.getAction().equals(EXTRA_COMMIT_BY_STRING)) {
            String params = intent.getStringExtra(EXTRA_PARAM_STRING);
            Log.e("msg", "request :" + params);
            final byte[] result = getSendBytes(params);
            MinaClient.INSTANCE.sendMsg(result);
        } else if (intent.getAction().equals(EXTRA_END)) {
            MinaClient.INSTANCE.stopConnect();
        }
    }

    public JSONObject buildJson(HashMap<String, String> params) {
        String op = params.get("op");
        if (!isIgnoreTokenPath(op)) {
            params.put("token", MinaClient.INSTANCE.getToken());
        }
        return new JSONObject(params);
    }

    public byte[] getSendBytes(String obj) {
        byte[] source = new byte[]{};
        try {
            source = obj.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int len = source.length;
        byte[] lenBytes = TypeToBytesUtils.intToByte4(len);
        byte[] result = TypeToBytesUtils.byteMerger(lenBytes, source);

        return result;
    }

    /**
     * 是否忽略Token的请求
     *
     * @param path
     * @return
     */
    private boolean isIgnoreTokenPath(String path) {
        return ignoreTokenPaths.contains(path);
    }

    public void sendMsg(HashMap<String, String> params) {
        //获取obj对象
        JSONObject obj = buildJson(params);
        final byte[] result = getSendBytes(obj.toString());
        MinaClient.INSTANCE.sendMsg(result);
    }

}
