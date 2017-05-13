package cn.scooper.com.whiteboard.views.whiteboardview.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.util.List;
import java.util.Random;

/**
 * 曲线
 */
public class PathShape extends AbsShape {

    public PathShape(String id, int color, int width, boolean isEraser) {
        super(id, color, width);
        init(width, isEraser);
    }

    public PathShape(String serviceShapeId, int color, int width, boolean isEraser, List<int[]> points) {
        super(serviceShapeId, color, width);
        init(width, isEraser);
        mPath.addPathPoints(points);
    }

    public PathShape(int brushColor, int brushWidth, boolean isEraser) {
        super((int)(System.currentTimeMillis()*1000)+"", brushColor, brushWidth);
        init(width, isEraser);
    }


    private void init(int width, boolean isEraser) {
        mPath = new SerializablePath();
        if (isEraser) {
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setStrokeWidth(width);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        } else {

            mPaint.setStrokeWidth(width);
            // 去锯齿
            mPaint.setAntiAlias(true);
            // 设置paint的 style 为STROKE：空心
            mPaint.setStyle(Paint.Style.STROKE);

            // 接合处为圆弧
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            // 画笔样式圆形
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }
    }


    public void onLayout(float startX, float startY, float x, float y) {

    }

    public void onLayout(float x, float y) {

        for (Point point : mPath.getPathPoints()) {
            point.x += x;
            point.y += y;
        }
        mPath.loadPathPointsAsQuadTo();
    }

    public void onAddPoint(float x, float y) {

        // 判断是不是down事件
        if (startX == 0 && startY == 0) {
            this.startX = x;
            this.startY = y;
            mPath.addPathPoint(new Point((int) startX, (int) startY));
        }
        int border = (int) mPaint.getStrokeWidth();
        float mCurveEndX = (x + startX) / 2;
        float mCurveEndY = (y + startY) / 2;
        mPath.addQuadPathPoint(new Point((int) mCurveEndX, (int) mCurveEndY));
        mInvalidRect.union((int) mCurveEndX - border,
                (int) mCurveEndY - border, (int) mCurveEndX + border,
                (int) mCurveEndY + border);
        startX = x;
        startY = y;
    }

    public void drawShape(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }

    public List<Point> getPoints() {
        return mPath.getPathPoints();
    }
}