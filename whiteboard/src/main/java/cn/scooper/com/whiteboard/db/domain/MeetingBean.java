package cn.scooper.com.whiteboard.db.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "MeetingBean")
public class MeetingBean {
    /**
     * id
     */
    @DatabaseField(generatedId = true)
    protected int id;

    /**
     * name
     */
    @DatabaseField
    protected String name;
    /**
     * 远程id
     */
    @DatabaseField
    protected String serviceMeetingId;
    /**
     * type
     */
    @DatabaseField
    protected String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceMeetingId() {
        return serviceMeetingId;
    }

    public void setServiceMeetingId(String serviceMeetingId) {
        this.serviceMeetingId = serviceMeetingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}