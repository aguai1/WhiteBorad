package cn.scooper.com.easylib.ui.recorder;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2015/4/2.
 */

public class MediaRecorderManager {
    //camera代理
    private static MediaRecorderManager mMediaRecorderManager;
    //camera实例
    private MediaRecorder mMediaRecorder;
    private Context mContext;

    /**
     * 私有构造函数
     *
     * @param c 上下文
     */
    private MediaRecorderManager(Context c) {
        mContext = c;
    }

    /**
     * 获取manager实例
     *
     * @param c 上下文
     * @return manager实例
     */
    public static MediaRecorderManager getMediaRecorderManagerInstance(Context c) {
        if (mMediaRecorderManager == null) {
            mMediaRecorderManager = new MediaRecorderManager(c);
        }
        return mMediaRecorderManager;
    }

    /**
     * 获取mediaRecorder
     *
     * @return
     */
    private MediaRecorder getMediaRecorder() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        } else {
            mMediaRecorder.reset();
        }
        return mMediaRecorder;
    }

    /**
     * 获取当前摄像头
     *
     * @return
     */
    public MediaRecorder getCurrentMediaRecorder() {
        return mMediaRecorder;
    }

    /**
     * prepare参数设置
     *
     * @param camera   摄像头实例
     * @param type     camera 类型（前置，后置）
     * @param holder   surfaceHolder
     * @param width    宽
     * @param height   高
     * @param filepath 文件路径
     * @return 返回prepare是否成功
     */
    public boolean prepareRecorder(Camera camera, int type, SurfaceHolder holder, int width, int height, String filepath, int bitrate, boolean isMicInUse) {
        if (mMediaRecorder == null) {
            mMediaRecorder = getMediaRecorder();
        }
        mMediaRecorder.setCamera(camera);

        mMediaRecorder.setPreviewDisplay(holder
                .getSurface());
        mMediaRecorder
                .setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (!isMicInUse) {
            mMediaRecorder
                    .setAudioSource(MediaRecorder.AudioSource.MIC);

        }
        mMediaRecorder
                .setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder
                .setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        if (!isMicInUse) {
            mMediaRecorder
                    .setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        }


        mMediaRecorder.setVideoSize(width, height);
        //设置帧率
        mMediaRecorder.setVideoFrameRate(30);
        //解决花屏的问题
        mMediaRecorder.setVideoEncodingBitRate(bitrate);
        //区分前置后置摄像头 成像反转

        if (!isScreenLandScape()) {
            if (type == CameraRecoderConstant.FRONT_CAMERA) {
                mMediaRecorder.setOrientationHint(270);
            } else {
                mMediaRecorder.setOrientationHint(90);
            }
        }
        //mMediaRecorder.setOutputFile(filepath.substring(7));
        mMediaRecorder.setOutputFile(new File(filepath).getAbsolutePath());
        boolean ret = false;
        try {
            mMediaRecorder.prepare();
            ret = true;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "" + e.getStackTrace().toString() + "  " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return ret;
    }

    public boolean isScreenLandScape() {

        Configuration mConfiguration = mContext.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向

        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {

//横屏
            return true;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {

//竖屏
            return false;
        }
        return false;
    }


    /**
     * 开始录制，一般在prepare之后调用
     *
     * @return
     */
    public boolean startRecord() {
        try {
            mMediaRecorder.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 停止录制，释放mediaRecorder
     *
     * @return 停止成功返回true 失败返回false
     */
    public boolean stopRecord() {
        try {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
