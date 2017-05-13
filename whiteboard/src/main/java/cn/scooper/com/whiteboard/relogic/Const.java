package cn.scooper.com.whiteboard.relogic;


public class Const {


    public static final String EVENT_ACTIVE = "active";
    public static final String EVENT_LOGIN = "login";
    public static final String EVENT_LOGINOUT = "loginout";
    /**
     * 创建
     */
    public static final String EVENT_CREATE = "create";
    /**
     * 邀请
     */
    public static final String EVENT_INVITE = "invite";
    /**
     * 加入
     */
    public static final String EVENT_JOIN = "join";
    /**
     * 解决
     */
    public static final String EVENT_REJECT = "reject";

    /**
     * 退出会议
     */
    public static final String EVENT_EXIT = "exit";
    /**
     * 请出
     */
    public static final String EVENT_KICKOUT = "kickout";
    /**
     * 查询会议成员(members)
     */
    public static final String EVENT_MEMBERS = "members";
    /**
     * 白板操作
     */
    public static final String EVENT_WHITEBOARD = "whiteboard";
    /**
     * 通知
     */
    public static final String EVENT_NOTIFY = "notify";

    /**
     * 服务器通知
     */
    public static final String NOTIFY_LOGIN = "login";
    public static final String NOTIFY_INVITE = "invite";
    public static final String NOTIFY_REJECT = "reject";
    public static final String NOTIFY_WHITEBOARD = "whiteboard";
    public static final String NOTIFY_JOIN = "join";
    public static final String NOTIFY_FORCE_EXIT = "force_exit";
    public static final String NOTIFY_EXIT = "exit";

    //commondId 命令值
    /**
     * 增加对象
     */
    public static final int DATA_TRANS_ADDOBJECT = 1;
    /**
     * 查询对象
     */
    public static final int DATA_TRANS_OBJREQUEST = 2;
    /**
     * 查询对象响应
     */
    public static final int DATA_TRANS_OBJRESPONSE = 3;
    /**
     * 删除对象
     */
    public static final int DATA_TRANS_DELETE_OBJECT = 4;
    /**
     * 删除所有对象
     */
    public static final int DATA_TRANS_DELETE_ALL = 5;
    /**
     * 调整对象大小
     */
    public static final int DATA_TRANS_OBJ_RESIZE = 6;
    /**
     * 移动对象
     */
    public static final int DATE_TRANS_MOVEOBJ = 7;
    /**
     * 修改颜色
     */
    public static final int DATA_TRANS_COLORREF_CHANGED = 8;
    /**
     * 修改字体
     */
    public static final int DATA_TRANS_FONT_CHANGED = 9;
    /**
     * 修改线宽
     */
    public static final int DATA_TRANS_LINE_WIDTH_CHANGED = 10;
    /**
     * 修改文本内容
     */
    public static final int DATA_TRANS_TEXT_CHANGED = 11;
    /**
     * 手型指针操作
     */
    public static final int DATA_TRANS_POINTER_CMD = 12;
    /**
     * 滚动条操作
     */
    public static final int DATA_TRANS_SCROLL = 13;
    /**
     * 白板页面操作
     */
    public static final int DATA_TRANS_PAGE = 14;
    /**
     * 载入文件
     */
    public static final int DATA_TRANS_LOADFILE = 15;
    /**
     * 设置背景图
     */
    public static final int DATA_TRANS_SET_BACKBITMAP = 16;
    /**
     * 设置背景颜色
     */
    public static final int DATA_TRANS_SET_BACKCOLOR = 17;
    /**
     * 所有对象列表
     */
    public static final int DATA_TRANS_OBJECT_ALL = 18;
    /**
     * 锁定
     */
    public static final int DATA_TRANS_LOCK = 19;
    /**
     * 添加文档
     */
    public static final int DATA_TRANS_ADD_DOCUMENT_OBJECT = 20;
    /**
     * 文档查询
     */
    public static final int DATA_TRANS_DOCUMENT_OBJECT_REQUEST = 21;
    /**
     * 文档查询响应
     */
    public static final int DATA_TRANS_DOCUMENT_OBJECT_RESPONSE = 22;

    public static final int PAGE_NEXT = 2;
    public static final int PAGE_CREATE = 1;
}
