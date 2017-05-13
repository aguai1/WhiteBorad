package cn.scooper.com.whiteboard.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by Aguai on 2016/12/6.
 */

public class ZlibUtils {

    /**
     * 压缩
     *
     * @param bytes 待压缩数据
     * @return byte[] 压缩后的数据
     */
    public static byte[] compress(byte[] bytes) {
        ByteArrayOutputStream aos=new ByteArrayOutputStream();
        Deflater inflater=new Deflater(9,true);
        inflater.setInput(bytes);
        inflater.finish();
        byte[] buff=new byte[1024];
        int byteNum=0;
        while(!inflater.finished()){
            byteNum=inflater.deflate(buff);
            aos.write(buff, 0, byteNum);
        }
        return aos.toByteArray();
    }

    /**
     * 解缩
     */
    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];
        Inflater decompresser = new Inflater(true);
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return output;
    }


}