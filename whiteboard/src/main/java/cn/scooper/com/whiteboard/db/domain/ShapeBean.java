package cn.scooper.com.whiteboard.db.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

import cn.scooper.com.whiteboard.views.whiteboardview.Constants;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.AbsShape;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.DocPicShape;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.LineShape;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.OvalShape;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.PathShape;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.PicShape;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.RectangleShape;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.TextShape;

@DatabaseTable(tableName = "ShapeBean")
public class ShapeBean {
    /**
     * 数据
     */
    @DatabaseField
    public float startX;
    @DatabaseField
    public float startY;
    @DatabaseField
    public float endx;
    @DatabaseField
    public float endy;
    @DatabaseField
    public int color;
    @DatabaseField
    public int width;
    @DatabaseField
    public String picPath = "";
    @DatabaseField
    public int other;
    @DatabaseField
    public boolean isFull;
    @DatabaseField
    public String text;
    /**
     * id
     */
    @DatabaseField(generatedId = true)
    protected int id;
    /**
     * 类型
     */
    @DatabaseField
    protected int type;

    /**
     * 远程id
     */
    @DatabaseField
    protected String serviceShapeId;
    @DatabaseField
    protected String meetingId;
    /**
     * 远程页面id
     */
    @DatabaseField
    protected String pageId;


    private int offsetY;

    private int offsetX;

    @DatabaseField(persisterClass = SerializableCollectionsType.class)
    private List<int[]> points = new ArrayList<int[]>();

    public static AbsShape getAbsShape(ShapeBean shapeBean) {
        int type = shapeBean.getType();
        AbsShape endshape = null;
        switch (type) {
            case Constants.SHAPE_PEN:
                endshape = new PathShape(shapeBean.serviceShapeId, shapeBean.color, shapeBean.width, false, shapeBean.points);
                break;
            case Constants.SHAPE_YG_PEN:
                endshape = new PathShape(shapeBean.serviceShapeId, shapeBean.color, shapeBean.width, false, shapeBean.points);
                break;
            case Constants.SHAPE_LINE:
                endshape = new LineShape(shapeBean.serviceShapeId, shapeBean.color, shapeBean.width);
                break;
            case Constants.SHAPE_RECT:
                endshape = new RectangleShape(shapeBean.serviceShapeId, shapeBean.isFull, shapeBean.color, shapeBean.width);
                break;
            case Constants.SHAPE_FULL_RECT:
                endshape = new RectangleShape(shapeBean.serviceShapeId, shapeBean.isFull, shapeBean.color, shapeBean.width);
                break;
            case Constants.SHAPE_OVAL:
                endshape = new OvalShape(shapeBean.serviceShapeId, shapeBean.isFull, shapeBean.color, shapeBean.width);
                break;
            case Constants.SHAPE_FULL_OVAL:
                endshape = new OvalShape(shapeBean.serviceShapeId, shapeBean.isFull, shapeBean.color, shapeBean.width);
                break;
            case Constants.SHAPE_TEXT:
                endshape = new TextShape(shapeBean.serviceShapeId, shapeBean.text, shapeBean.color);
                break;
            case Constants.SHAPE_PIC:
                endshape = new PicShape(shapeBean.serviceShapeId, shapeBean.picPath);
                break;
            case Constants.SHAPE_DOC_PIC:
                endshape = new DocPicShape(shapeBean.serviceShapeId, shapeBean.picPath);
                break;
        }
        //处理未指定类型情况，构造一个默认的对象
        if (endshape == null) {
            endshape = new LineShape(shapeBean.serviceShapeId, shapeBean.color, 10);
            endshape.setOffsetX(shapeBean.offsetX);
            endshape.setOffsetY(shapeBean.offsetY);
        }
        endshape.setWidth(shapeBean.getWidth());
        endshape.onLayout(shapeBean.startX, shapeBean.startY, shapeBean.endx, shapeBean.endy);
        endshape.setMeetingPage(shapeBean.getPageId());
        return endshape;
    }

    public static List<AbsShape> getAbsShape(List<ShapeBean> allByMeetingIdAndPage) {
        List<AbsShape> shapes = new ArrayList<>();
        for (ShapeBean shapeBean : allByMeetingIdAndPage) {
            AbsShape endshape = getAbsShape(shapeBean);
            if (endshape != null) {
                endshape.onLayout(shapeBean.startX, shapeBean.startY, shapeBean.endx, shapeBean.endy);
                shapes.add(endshape);
            }
        }
        return shapes;
    }

    public void addPoint(int[] point) {
        points.add(point);
    }

    public int[] getPoint(int position) {
        return points.get(position);
    }

    public int getPointSize() {
        return points.size();
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<int[]> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<int[]> points) {
        this.points = points;
    }

    public void setPoints(List<int[]> points) {
        this.points = points;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndx() {
        return endx;
    }

    public void setEndx(int endx) {
        this.endx = endx;
    }

    public void setEndx(float endx) {
        this.endx = endx;
    }

    public float getEndy() {
        return endy;
    }

    public void setEndy(int endy) {
        this.endy = endy;
    }

    public void setEndy(float endy) {
        this.endy = endy;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getServiceShapeId() {
        return serviceShapeId;
    }

    public void setServiceShapeId(String serviceShapeId) {
        this.serviceShapeId = serviceShapeId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public int getOther() {
        return other;
    }

    public void setOther(int other) {
        this.other = other;
    }

    public String isText() {
        return text;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }
}