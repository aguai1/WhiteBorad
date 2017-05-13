package cn.scooper.com.whiteboard.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import cn.scooper.com.whiteboard.db.DBHelper;
import cn.scooper.com.whiteboard.db.domain.ShapeBean;

/**
 * Created by Aguai on 2016/11/20.
 * 会议Shape数据库处理逻辑
 */

public class ShapeDao {
    private Dao<ShapeBean, Integer> mDao;

    public ShapeDao(Context context) {
        try {
            DBHelper mHelper = new DBHelper(context);
            mDao = mHelper.getDao(ShapeBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ShapeBean add(ShapeBean userBean) {
        try {
            if (mDao != null && userBean != null) {
                mDao.createOrUpdate(userBean);
                return userBean;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新一条记录
     *
     * @param userBean 需要更新的friend
     */
    public void update(ShapeBean userBean) {
        try {
            if (mDao != null && userBean != null) {
                mDao.update(userBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ShapeBean> findAllByPageId(final String pageId) {
        try {
            if (mDao != null) {
                QueryBuilder<ShapeBean, Integer> builder = mDao.queryBuilder();
                //倒序查询
                builder.orderBy("id", true);
                builder.where().eq("pageId", pageId);
                List<ShapeBean> query = builder.query();
                return query;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ShapeBean> findAllByMeetingPageId(String meetingid, String pageId) {
        try {
            if (mDao != null) {
                QueryBuilder<ShapeBean, Integer> builder = mDao.queryBuilder();
                //倒序查询
                builder.orderBy("id", true);
                builder.where().eq("meetingId", meetingid).and().eq("pageId", pageId);
                List<ShapeBean> query = builder.query();
                return query;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除一条数据
     *
     * @param shapeBean 需要删除的数据
     */
    public void delete(ShapeBean shapeBean) {
        try {
            if (mDao != null && shapeBean != null) {
                mDao.delete(shapeBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除
     *
     * @param meetingId
     */
    public void deleteAllByMeetingId(String meetingId) {
        try {
            mDao.executeRaw("delete from ShapeBean where meetingId = '" + meetingId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除会议所有数据
     *
     * @param shapeId
     */
    public void deleteAllShapeId(String shapeId) {
        try {
            mDao.executeRaw("delete from ShapeBean where serviceShapeId = '" + shapeId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public ShapeBean findByServiceId(String meetingid, String pageId, String serviceId) {
        try {
            if (mDao != null) {
                QueryBuilder<ShapeBean, Integer> builder = mDao.queryBuilder();
                //倒序查询
                builder.orderBy("id", true);
                builder.where().eq("meetingId", meetingid).and().eq("pageId", pageId).and().eq("serviceShapeId", serviceId);
                return builder.queryForFirst();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllByPageId(String meetingId, String pageId) {
        try {
            mDao.executeRaw("delete from ShapeBean where pageId = '" + pageId + "' and meetingId = '" + meetingId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ShapeBean> findAllByMeetingId(String meetingId) {
        try {
            if (mDao != null) {
                QueryBuilder<ShapeBean, Integer> builder = mDao.queryBuilder();
                //倒序查询
                builder.orderBy("id", true);
                builder.where().eq("meetingId", meetingId);
                List<ShapeBean> query = builder.query();
                return query;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ShapeBean> findAllByMeetingInstandOfPageId(String meetingId, String currentPage) {
        try {
            if (mDao != null) {
                QueryBuilder<ShapeBean, Integer> builder = mDao.queryBuilder();
                //倒序查询
                builder.orderBy("id", true);
                builder.where().eq("meetingId", meetingId).and().ne("pageId", currentPage);
                List<ShapeBean> query = builder.query();
                return query;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
