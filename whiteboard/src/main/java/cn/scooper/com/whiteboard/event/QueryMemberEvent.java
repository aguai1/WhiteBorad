package cn.scooper.com.whiteboard.event;

import java.util.ArrayList;
import java.util.List;

import cn.scooper.com.easylib.rxbus.Event;
import cn.scooper.com.whiteboard.db.domain.UserBean;

/**
 * Created by zhenglikun on 2016/12/8.
 */

public class QueryMemberEvent extends Event {
    public List<UserBean> users = new ArrayList();
}
