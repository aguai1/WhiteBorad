package cn.scooper.com.whiteboard.views.whiteboardview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import cn.scooper.com.easylib.utils.BitmapUtils;
import cn.scooper.com.whiteboard.WhiteBoardApplication;

/**
 * Created by 阿怪 on 2015/8/10.
 * 画笔工具
 */
public class MultiLayoutView extends ImageView {
    /**
     * 操作模式
     * 1   画笔
     * 2  橡皮擦
     */
    public static final int MODE_ERASER = 2;
    public static final int MODE_PAINT = 1;
    public int CURRENT_MODE = MODE_PAINT;
    public float mLastPointX, mLastPointY;
    /**
     * 画笔属性
     */
    public int brushWidth = 10;
    public int brushColor = Color.BLUE;
    //背景
    public Bitmap bgBitmap;
    protected Bitmap mBitmap;
    /**
     * 最后一次点坐标
     */
    private float startX;
    private float startY;
    /**
     * 画笔集合
     */
    private List<DrawInfo> allPath;
    /**
     * 删除记录画笔集合
     */
    private List<DrawInfo> deletePath;
    //记录零时画笔
    private DrawInfo tempPath;
    private Canvas mCanvas = null; //定义画布
    private Context mContext;

    public MultiLayoutView(Context context) {
        this(context, null);
        mContext = context;
        init();
    }


    public MultiLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        init();
    }

    public MultiLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();

    }

    public int getCurrentMode() {
        return CURRENT_MODE;
    }

    public void setCurrentMode(int currentMode) {
        CURRENT_MODE = currentMode;
    }

    public int getBrushColor() {
        return brushColor;
    }

    public void setBrushColor(int brushColor) {
        this.brushColor = brushColor;
        setCurrentMode(MODE_PAINT);

    }

    public int getBrushWidth() {
        return brushWidth;
    }

    public void setBrushWidth(int brushWidth) {
        this.brushWidth = brushWidth;
    }

    public void setBackBitamapUrl(String backBitamapUrl) {
        Glide.with(WhiteBoardApplication.getInstance()).load(backBitamapUrl)
                .asBitmap()
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        bgBitmap = BitmapUtils.getSmallBitmapByY(resource, getMeasuredHeight());
                        invalidate();
                    }
                });

    }

    private void init() {
        allPath = new ArrayList<>();
        deletePath = new ArrayList<>();
    }

    private void touchStart(float x, float y) {
        tempPath.path.moveTo(x, y);
        startX = x;
        startY = y;
        invalidate();
    }

    private void touchMove(float x, float y) {

        tempPath.path.quadTo(startX, startY, (x + startX) / 2, (y + startY) / 2);
        mCanvas.drawPath(tempPath.path, tempPath.paint);
        startX = x;
        startY = y;
        invalidate();
    }

    private void touchUp() {
        tempPath.path.lineTo(startX, startY);
        mCanvas.drawPath(tempPath.path, tempPath.paint);
        allPath.add(tempPath);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas != null) {
            if (mBitmap == null) {
                mBitmap = BitmapUtils.createBitmap(getMeasuredWidth(), getMeasuredHeight());
                mCanvas = new Canvas(mBitmap);
                refresh();
            }
            if (bgBitmap != null) {
                canvas.drawBitmap(bgBitmap, 0, 0, null);
            }

            // 在View上绘制
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        return paintUtils(event);
    }

    private boolean eraser(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private boolean paintUtils(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                mLastPointY = y;
                mLastPointX = x;
                tempPath = new DrawInfo();
                Paint selPaint = new Paint();
                selPaint.setAntiAlias(true);
                selPaint.setStyle(Paint.Style.STROKE);
                selPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
                selPaint.setStrokeCap(Paint.Cap.ROUND);
                selPaint.setStrokeWidth(brushWidth);
                if (CURRENT_MODE == MODE_ERASER) {
                    selPaint.setColor(Color.TRANSPARENT);
                    selPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                } else {
                    selPaint.setColor(brushColor);
                }

                tempPath.paint = selPaint;
                touchStart(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                break;
        }
        return true;
    }

    /**
     * 撤销的核心思想就是将画布清空，
     * <p>
     * 将保存下来的Path路径最后一个移除掉，
     * <p>
     * 重新将路径画在画布上面。
     */
    public void undo() {
        if (allPath.size() > 0) {
            DrawInfo remove = allPath.remove(allPath.size() - 1);
            deletePath.add(remove);
            refresh();
        }
    }

    private void refresh() {
        mBitmap.eraseColor(Color.TRANSPARENT);
        for (int i = 0; i < allPath.size(); ++i) {
            DrawInfo info = allPath.get(i);
            mCanvas.drawPath(info.path, info.paint);
        }
        invalidate();
    }

    /**
     * 恢复画笔
     */
    public void redo() {
        if (deletePath.size() > 0) {
            //将删除的路径列表中的最后一个，也就是最顶端路径取出（栈）,并加入路径保存列表中
            DrawInfo dp = deletePath.get(deletePath.size() - 1);
            allPath.add(dp);
            deletePath.remove(deletePath.size() - 1);
            refresh();
        }
    }

    /*

     * 清空的主要思想就是初始化画布
     * 将保存路径的两个List清空
     * */
    public void removeAllPaint() {
        bgBitmap = null;
        deletePath.clear();
        allPath.clear();
        refresh();
    }

    class DrawInfo {
        Path path = new Path();
        Paint paint;
    }
}
