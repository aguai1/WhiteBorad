package cn.scooper.com.whiteboard.views.whiteboardview.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import cn.scooper.com.whiteboard.relogic.imagecache.WBImageLoader;

/**
 * 图片
 */
public class PicShape extends AbsShape {


    public PicShape(String id, String picPath) {
        super(id);
        this.picUrl = picPath;
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
        if (bitmap1 != null) {
            canvas.drawBitmap(bitmap1, startX, startY, null);
        }
    }


    public String getPicPath() {
        return picUrl;
    }
}