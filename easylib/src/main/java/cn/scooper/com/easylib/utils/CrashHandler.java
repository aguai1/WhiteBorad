package cn.scooper.com.easylib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 程序崩溃异常处理
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
 * Created by jiangwj on 2014/11/25.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getCanonicalName();

    private static CrashHandler _instance = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();
    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static CrashHandler getInstance() {
        return _instance;
    }

    /**
     * 获取捕捉到的异常的字符串
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    public void init(Context context) {
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * The thread is being terminated by an uncaught exception. Further
     * exceptions thrown in this method are prevent the remainder of the
     * method from executing, but are otherwise ignored.
     *
     * @param thread the thread that has an uncaught exception
     * @param ex     the exception that was thrown
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }

            // CameraRecoderConstant程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) return false;
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉，程序出现异常。", Toast.LENGTH_LONG)
                        .show();
                Looper.loop();
            }
        }.start();

        // 收集设备参数信息
        collectDeviceInfo(mContext);

        // 保存日志文件
        String str = saveCrashInfo2File(ex);
        Log.e(TAG, "dump : " + str);

        return false;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "<null>"
                        : pi.versionName;
                String versionCode = String.valueOf(pi.versionCode);
                infos.put(versionName, versionName);
                infos.put(versionCode, versionCode);
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    private void collectApplicationInfo(StringBuilder sb, Context ctx) {
        sb.append("----------------------------------------------\r\n");
        try {
            ApplicationInfo ai = ctx.getApplicationInfo();
            sb.append("packageName      : ").append(ai.packageName).append("\r\n");
            sb.append("className        : ").append(ai.className).append("\r\n");
            sb.append("processName      : ").append(ai.processName).append("\r\n");
            sb.append("taskAffinity     : ").append(ai.taskAffinity).append("\r\n");
            sb.append("name             : ").append(ai.name).append("\r\n");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                sb.append("nativeLibraryDir : ").append(ai.nativeLibraryDir).append("\r\n");
            }
            sb.append("dataDir          : ").append(ai.dataDir).append("\r\n");
            sb.append("sourceDir        : ").append(ai.sourceDir).append("\r\n");
            //
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ai.packageName, 0);
            sb.append("versionName      : ").append(pi.versionName).append("\r\n");
        } catch (Exception ex) {
            Log.e(TAG, "an error occured when collect application info", ex);
        }
        sb.append("----------------------------------------------\r\n");
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuilder sb = new StringBuilder();

        // collect application info
        collectApplicationInfo(sb, mContext);

        //
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append("[").append(key).append(", ").append(value).append("]\r\n");
        }

        sb.append(getStackTraceString(ex));

        try {
            String time = formatter.format(new Date());

            TelephonyManager mTelephonyMgr = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = mTelephonyMgr.getDeviceId();
            if (TextUtils.isEmpty(imei)) {
                imei = "unknown_imei";
            }

            String fileName = "CRS_" + time + "_" + imei + ".txt";

            File sdDir = null;

            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                sdDir = Environment.getExternalStorageDirectory();
            }
            if (sdDir == null) {
                return null;
            }

            File cacheDir = new File(sdDir + File.separator + "scooper");
            if (!cacheDir.exists()) {
                if (!cacheDir.mkdirs()) return null;
            }

            File filePath = new File(cacheDir + File.separator + fileName);

            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(sb.toString().getBytes());
            fos.close();

            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }
}
