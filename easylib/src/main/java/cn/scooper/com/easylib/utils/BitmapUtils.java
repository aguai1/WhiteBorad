package cn.scooper.com.easylib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;

import java.io.InputStream;

/**
 * Created by 阿怪 on 2015/8/10.
 * 图片操作工具类
 */
public class BitmapUtils {

    /**
     * 通过id读取图片
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 等比例根据x坐标压缩
     */
    public static Bitmap getSmallBitmapByX(Bitmap bitmap, float f) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale2 = (float) f / w;
        matrix.postScale(scale2, scale2);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 等比例根据y坐标压缩
     */
    public static Bitmap getSmallBitmapByY(Bitmap bitmap, double y) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale2 = (float) y / h;
        matrix.postScale(scale2, scale2);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 旋转Bitmap
     *
     * @param b
     * @param rotateDegree
     * @return
     */
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
        return rotaBitmap;
    }

    public static Bitmap getFilpBitmap(Bitmap b) {
        Matrix matrix = new Matrix();
        // sw sh的绝对值为绽放宽高的比例，sw为负数表示X方向翻转，sh为负数表示Y方向翻转
        matrix.postScale(-1, 1);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
        return rotaBitmap;
    }


    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float temp = ((float) height) / ((float) width);
        int newHeight = (int) ((newWidth) * temp);
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static Bitmap loadBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bm = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bm;
    }

    public static Bitmap createBitmap(int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h,
                Bitmap.Config.ARGB_8888);
        return bitmap;
    }
}
