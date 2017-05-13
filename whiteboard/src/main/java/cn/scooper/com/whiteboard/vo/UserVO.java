package cn.scooper.com.whiteboard.vo;

import java.io.Serializable;

import cn.scooper.com.whiteboard.relogic.minaclient.MinaClient;
import cn.scooper.com.whiteboard.utils.StringUtils;

/**
 * Created by zhenglikun on 2016/11/22.
 */

public class UserVO implements Serializable, IJsonVO {
    public int uid;
    public String uname;
    public String token;


    public static UserVO build(String o) {
        UserVO vo = new UserVO();
        vo.parse(o);
        return vo;
    }

    @Override
    public void parse(String o) {
        if (o == null) return;
        uid = StringUtils.getIntContent(o, "uid");
        token = StringUtils.getStringContent(o, "token");
        uname = StringUtils.getStringContent(o, "uname");
        if (!token.equals(""))
            MinaClient.INSTANCE.setToken(token);
    }
}
