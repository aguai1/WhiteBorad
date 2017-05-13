package cn.scooper.com.whiteboard.relogic.minaclient;

/**
 * Created by zhenglikun on 2016/11/22.
 */

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @author BruceYang
 */
public class ByteArrayCodecFactory implements ProtocolCodecFactory {

    private ByteArrayDecoder decoder;
    private ByteArrayEncoder encoder;

    public ByteArrayCodecFactory() {
        encoder = new ByteArrayEncoder();
        decoder = new ByteArrayDecoder();
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }

}
