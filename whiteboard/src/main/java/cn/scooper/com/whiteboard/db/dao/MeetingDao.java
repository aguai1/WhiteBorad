package cn.scooper.com.whiteboard.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import cn.scooper.com.whiteboard.db.DBHelper;
import cn.scooper.com.whiteboard.db.domain.MeetingBean;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Aguai on 2016/11/20.
 * 会议信息数据库处理逻辑
 */
public class MeetingDao {
    private Dao<MeetingBean, Integer> mDao;

    public MeetingDao(Context context) {
        try {
            DBHelper mHelper = new DBHelper(context);
            mDao = mHelper.getDao(MeetingBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MeetingBean add(MeetingBean meeting) {
        try {
            if (mDao != null && meeting != null) {
                mDao.createOrUpdate(meeting);
                return meeting;
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
    public void update(MeetingBean userBean) {
        try {
            if (mDao != null && userBean != null) {
                mDao.update(userBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Observable<List<MeetingBean>> findAll() {
        return Observable.create(new Observable.OnSubscribe<List<MeetingBean>>() {
            @Override
            public void call(Subscriber<? super List<MeetingBean>> subscriber) {
                try {
                    if (mDao != null) {
                        QueryBuilder<MeetingBean, Integer> builder = mDao.queryBuilder();
                        //倒序查询
                        builder.orderBy("id", false);
                        List<MeetingBean> beans = builder.query();
                        if (beans != null) {
                            subscriber.onNext(beans);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(new IOException("获取失败"));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public MeetingBean findByMeetingId(final String meetingId) {

        if (mDao != null) {

            try {
                QueryBuilder<MeetingBean, Integer> builder = mDao.queryBuilder();
                //倒序查询
                builder.orderBy("id", false);
                builder.where().eq("serviceMeetingId", meetingId);
                return builder.queryForFirst();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        }
        return null;
    }


    public void deleteAllByMeetingId(String serviceMeetingId) {
        try {
            mDao.executeRaw("delete from MeetingBean where serviceMeetingId = '" + serviceMeetingId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
