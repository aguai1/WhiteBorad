package cn.scooper.com.whiteboard.utils;

/**
 * Created by zhenglikun on 2016/11/18.
 */

public class TypeToBytesUtils {
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2, byte[] byte_3) {

        byte[] a = byteMerger(byte_1, byte_2);
        byte[] b = byteMerger(a, byte_3);
        return b;
    }

    public static byte[] intToByte4(int sum) {
        byte[] arr = new byte[4];
        arr[3] = (byte) (sum >> 24);
        arr[2] = (byte) (sum >> 16);
        arr[1] = (byte) (sum >> 8);
        arr[0] = (byte) (sum & 0xff);
        System.out.println(arr[3] + ":" + arr[2] + ":" + arr[1] + ":" + arr[0]);
        return arr;
    }

    public static int byte4Toint(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    public static byte[] reverseByte(byte[] target) {
        byte[] a = new byte[target.length];
        for (int i = 0; i < target.length; i++) {
            a[i] = target[target.length - 1 - i];
        }
        return a;
    }

    //字节转char
    public static char byteToChar(byte a, byte b) {
        char c = (char) (((a & 0xFF) << 8) | (b & 0xFF));
        return c;
    }
}
