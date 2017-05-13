package cn.scooper.com.whiteboard.views.whiteboardview.shape;

import android.graphics.Canvas;

/**
 * 圆形
 */
public class CircleShape extends AbsShape {

    private int radius;

    public CircleShape(String serviceId, boolean isFull, int color, int width) {
        super(serviceId, isFull, color, width);
    }

    public void onLayout(float startX, float startY, float x, float y) {
        this.startX = startX;
        this.startY = startY;
        this.endx = x;
        this.endy = y;
        setPostion(startX, startY, x, y);
        int border = (int) mPaint.getStrokeWidth();
        mInvalidRect.set(cx - radius - border, cy - radius - border, cx + radius + border, cy + radius + border);
    }

    public void drawShape(Canvas canvas) {
        canvas.drawCircle(cx, cy, radius, mPaint);
    }

    private void setPostion(float startX, float startY, float x, float y) {
        cx = (int) ((x + startX) / 2);
        cy = (int) ((y + startY) / 2);
        radius = (int) Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2)) / 2;
    }

}