package cn.scooper.com.easylib.ui.recorder;

import android.os.Environment;

/**
 * Created by zhaoboliang on 15/3/28.
 */
public class CameraRecoderConstant {
    //前置id
    public static final int FRONT_CAMERA = 0;
    //后置摄像头id
    public static final int BACK_CAMERA = 1;
    //Activity传递intent的参数名
    public static final String CAMERA_TYPE = "camera_type";
    public static final String RESOLUTION_W = "resolution_w";
    public static final String RESOLUTION_H = "resolution_h";
    public static final String MAX_DURATION = "max_duration";
    public static final String PATH_DIR = "path_dir";
    public static final String PATH_FILE = "path_file";
    public static final String ENCODING_BITRATE = "encoding_bitrate";
    public static final String MIC_INUSE = "mic_inuse";
    //Activity传递intent的参数默认值
    public static final int RESOLUTION_W_DEFAULT_VALUE = 640;
    public static final int RESOLUTION_H_DEFAULT_VALUE = 480;
    public static final int MAX_DURATION_DEFAULT_VALUE = 0;
    public static final int ENCODING_BITRATE_DEFAULT_VALUE = 2000 * 1024 / 2;
    public static final int RESULT_OK = 1;
    public static final int RESULT_ERROR = 0;

    public static final String STORE_DIR_PATH_DEFAULT_VALUE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Video_sip/";
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String GO_ROOT = "goRoot";
    public static final String GO_PARENT = "goParent";

    /**
     * 拍摄时长显示格式化（其实就是一个不满两位加0操作）
     *
     * @param i 时间参数
     * @return 返回格式化后的值
     */
    public static String format(int i) {
        String s = i + "";
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * Activity开启文件选择dialog后，选择listItem回调函数，设置路径
     */
    public interface FileCallBack {
        public void setFileDir(String s);

    }
}