package cn.scooper.com.whiteboard.views.whiteboardview.shape;

import android.graphics.Canvas;

/**
 * 正方形
 */
public class SquareShape extends AbsShape {


    public SquareShape(String id, boolean isFull, int color, int width) {
        super(id, isFull, color, width);
    }

    public void onLayout(float mStartX, float mStartY, float x, float y) {
        cx = (int) ((x + startX) / 2);
        cy = (int) ((y + startY) / 2);

        this.startX = mStartX;
        this.startY = mStartY;
        this.endx = x;
        this.endy = y;

        int border = (int) mPaint.getStrokeWidth();
        mInvalidRect.set(mStartX - border, mStartY - border, mStartX + border,
                mStartY + border);
        mInvalidRect.union(x - border, y - border, x + border, y + border);
    }

    public void drawShape(Canvas canvas) {
        if ((endx > startX && endy > endy) || (endx < startX && endy < startY)) {
            if (Math.abs(endx - startX) > Math.abs(endy - startY)) {
                canvas.drawRect(startX, startY, startX + endy - startY, endy,
                        mPaint);
            } else {
                canvas.drawRect(startX, startY, endx, startY + endx - startX,
                        mPaint);
            }
        } else {
            if (Math.abs(endx - startX) > Math.abs(endy - startY)) {
                canvas.drawRect(startX, startY, startX + startY - endy, endy,
                        mPaint);
            } else {
                canvas.drawRect(startX, startY, endx, startY + startX - endx,
                        mPaint);
            }
        }
    }


}