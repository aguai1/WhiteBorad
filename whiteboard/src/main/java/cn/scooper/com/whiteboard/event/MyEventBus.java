package cn.scooper.com.whiteboard.event;


import java.util.ArrayList;
import java.util.List;

import cn.scooper.com.easylib.rxbus.Event;

/**
 * Created by Aguai on 2016/11/29.
 * <p>
 * 自定义 事件发送器
 */
public class MyEventBus {

    private static MyEventBus mInstance;
    private List<ListenerPage> pageList = new ArrayList<>();

    public static MyEventBus get() {
        if (mInstance == null) {
            synchronized (MyEventBus.class) {
                if (mInstance == null) {
                    mInstance = new MyEventBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 增加指定类的监听
     */
    public ListenerPage addEventListener(Class aClass, OnReciveBroadCastListener listener) {
        ListenerPage page = new ListenerPage();
        page.aClass = aClass;
        page.reciveBroadCast = listener;
        pageList.add(page);
        return page;
    }

    public void sendEvent(Event event) {
        for (int i = 0; i < pageList.size(); ++i) {
            ListenerPage page = pageList.get(i);
            if (page.aClass == event.getClass()) {
                page.reciveBroadCast.onRecive(event);
            }
        }
    }

    public void removeListener(ListenerPage listenerPage) {
        pageList.remove(listenerPage);
    }

    public interface OnReciveBroadCastListener {
        void onRecive(Event e);
    }

    public class ListenerPage {
        public OnReciveBroadCastListener reciveBroadCast;
        public Class<? extends Event> aClass;
    }
}
