package cn.scooper.com.whiteboard.db.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "PageBean")
public class PageBean {
    /**
     * id
     */
    @DatabaseField(generatedId = true)
    protected int id;
    /**
     * 远程会议id
     */
    @DatabaseField
    protected String meetingId;

    /**
     * 远程id
     */
    @DatabaseField
    protected String servicePageId;

    /**
     * 页码
     */
    @DatabaseField
    protected int pageNum;

    /**
     * 页面宽度
     */
    @DatabaseField
    protected int pageWidth;

    /**
     * 页面高度
     */
    @DatabaseField
    protected int pageHeight;
    /**
     * name
     */
    @DatabaseField
    protected String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServicePageId() {
        return servicePageId;
    }

    public void setServicePageId(String servicePageId) {
        this.servicePageId = servicePageId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}