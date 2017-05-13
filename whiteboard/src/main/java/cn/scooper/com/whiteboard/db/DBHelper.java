package cn.scooper.com.whiteboard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import cn.scooper.com.whiteboard.db.domain.MeetingBean;
import cn.scooper.com.whiteboard.db.domain.PageBean;
import cn.scooper.com.whiteboard.db.domain.ShapeBean;

public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "_DB_Scopper";

    private DatabaseConnection mConn;

    private Map<String, Dao> mDaoMap = new HashMap<>();

    public DBHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    public DBHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            mConn = connectionSource.getSpecialConnection();
            TableUtils.createTable(connectionSource, ShapeBean.class);
            TableUtils.createTable(connectionSource, MeetingBean.class);
            TableUtils.createTable(connectionSource, PageBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库更新
     * 1.0  UserBean创建
     * 历史版本数据库更新日志
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

    }

    @SuppressWarnings("unchecked")
    public Dao getDao(Class clazz) throws SQLException {
        String clazzName = clazz.getSimpleName();
        Dao dao;
        if (!mDaoMap.containsKey(clazzName)) {
            dao = super.getDao(clazz);
            mDaoMap.put(clazzName, dao);
        } else {
            dao = mDaoMap.get(clazzName);
        }

        return dao;
    }

    @Override
    public void close() {
        super.close();
        for (String key : mDaoMap.keySet()) {
            Dao dao = mDaoMap.get(key);
            dao = null;
        }
        mDaoMap.clear();
    }
}
