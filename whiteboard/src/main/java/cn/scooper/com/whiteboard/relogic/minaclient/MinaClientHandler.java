package cn.scooper.com.whiteboard.relogic.minaclient;

import android.util.Log;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import cn.scooper.com.easylib.rxbus.RxBus;
import cn.scooper.com.whiteboard.event.NotifyEvent;

public class MinaClientHandler extends IoHandlerAdapter {

    boolean isTotal = true;

    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        // TODO Auto-generated method stub
        super.exceptionCaught(session, cause);
        Log.e("msg", cause.toString());
    }

    /**
     * 关闭
     * */
    public void inputClosed(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        super.inputClosed(session);
        NotifyEvent notifyEvent = new NotifyEvent();
        notifyEvent.eventType = NotifyEvent.NOTIFY_CONNECT_CLOSE;
        RxBus.get().send(notifyEvent);
    }

    public void messageReceived(IoSession session, Object message)
            throws Exception {
        //TODO Auto-generated method stub
        super.messageReceived(session, message);

        //IoBuffer ioBuffer = (IoBuffer) message;
        //byte[] b = (byte[]) message;
        //ioBuffer.get(b);
        //ioBuffer.get(b);
        //前四个字节是包长度，麻痹，有时候发包长+内容,有时候先发包长，再发内容
        /*if(b.length == 4){
            isTotal = false;
			return;
		}
		byte[] content;
		if(!isTotal){
			content = b;
			isTotal = true;
		}else{
			content = Arrays.copyOfRange(b, 4, b.length);

		}
		String msg = new String(content,"utf-8");
		ReponseEntity reponseEntity = JsonUtil.getParser().fromJson(msg, ReponseEntity.class);
		Log.e("receive",reponseEntity.toString());
		UserOpEvent event = new UserOpEvent();
		event.entity = reponseEntity;
		RxBus.get().send(event);*/

    }

    public void messageSent(IoSession session, Object message) throws Exception {
        //TODO Auto-generated method stub

    }

    public void sessionClosed(IoSession session) throws Exception {
        // TODO Auto-generated method stub
    }

    public void sessionCreated(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        super.sessionCreated(session);
    }

    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        //TODO Auto-generated method stub
        super.sessionIdle(session, status);
    }

    public void sessionOpened(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        super.sessionOpened(session);
    }

}
