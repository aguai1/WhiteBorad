package cn.scooper.com.whiteboard.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import cn.scooper.com.whiteboard.db.DBHelper;
import cn.scooper.com.whiteboard.db.domain.PageBean;

/**
 * Created by Aguai on 2016/11/20.
 * 会议page数据库处理逻辑
 */
public class PageDao {
    private Dao<PageBean, Integer> mDao;

    public PageDao(Context context) {
        try {
            DBHelper mHelper = new DBHelper(context);
            mDao = mHelper.getDao(PageBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PageBean add(PageBean userBean) {
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
     */
    public void update(PageBean userBean) {
        try {
            if (mDao != null && userBean != null) {
                mDao.update(userBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PageBean> findAll() {
        try {
            if (mDao != null) {
                QueryBuilder<PageBean, Integer> builder = mDao.queryBuilder();
                //倒序查询
                builder.orderBy("id", false);
                return builder.query();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PageBean> findAllByMeetingId(final String meetingId) {
        try {
            if (mDao != null) {
                QueryBuilder<PageBean, Integer> builder = mDao.queryBuilder();
                //倒序查询
                builder.orderBy("id", true);
                builder.where().eq("meetingId", meetingId);
                List<PageBean> beans = builder.query();
                return beans;
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
    public void delete(PageBean shapeBean) {
        try {
            if (mDao != null && shapeBean != null) {
                mDao.delete(shapeBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PageBean findByMeetingIdAndPageNum(String id, String servicePageId) {
        try {
            if (mDao != null) {
                QueryBuilder<PageBean, Integer> builder = mDao.queryBuilder();
                //倒序查询
                builder.orderBy("id", false);
                builder.where().eq("meetingId", id).and().eq("servicePageId", servicePageId);
                return builder.queryForFirst();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void deleteAllByMeetingId(String meetingId) {
        try {
            mDao.executeRaw("delete from PageBean where meetingId = '" + meetingId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
