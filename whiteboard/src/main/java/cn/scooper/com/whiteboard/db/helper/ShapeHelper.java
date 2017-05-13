package cn.scooper.com.whiteboard.db.helper;

import android.content.Context;

import java.util.List;

import cn.scooper.com.whiteboard.db.dao.MeetingDao;
import cn.scooper.com.whiteboard.db.dao.PageDao;
import cn.scooper.com.whiteboard.db.dao.ShapeDao;
import cn.scooper.com.whiteboard.db.domain.MeetingBean;
import cn.scooper.com.whiteboard.db.domain.PageBean;
import cn.scooper.com.whiteboard.db.domain.ShapeBean;

/**
 * Created by Aguai on 2016/11/20.
 * 数据库查询封装类
 */
public class ShapeHelper {
    private final Context context;
    private ShapeDao shapeDao;
    private MeetingDao meetingDao;
    private PageDao pageDao;

    public ShapeHelper(Context applicationContext) {
        this.context = applicationContext;
        initDao();
    }

    private void initDao() {
        shapeDao = new ShapeDao(context);
        meetingDao = new MeetingDao(context);
        pageDao = new PageDao(context);
    }

    /**
     * 插入会议，已经存在则返回
     *
     * @param name Meeting name
     * @param id   Meeting远程id
     * @return
     */
    public MeetingBean insetMeeting(String name, String id) {
        MeetingBean byMeetingId = meetingDao.findByMeetingId(id);
        if (byMeetingId != null) {
            return byMeetingId;
        }
        MeetingBean meeting = new MeetingBean();
        meeting.setName(name);
        meeting.setServiceMeetingId(id);
        return meetingDao.add(meeting);
    }

    /**
     * 插入page 如果存在则返回
     *
     * @param meetingId 远程会议id
     * @param pageId    远程pagenum
     * @return
     */
    public PageBean insetPage(String meetingId, String pageId) {
        PageBean byMeetingIdAndPageNum = pageDao.findByMeetingIdAndPageNum(meetingId, pageId);
        if (byMeetingIdAndPageNum != null) {
            return byMeetingIdAndPageNum;
        }
        PageBean page = new PageBean();
        page.setMeetingId(meetingId);
        page.setServicePageId(pageId);
        return pageDao.add(page);
    }

    /**
     * 插入Shape
     * 默认处理插入会议插入页面逻辑
     */
    public void insetShape(String meetingId, String pageId, ShapeBean shape) {
        insetMeeting("test", meetingId);
        insetPage(meetingId, pageId);
        shape.setPageId(pageId);
        shape.setMeetingId(meetingId);
        shapeDao.add(shape);
    }

    /**
     * 删除Shape
     */
    public void deleteShape(String id) {
        shapeDao.deleteAllShapeId(id);
    }

    /***
     * 获取会议该page的所有shapeBean
     *
     * @param meetingid
     * @return
     */
    public List<ShapeBean> getShapesByMeetingPage(String meetingid, String pageId) {
        return shapeDao.findAllByMeetingPageId(meetingid, pageId);
    }

    /**
     * 该会议除了该pageId的所有shapbean
     *
     * @param meetingId
     * @param currentPage
     * @return
     */
    public List<ShapeBean> getAllShapesByMeetingIdInstandOfPageId(String meetingId, String currentPage) {
        return shapeDao.findAllByMeetingInstandOfPageId(meetingId, currentPage);
    }

    /**
     * 获取所有page
     */
    public List<PageBean> getAllShapesPageByMeetingId(String id) {
        return pageDao.findAllByMeetingId(id);
    }


    /**
     * 根据pageid返回shape
     *
     * @param meetingId
     */
    public void clearByMeeting(String meetingId) {
        meetingDao.deleteAllByMeetingId(meetingId);
        pageDao.deleteAllByMeetingId(meetingId);
        shapeDao.deleteAllByMeetingId(meetingId);
    }

    /**
     * 移动shape
     *
     * @param meetingid
     * @param pageId
     * @param serviceId
     * @param offsetX
     * @param offsetY
     */
    public void moveShape(String meetingid, String pageId, String serviceId, int offsetX, int offsetY) {
        ShapeBean s = shapeDao.findByServiceId(meetingid, pageId, serviceId);
        if (s != null) {
            s.setStartX(s.getStartY() + offsetX);
            s.setStartY(s.getStartY() + offsetY);
            s.setEndx(s.getEndx() + offsetX);
            s.setEndy(s.getEndy() + offsetY);
            shapeDao.update(s);
        }
    }

    /**
     * 修改shape bunds
     *
     * @param meetingid
     * @param pageId
     * @param serviceId
     * @param startX
     * @param startY
     * @param endx
     * @param endy
     */
    public void adjustShapeBunds(String meetingid, String pageId, String serviceId, float startX, float startY, float endx, float endy) {
        ShapeBean s = shapeDao.findByServiceId(meetingid, pageId, serviceId);
        if (s != null) {
            s.setStartX(startX);
            s.setStartY(startY);
            s.setEndx(endx);
            s.setEndy(endy);
            shapeDao.update(s);
        }
    }

    public void adjustShapeWidth(String meetingId, String meetingPage, String serviceId, int color) {
        ShapeBean s = shapeDao.findByServiceId(meetingId, meetingPage, serviceId);
        if (s != null) {
            s.setColor(color);
            shapeDao.update(s);
        }
    }

    /**
     * 修改shape颜色
     *
     * @param meetingId
     * @param meetingPage
     * @param serviceId
     * @param color
     */
    public void adjustShapeColor(String meetingId, String meetingPage, String serviceId, int color) {
        ShapeBean s = shapeDao.findByServiceId(meetingId, meetingPage, serviceId);
        if (s != null) {
            s.setColor(color);
            shapeDao.update(s);
        }
    }

    public void deleteShapeByPage(String meetingId, String pageId) {
        shapeDao.deleteAllByPageId(meetingId, pageId);
    }


    public ShapeBean getShapesMeetingPageObjId(String meetingId, String pageId, String objId) {
        return shapeDao.findByServiceId(meetingId, pageId, objId);
    }


}
