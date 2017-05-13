package cn.scooper.com.whiteboard.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import cn.scooper.com.whiteboard.db.DBHelper;
import cn.scooper.com.whiteboard.db.domain.UserBean;


public class UserDao {
    private Dao<UserBean, Integer> mDao;

    public UserDao(Context context) {
        try {
            DBHelper mHelper = new DBHelper(context);
            mDao = mHelper.getDao(UserBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add(UserBean userBean) {
        try {
            if (mDao != null && userBean != null) {
                mDao.createOrUpdate(userBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新一条记录
     *
     * @param userBean 需要更新的friend
     */
    public void update(UserBean userBean) {
        try {
            if (mDao != null && userBean != null) {
                mDao.update(userBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一条数据
     *
     * @param friend 需要删除的数据
     */
    public void delete(UserBean friend) {
        try {
            if (mDao != null && friend != null) {
                mDao.delete(friend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
