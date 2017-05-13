package cn.scooper.com.whiteboard.relogic.minaclient;

import cn.scooper.com.whiteboard.utils.StringUtils;


/**
 * Created by zhenglikun on 2016/11/22.
 */

public class DataInfoBean {
    public String msg;
    public String result;
    public String op;
    public String data;

    public String shapeType;

    public DataInfoBean(String o) {
        msg = StringUtils.getStringContent(o, "msg");
        result = StringUtils.getStringContent(o, "result");
        data = o;
        op = StringUtils.getStringContent(o, "op");
        shapeType = StringUtils.getStringContent(o, "type");
    }

}
