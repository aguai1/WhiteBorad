package cn.scooper.com.whiteboard.utils;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.io.ByteArrayOutputStream;

import cn.scooper.com.whiteboard.db.domain.ShapeBean;
import cn.scooper.com.whiteboard.relogic.imagecache.WBImageLoader;
import cn.scooper.com.whiteboard.views.whiteboardview.Constants;

/**
 * Created by zhenglikun on 2016/11/30.
 */

/**
 * 生成shapEvent数组
 */
public class AnalysicShapeEventutils {

    public static byte[] analysicShapeBean(ShapeBean bean) throws Exception {
        byte[] bytes = null;
        switch (bean.getType()) {
            case Constants.SHAPE_PEN:
                bytes = dealWithPen(bean);
                break;
            case Constants.SHAPE_YG_PEN:
                bytes = dealWithPen(bean);
                break;
            case Constants.SHAPE_LINE:
                break;
            case Constants.SHAPE_RECT:
                break;
            case Constants.SHAPE_FULL_RECT:
                break;
            case Constants.SHAPE_OVAL:
                break;
            case Constants.SHAPE_FULL_OVAL:
                break;
            case Constants.SHAPE_TEXT:
                break;
            case Constants.SHAPE_PIC:
                bytes = dealWithPicture(bean);
                break;
            case Constants.SHAPE_DOC_PIC:
                break;
        }
        return bytes;
    }

    private static byte[] getBitmapByteArr(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    public static byte[] dealWithPicture(ShapeBean shapeBean) throws Exception {
        Bitmap bitmap = WBImageLoader.getInstance().loadBitmap(shapeBean.picPath);
        byte[] byteArray = getBitmapByteArr(bitmap);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] dwUnSize = TypeToBytesUtils.intToByte4(byteArray.length);
        byte[] objId = TypeToBytesUtils.intToByte4(Integer.parseInt(shapeBean.getServiceShapeId()));
        byte[] left = TypeToBytesUtils.intToByte4((int) shapeBean.getStartX());
        byte[] top = TypeToBytesUtils.intToByte4((int) shapeBean.getStartY());
        byte[] right = TypeToBytesUtils.intToByte4((int) shapeBean.getEndx());
        byte[] bottom = TypeToBytesUtils.intToByte4((int) shapeBean.getEndy());

        byte[] dwByteBitmap = ZlibUtils.compress(byteArray);

        byte[] dwSize = TypeToBytesUtils.intToByte4(dwByteBitmap.length);
        bos.write(objId);
        bos.write(left);
        bos.write(top);
        bos.write(right);
        bos.write(bottom);
        bos.write(dwUnSize);
        bos.write(dwSize);
        bos.write(dwByteBitmap);

        byte[] type = TypeToBytesUtils.intToByte4(Constants.SHAPE_PIC);
        byte[] byteLength = TypeToBytesUtils.intToByte4(bos.toByteArray().length);

        byte[] result = TypeToBytesUtils.byteMerger(type, byteLength, bos.toByteArray());
        return result;

    }


    public static byte[] dealWithPen(ShapeBean pathShape) throws Exception {
        int num = pathShape.getPoints().size();
        byte[] number = TypeToBytesUtils.intToByte4(num);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] objId = TypeToBytesUtils.intToByte4(Integer.parseInt(pathShape.getServiceShapeId()));
        byte[] colorId = TypeToBytesUtils.intToByte4(ColorUtil.getColorRFT(pathShape.getColor()));
        byte[] width = TypeToBytesUtils.intToByte4(pathShape.getWidth());

        byte[] left = TypeToBytesUtils.intToByte4((int) pathShape.getStartX());
        byte[] top = TypeToBytesUtils.intToByte4((int) pathShape.getStartY());
        byte[] right = TypeToBytesUtils.intToByte4(pathShape.getPoints().get(num - 1)[0]);
        byte[] bottom = TypeToBytesUtils.intToByte4(pathShape.getPoints().get(num - 1)[1]);


        bos.write(objId);
        bos.write(colorId);
        bos.write(width);
        bos.write(left);
        bos.write(top);
        bos.write(right);
        bos.write(bottom);
        bos.write(number);

        for (int i = 0; i < num; i += 1) {
            int[] arr = pathShape.getPoint(i);
            Point p = new Point(arr[0], arr[1]);
            byte[] x = TypeToBytesUtils.intToByte4(p.x);
            byte[] y = TypeToBytesUtils.intToByte4(p.y);
            bos.write(x);
            bos.write(y);
        }

        byte[] type = TypeToBytesUtils.intToByte4(Constants.SHAPE_PEN);
        byte[] byteLength = TypeToBytesUtils.intToByte4(bos.toByteArray().length);

        byte[] result = TypeToBytesUtils.byteMerger(type, byteLength, bos.toByteArray());
        return result;
        //event.shapeBean.get
    }

}
