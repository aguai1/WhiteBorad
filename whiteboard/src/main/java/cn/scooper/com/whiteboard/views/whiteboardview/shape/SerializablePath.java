package cn.scooper.com.whiteboard.views.whiteboardview.shape;

import android.graphics.Path;
import android.graphics.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializablePath extends Path implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Point> pathPoints;
    private int startX;
    private int startY;

    public SerializablePath() {
        super();
        pathPoints = new ArrayList<>();
    }


    public void addPathPoints(List<int[]> points) {
//        pathPoints.addAll(points);
        for (int[] i : points) {
            pathPoints.add(new Point(i[0], i[1]));
        }
        loadPathPointsAsQuadTo();
    }

    public void loadPathPointsAsQuadTo() {
        super.reset();
        Point initPoints = pathPoints.get(0);
        moveTo(initPoints.x, initPoints.y);
        startX = initPoints.x;
        startY = initPoints.y;
        for (Point pointSet : pathPoints) {
            quadTo(startX, startY, pointSet.x, pointSet.y);
            startX = pointSet.x;
            startY = pointSet.y;
        }
    }

    public void addPathPoint(Point point) {
        pathPoints.add(point);
        moveTo(point.x, point.y);
        startX = point.x;
        startY = point.y;
    }

    public void addQuadPathPoint(Point point) {
        pathPoints.add(point);
        quadTo(startX, startY, point.x, point.y);
        startX = point.x;
        startY = point.y;
    }

    public List<Point> getPathPoints() {
        return pathPoints;
    }

    public List<int[]> getPathArrayPoints() {
        List<int[]> l = new ArrayList<>();
        for (Point p : pathPoints) {
            l.add(new int[]{p.x, p.y});
        }
        return l;
    }
}