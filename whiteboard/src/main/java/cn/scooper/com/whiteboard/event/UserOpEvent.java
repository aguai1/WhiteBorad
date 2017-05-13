package cn.scooper.com.whiteboard.event;


import cn.scooper.com.easylib.rxbus.Event;
import cn.scooper.com.whiteboard.vo.UserVO;

/**
 * Created by zhenglikun on 2016/11/23.
 * <p>
 * 用户登陆成功消息事件
 */

public class UserOpEvent extends Event {
    public UserVO vo;
}
