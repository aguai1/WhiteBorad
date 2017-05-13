package cn.scooper.com.whiteboard.relogic.minaclient;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public enum MinaClient {
    INSTANCE;


    private IoSession session;
    private IoConnector connector;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public void stopConnect() {

        if (session != null && connector != null) {
//            session.getCloseFuture().awaitUninterruptibly();
            connector.dispose();
        }
    }

    public void connectToServer(String host, int port) throws Exception {
        session = null;
        connector = new NioSocketConnector();
        connector.setConnectTimeout(8000);

        //文本过滤
/*		connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(
                new TextLineCodecFactory(
				Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(),
				LineDelimiter.WINDOWS.getValue())));*/
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory()));


        connector.setHandler(new MinaClientHandler());
        ConnectFuture futrue = connector.connect(new InetSocketAddress(host, port));

        futrue.awaitUninterruptibly();
        try {
            session = futrue.getSession();
        } catch (Exception e) {
            throw new Exception("无法连接到服务器");
        }

/*		*/
    }

    public void sendMsg(byte[] msg) {
        if (session != null) {
            session.write(IoBuffer.wrap(msg));
        }
    }
}
