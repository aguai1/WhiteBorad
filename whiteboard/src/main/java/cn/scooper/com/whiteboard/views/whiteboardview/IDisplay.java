package cn.scooper.com.whiteboard.views.whiteboardview;

import java.util.List;

import cn.scooper.com.whiteboard.views.whiteboardview.shape.AbsShape;

/**
 *  Created by Aguai on 2016/11/20.
 *  SfDisplayInfoView功能
 */
public interface IDisplay {
    /**
     * 增加一个对象
     *
     * @param shape
     */
    void addShape(AbsShape shape);

    /**
     * 批量增加
     *
     * @param shapes
     */
    void addShapes(List<AbsShape> shapes);

    void removeShape(String serviceId);


    void removeAllShape();

    /**
     * 移动shape
     *
     * @param serviceId
     * @param x         startx偏移量
     * @param y
     */
    void moveShape(String serviceId, int x, int y);

    void refresh();

}
