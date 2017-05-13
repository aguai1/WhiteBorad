package cn.scooper.com.whiteboard.views.whiteboardview.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class TextShape extends AbsShape {
    private RectF mDrawRect;

    public TextShape(String serviceId, String str, int cyan) {
        super(serviceId);
        text = str;
        mDrawRect = new RectF();
        mPaint = new Paint();
        mPaint.setColor(cyan);
        mPaint.setTextSize(24);
        mPaint.setAntiAlias(true);//去除锯齿
        mPaint.setFilterBitmap(true);//对位图进行滤波处理
    }

    public void onLayout(float startX, float startY, float x, float y) {
        this.startX = startX;
        this.startY = startY;
        this.endx = x;
        this.endy = y;
        cx = (int) ((x + startX) / 2);
        cy = (int) ((y + startY) / 2);
        mDrawRect.set(startX, startY, x, y);
    }

    public void drawShape(Canvas canvas) {
        canvas.drawText(text, cx, cy, mPaint);
    }

}