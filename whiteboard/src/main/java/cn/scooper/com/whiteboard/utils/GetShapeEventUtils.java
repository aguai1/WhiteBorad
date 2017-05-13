package cn.scooper.com.whiteboard.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.scooper.com.whiteboard.db.domain.ShapeBean;
import cn.scooper.com.whiteboard.event.ShapeEvent;
import cn.scooper.com.whiteboard.relogic.imagecache.WBImageLoader;
import cn.scooper.com.whiteboard.relogic.minaclient.Request;
import cn.scooper.com.whiteboard.views.whiteboardview.Constants;

/**
 * Created by zhenglikun on 2016/11/29.
 */

public class GetShapeEventUtils {

    public static ShapeEvent generateShapeEvent(BigInteger typeId, int dataSize, byte[] data) throws Exception {
        ShapeEvent event = null;
        switch (typeId.intValue()) {
            case Constants.SHAPE_PEN:
                event = dealWithPen(data);
                break;
            case Constants.SHAPE_YG_PEN:
                event = dealWithPen(data);
                break;
            case Constants.SHAPE_OVAL:
                event = dealWithLine(data, false);
                break;
            case Constants.SHAPE_LINE:
                event = dealWithLine(data, false);
                break;
            case Constants.SHAPE_RECT:
                event = dealWithLine(data, false);
                break;
            case Constants.SHAPE_FULL_OVAL:
                event = dealWithLine(data, true);
                break;
            case Constants.SHAPE_FULL_RECT:
                event = dealWithLine(data, true);
                break;
            case Constants.SHAPE_PIC:
                event = dealWithPicture(data);
                break;
            case Constants.SHAPE_TEXT:
                event = dealWithText(data);
                break;
        }
        event.shapeBean.setType(typeId.intValue());
        return event;
    }

    /**
     * 图片的添加事件
     */
    public static ShapeEvent dealWithPicture(byte[] data) {

        BigInteger objId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 0)));
        BigInteger left = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 4)));
        BigInteger top = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 8)));
        BigInteger right = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 12)));
        BigInteger bottom = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 16)));
        BigInteger dwUnSize = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 20)));
        BigInteger dwSize = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 24)));

        byte[] bytes = Arrays.copyOfRange(data, 28, data.length);
        bytes = ZlibUtils.decompress(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        ShapeEvent event = new ShapeEvent();
        event.shapeBean = new ShapeBean();
        event.shapeBean.setStartX(left.intValue());
        event.shapeBean.setStartY(top.intValue());
        event.shapeBean.setEndx(right.intValue());
        event.shapeBean.setEndy(bottom.intValue());
        event.shapeBean.setServiceShapeId(objId.toString());
        event.shapeBean.setPicPath(Request.meetingId + objId.toString());
        WBImageLoader.getInstance().saveBitmap(bitmap, Request.meetingId + objId.toString());
        return event;
    }

    public static ShapeEvent dealWithText(byte[] data) {

        BigInteger objId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 0)));
        BigInteger color = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 4)));
        BigInteger left = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 8)));
        BigInteger top = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 12)));
        BigInteger right = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 16)));
        BigInteger bottom = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 20)));
        BigInteger lfHeight = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 24)));
        BigInteger lfWidth = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 28)));
        BigInteger lfEscapement = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 32)));
        BigInteger lfOrientation = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 36)));
        BigInteger lfWeight = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 40)));

        byte lfItalic = data[44];
        byte lfUnderline = data[45];
        byte lfStrikeOut = data[46];
        byte lfCharSet = data[47];
        byte lfOutPrecision = data[48];
        byte lfClipPrecision = data[49];
        byte lfQuality = data[50];
        byte lfPitchAndFamily = data[51];

        //http://blog.163.com/hfut_quyouhu/blog/static/7847183520128193482175/
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 32; i++) {

            byte[] content = new byte[]{data[52 + i * 2], data[52 + i * 2 + 1]};
            try {
                String str = new String(content, "GB2312");
                sb.append(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int number = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 116))).intValue();
        byte[] content = Arrays.copyOfRange(data, 120, data.length);
        String result = "";
        try {
            result = new String(content, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ShapeEvent event = new ShapeEvent();
        event.shapeBean = new ShapeBean();
        event.shapeBean.setStartX(left.intValue());
        event.shapeBean.setStartY(top.intValue());
        event.shapeBean.setEndx(right.intValue());
        event.shapeBean.setEndy(bottom.intValue());
        int color1 = ColorUtil.getColor(color.intValue());
        event.shapeBean.setColor(color1);
        event.shapeBean.setServiceShapeId(objId.toString());
        event.shapeBean.setText(result);
        return event;
    }

    public static List<ShapeEvent> changeObjColor(BigInteger pageId, byte[] data) {

        BigInteger color = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 0)));
        int color1 = ColorUtil.getColor(color.intValue());
        List<ShapeEvent> mList = new ArrayList<ShapeEvent>();

        for (int i = 4; i < data.length; i += 4) {
            BigInteger objId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, i)));

            ShapeEvent event = new ShapeEvent();
            event.opType = ShapeEvent.OP_ADJUST_COLOR;
            event.shapeBean = new ShapeBean();
            event.shapeBean.setServiceShapeId(objId.toString());
            event.shapeBean.setColor(color1);
            event.shapeBean.setPageId(pageId.intValue() + "");
            mList.add(event);

        }
        return mList;
    }

    public static ShapeEvent changeObjSize(byte[] data) {
        ShapeEvent event = new ShapeEvent();
        event.opType = ShapeEvent.OP_ADJUST_BUNDS;
        BigInteger left = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 0)));
        BigInteger top = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 4)));
        BigInteger right = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 8)));
        BigInteger bottom = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 12)));

        event.shapeBean.setStartX(left.intValue());
        event.shapeBean.setStartY(top.intValue());
        event.shapeBean.setEndx(right.intValue());
        event.shapeBean.setEndy(bottom.intValue());

        return event;
    }


    /**
     * 画笔的移动事件
     *
     * @return
     */
    public static ShapeEvent dealWithPen(byte[] data) {

        ShapeEvent event = new ShapeEvent();
        event.shapeBean = new ShapeBean();
        BigInteger objId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 0)));
        BigInteger color = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 4)));
        BigInteger lineWidth = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 8)));
        BigInteger left = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 12)));
        BigInteger top = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 16)));
        BigInteger right = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 20)));
        BigInteger bottom = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 24)));
        BigInteger numer = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 28)));
        int index = 32;
        for (int i = 0; i < numer.intValue(); i++) {
            int x = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, index))).intValue();
            index += 4;
            int y = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, index))).intValue();
            index += 4;
            event.shapeBean.addPoint(new int[]{x, y});
        }

        event.shapeBean.setStartX(left.intValue());
        event.shapeBean.setStartY(top.intValue());
        event.shapeBean.setEndx(right.intValue());
        event.shapeBean.setEndy(bottom.intValue());
        int color1 = ColorUtil.getColor(color.intValue());
        event.shapeBean.setColor(color1);
        event.shapeBean.setWidth(lineWidth.intValue());
        event.shapeBean.setServiceShapeId(objId.toString());

        return event;
    }

    public static List<ShapeEvent> generateMoveShapeEvent(BigInteger pageId, byte[] data) throws Exception {
        int x = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 0))).intValue();
        int y = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 4))).intValue();

        byte[] objs = Arrays.copyOfRange(data, 8, data.length);
        List<ShapeEvent> mList = new ArrayList<ShapeEvent>();

        for (int i = 0; i < objs.length; i += 4) {
            BigInteger objId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(objs, i)));

            ShapeEvent event = new ShapeEvent();
            event.shapeBean = new ShapeBean();
            event.opType = ShapeEvent.OP_MOVE;
            event.shapeBean.setPageId(pageId.intValue() + "");
            event.shapeBean.setOffsetX(x);
            event.shapeBean.setOffsetY(y);
            event.shapeBean.setServiceShapeId(objId.toString());

            mList.add(event);

        }
/*        BigInteger left =  new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 0)));
        BigInteger top = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 4)));
        BigInteger right = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 8)));
        BigInteger bottom = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 12)));
        event.shapeBean.setStartX(left.intValue() );
        event.shapeBean.setStartY(top.intValue() );
        event.shapeBean.setEndx(right.intValue() );
        event.shapeBean.setEndy(bottom.intValue() );*/

        return mList;
    }


    public static ShapeEvent dealWithLine(byte[] data, boolean b) throws Exception {
        BigInteger objId = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 0)));
        BigInteger color = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 4)));
        BigInteger lineWidth = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 8)));
        BigInteger left = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 12)));
        BigInteger top = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 16)));
        BigInteger right = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 20)));
        BigInteger bottom = new BigInteger(TypeToBytesUtils.reverseByte(splitByteArray(data, 24)));

        ShapeEvent event = new ShapeEvent();
        event.shapeBean = new ShapeBean();
        event.shapeBean.setFull(b);
        event.shapeBean.setStartX(left.intValue());
        event.shapeBean.setStartY(top.intValue());
        event.shapeBean.setEndx(right.intValue());
        event.shapeBean.setEndy(bottom.intValue());
        int color1 = ColorUtil.getColor(color.intValue());
        event.shapeBean.setColor(color1);
        event.shapeBean.setWidth(lineWidth.intValue());
        event.shapeBean.setServiceShapeId(objId.toString());

        return event;
    }


    private static byte[] splitByteArray(byte[] arr, int start) {
        if (start + 4 > arr.length) {
            return null;
        }
        byte[] newBytes = new byte[4];

        for (int i = 0; i < 4; i++) {
            newBytes[i] = arr[i + start];
        }
        return newBytes;
    }
}
