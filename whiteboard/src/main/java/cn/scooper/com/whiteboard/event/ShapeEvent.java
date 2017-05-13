package cn.scooper.com.whiteboard.event;

import cn.scooper.com.easylib.rxbus.Event;
import cn.scooper.com.whiteboard.db.domain.ShapeBean;

/**
 * Created by Aguai on 2016/11/23
 * shape事件
 */
public class ShapeEvent extends Event {
    public static int OP_ADD = 1;
    public static int OP_DELETE = 2;
    public static int OP_MOVE = 3;
    public static int OP_ADJUST_BUNDS = 4;
    public static int OP_ADJUST_COLOR = 5;
    public static int OP_ADJUST_WIDTH = 6;
    //这里写删除，移动，增加，等命令，
    public int opType;
    public ShapeBean shapeBean = new ShapeBean();
}
