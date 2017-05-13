package cn.scooper.com.whiteboard.views.whiteboardview.shape;

import android.graphics.Canvas;
import android.graphics.RectF;

public class OvalShape extends AbsShape {
    private RectF mDrawRect;

    public OvalShape(String serviceId, boolean isFull, int color, int width) {
        super(serviceId, isFull, color, width);
        mDrawRect = new RectF();
    }

    public void onLayout(float startX, float startY, float x, float y) {
        this.startX = startX;
        this.startY = startY;
        this.endx = x;
        this.endy = y;
        cx = (int) ((x + startX) / 2);
        cy = (int) ((y + startY) / 2);
        mDrawRect.set(startX, startY, x, y);

        int border = (int) mPaint.getStrokeWidth();
        mInvalidRect.set(startX - border, startY - border, startX + border, startY + border);
        mInvalidRect.union(x - border, y - border, x + border, y + border);
    }

    public void drawShape(Canvas canvas) {
        canvas.drawOval(mDrawRect, mPaint);
    }

}