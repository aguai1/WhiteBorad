package cn.scooper.com.whiteboard.views.whiteboardview.shape;

import android.graphics.Canvas;

/**
 * 直线
 */
public class LineShape extends AbsShape {
    protected SerializablePath mPath;

    public LineShape(String serviceId, int color, int width) {
        super(serviceId, color, width);
        mPath = new SerializablePath();
    }

    public void onLayout(float startX, float startY, float endx, float endy) {
        this.startX = startX;
        this.startY = startY;
        this.endx = endx;
        this.endy = endy;
        cx = (int) ((endx + startX) / 2);
        cy = (int) ((endy + startY) / 2);
        mPath.reset();
        mPath.moveTo(startX, startY);
        mPath.lineTo(endx, endy);

        int border = (int) mPaint.getStrokeWidth() * 2;
        mInvalidRect.set((int) startX - border, (int) startY - border, (int) startX + border, (int) startY + border);
        mInvalidRect.union((int) endx - border, (int) endy - border, (int) endx + border, (int) endy + border);
    }

    public void drawShape(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }
}