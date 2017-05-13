package cn.scooper.com.whiteboard.views.whiteboardview.shape;

import android.graphics.Canvas;

public interface IShape {

    /**
     * 设定位置
     */
    void onLayout(float startX, float startY, float x, float y);


    /**
     * 绘制函数
     */
    void drawShape(Canvas canvas);


}