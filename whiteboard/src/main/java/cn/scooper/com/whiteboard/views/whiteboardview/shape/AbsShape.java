package cn.scooper.com.whiteboard.views.whiteboardview.shape;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import cn.scooper.com.whiteboard.db.domain.ShapeBean;

public abstract class AbsShape implements IShape {

    protected SerializablePath mPath = new SerializablePath();
    protected String serviceId;
    protected boolean isFull = false;
    protected String meetingPage;
    protected String meetingId;
    protected int shapeType;

    protected Paint mPaint;
    protected int cx;
    protected int cy;

    protected float startX;
    protected float startY;
    protected float endx;
    protected float endy;
    protected String picUrl;
    protected String text;
    protected int color = Color.BLACK;
    protected int width;
    protected RectF mInvalidRect = new RectF();
    private int offsetY;
    private int offsetX;

    public AbsShape(String serviceId) {
        this.serviceId = serviceId;
        initPaint();
    }

    public AbsShape(String serviceId, int color) {
        this.serviceId = serviceId;
        this.color = color;
        initPaint();
        // 设置paint的颜色
        mPaint.setColor(color);

    }

    public AbsShape(String serviceId, int color, int width) {
        this.serviceId = serviceId;
        this.width = width;
        this.color = color;
        initPaint();
        // 设置paint的颜色
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);

    }


    /**
     * @param isFull 是否空心
     * @param color  颜色
     */
    public AbsShape(String serviceId, boolean isFull, int color, int width) {
        this.serviceId = serviceId;
        this.isFull = isFull;
        this.width = width;
        this.color = color;
        initPaint();
        // 设置paint的颜色
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);
        // 设置paint的 style 为STROKE：空心
        mPaint.setStyle(isFull ? Paint.Style.FILL : Paint.Style.STROKE);

    }


    private void initPaint() {
        // 去锯齿
        mPaint = new Paint();
        // 去锯齿
        mPaint.setAntiAlias(true);
        // 设置paint的 style 为STROKE：空心
        mPaint.setStyle(Paint.Style.STROKE);
        // 设置paint的外框宽度
        mPaint.setStrokeWidth(4);
        // 获取跟清晰的图像采样
        mPaint.setDither(true);
        // 接合处为圆弧
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // 画笔样式圆形
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        // 设置paint的颜色
        mPaint.setColor(color);
    }


    public ShapeBean toShapeBean() {
        ShapeBean shapeBean = new ShapeBean();
        shapeBean.setPageId(meetingPage);
        shapeBean.setServiceShapeId(serviceId);
        shapeBean.setMeetingId(meetingId);
        shapeBean.setType(shapeType);
        shapeBean.startX = startX;
        shapeBean.startY = startY;
        shapeBean.color = color;
        shapeBean.endx = endx;
        shapeBean.endy = endy;
        shapeBean.isFull = isFull;
        shapeBean.width = width;
        shapeBean.picPath = picUrl;
        shapeBean.text = text;
        shapeBean.setPoints(mPath.getPathArrayPoints());
        return shapeBean;
    }

    public float[] getCenterPostion() {
        return new float[]{cx, cy};
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public String getMeetingPage() {
        return meetingPage;
    }

    public void setMeetingPage(String meetingPage) {
        this.meetingPage = meetingPage;
    }

    public int getShapeType() {
        return shapeType;
    }

    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public int getCx() {
        return cx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public int getCy() {
        return cy;
    }

    public void setCy(int cy) {
        this.cy = cy;
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public float getEndx() {
        return endx;
    }

    public void setEndx(float endx) {
        this.endx = endx;
    }

    public float getEndy() {
        return endy;
    }

    public void setEndy(float endy) {
        this.endy = endy;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
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
        // 设置paint的颜色
        mPaint.setColor(color);
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        mPaint.setStrokeWidth(width);
        this.width = width;
    }

    public RectF getmInvalidRect() {
        return mInvalidRect;
    }

    public void setmInvalidRect(RectF mInvalidRect) {
        this.mInvalidRect = mInvalidRect;
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
}