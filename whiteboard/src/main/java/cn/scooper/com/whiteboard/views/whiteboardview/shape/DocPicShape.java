package cn.scooper.com.whiteboard.views.whiteboardview.shape;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.whiteboard.relogic.imagecache.WBImageLoader;

/**
 * 文档图片
 */
public class DocPicShape extends AbsShape {


    public DocPicShape(String serviceId, String picPath) {
        super(serviceId);
        this.picUrl = picPath;
    }

    /**
     * Get data from stream
     *
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    public void onLayout(float mStartX, float mStartY, float x, float y) {
        this.startX = mStartX;
        this.startY = mStartY;
        this.endx = x;
        this.endy = y;

        int border = (int) mPaint.getStrokeWidth();
        mInvalidRect.set(mStartX - border, mStartY - border, mStartX + border, mStartY + border);
        mInvalidRect.union(x - border, y - border, x + border, y + border);
    }

    public void drawShape(final Canvas canvas) {
        Bitmap bitmap1 = WBImageLoader.getInstance().loadBitmap(picUrl);
        if (bitmap1 == null) {
            try {
                byte[] image = getImage(picUrl);
                if (image != null) {
                    bitmap1 = BitmapFactory.decodeByteArray(image, 0, image.length);
                    WBImageLoader.getInstance().saveBitmap(bitmap1, picUrl);
                }
            } catch (Exception e) {
                LogUtil.e("PicShape", "loadImageError" + e.toString());
            }
        }
        if (bitmap1 != null) {
            canvas.drawBitmap(bitmap1, startX, startY, null);
        }
    }

    /**
     * Get image from newwork
     *
     * @param path The path of image
     * @return byte[]
     * @throws Exception
     */
    public byte[] getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return readStream(inStream);
        }
        return null;
    }

    public String getPicPath() {
        return picUrl;
    }
}