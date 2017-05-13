package cn.scooper.com.whiteboard.relogic.minaclient;

/**
 * Created by zhenglikun on 2016/11/22.
 */

import android.util.Log;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.math.BigInteger;
import java.util.Arrays;

import cn.scooper.com.whiteboard.utils.TypeToBytesUtils;

/**
 * @author BruceYang
 */
public class ByteArrayDecoder extends ProtocolDecoderAdapter {

    private int length = 0;
    private byte[] content = new byte[]{};

    @Override
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
            throws Exception {
        // TODO Auto-generated method stub

        int limit = in.limit();
        byte[] b = new byte[limit];
        in.get(b);
        out.write(b);


        //先读长度
        byte[] lengthByte;

        if (length == 0 && b.length >= 4) {
            lengthByte = Arrays.copyOfRange(b, 0, 4);
            length = new BigInteger(TypeToBytesUtils.reverseByte(lengthByte)).intValue();
            content = TypeToBytesUtils.byteMerger(content, Arrays.copyOfRange(b, 4, b.length));
        } else {
            content = TypeToBytesUtils.byteMerger(content, Arrays.copyOfRange(b, 0, b.length));
        }
        Log.e("msg", length + "");
        //说明还没有读取结束
        if (content.length < length) {
            return;
        } else if (content.length == length) {
            length = 0;
            try {
                opData(content);
                content = new byte[]{};
            } catch (Exception e) {
                Log.e("msg", e.toString());
            }

        } else {
            dealWithPack(content, length);
        }
    }

    /**
     * 处理粘包
     */
    public void dealWithPack(byte[] pack, int len) {

        int otherPackLength = len;
        while (true) {
            //说明当前包没有被处理结束
            if (otherPackLength > pack.length) {
                length = otherPackLength;
                content = pack;
                break;
            }
            //截取前面有效的字段
            byte[] newByte = Arrays.copyOfRange(pack, 0, otherPackLength);
            try {
                opData(newByte);
            } catch (Exception e) {
                Log.e("msg", e.toString());
            }
            pack = remove(otherPackLength, pack);
            if (pack.length == 0) {
                length = 0;
                content = new byte[]{};
                break;
            }
            byte[] lengthByte = Arrays.copyOfRange(pack, 0, 4);
            pack = remove(4, pack);
            otherPackLength = new BigInteger(TypeToBytesUtils.reverseByte(lengthByte)).intValue();
            //粘包处理后
            if (pack.length == 0) {
                length = otherPackLength;
                content = new byte[]{};
                break;
            }
        }

    }

    /**
     * 去除前几位字符
     *
     * @param num
     * @param pack
     * @return
     */
    public byte[] remove(int num, byte[] pack) {

        byte[] bytes = new byte[pack.length - num];
        for (int i = 0; i < pack.length - num; i++) {
            bytes[i] = pack[i + num];
        }
        return bytes;
    }

    /**
     * 处理解析以后的数据
     *
     * @throws Exception
     */
    public void opData(byte[] bytes) throws Exception {
        String msg = new String(bytes, "utf-8");
        try {
            Log.e("msg", msg);
            DataInfoBean dealWithData = new DataInfoBean(msg);
            NotifyApp.INSTANCE.dealWith(dealWithData.op, dealWithData.shapeType, dealWithData.data);
        } catch (Exception e) {
            Log.e("msg", e.toString());
        }

    }

}