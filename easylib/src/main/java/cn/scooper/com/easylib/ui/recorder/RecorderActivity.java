package cn.scooper.com.easylib.ui.recorder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.scooper.com.easylib.R;
import cn.scooper.com.easylib.ui.BaseActivity;
import cn.scooper.com.easylib.utils.PermissionsUtil;
import cn.scooper.com.easylib.utils.ToastUtils;

import static cn.scooper.com.easylib.utils.PermissionsUtil.TAKE_PHOTO_REQUEST_CODE;

public class RecorderActivity extends BaseActivity {

    private Button mVideoStartBtn;
    private ImageView mVideoSwitchImg;
    private LinearLayout mVideoSwitchFlashLL;
    private TextView timerTV;
    private TextView mVideoFlashStatusTV;
    private FrameLayout mVideoFrame;

    private Context mContext;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    //isRecording为true 开始录制并且计时开始； isPreview 相机为预览状态为true；isFlashing 闪光灯打开常亮为 true
    private boolean isRecording = false;
    private boolean isPreview = true;
    private boolean isFlashing = false;
    //计时相关
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    //用户设定的录制时长 0则是没有设定
    private int mMaxDuration = 0;
    //相机类型 前置后置
    private int mCameraType;
    //用户传进来的分辨率
    private int mResolutionHeight;
    private int mResolutionWidth;
    private int mEncodingBitRate = 0;

    private boolean isMicInUse = false;

    //存储的文件夹
    private File mVideoDirs;
    private File mRecVideoFile;
    private String mRecVideoDirPath;
    private String mRecVideoFilePath;

    private CameraManager mCameraManager;
    private MediaRecorderManager mMediaRecorderManager;
    //上个activity的返回码
    private int mResultCode = Activity.RESULT_CANCELED;
    private Handler handler = new Handler();
    /**
     * task 用来计时
     */
    private Runnable task = new Runnable() {
        public void run() {
            if (isRecording) {
                handler.postDelayed(this, 1000);
                second++;
                if (second >= 60) {
                    minute++;
                    second = second % 60;
                }
                if (minute >= 60) {
                    hour++;
                    minute = minute % 60;
                }
                if (hour == 0) {
                    timerTV.setText(CameraRecoderConstant.format(minute) + ":"
                            + CameraRecoderConstant.format(second));
                } else {
                    timerTV.setText(CameraRecoderConstant.format(hour) + ":" + CameraRecoderConstant.format(minute) + ":"
                            + CameraRecoderConstant.format(second));
                }
                //时间大于0 小于60分钟
                if (mMaxDuration > 0 && mMaxDuration <= 60 && mMaxDuration == second) {
                    onStartStopRecord();
                    //时间大于60分钟
                } else if (mMaxDuration > 60 && mMaxDuration / 60 == minute && second == mMaxDuration % 60) {
                    onStartStopRecord();
                }
            }
        }
    };

    @Override
    public int bindLayout() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        return R.layout.activity_recorder;
    }

    @Override
    public boolean translucentStatus() {
        return false;
    }

    @Override
    public void initView(View view) {
        mContext = this;
        mVideoStartBtn = (Button) findViewById(R.id.start_stop_recording);
        mVideoSwitchImg = (ImageView) findViewById(R.id.video_switch);
        mVideoSwitchFlashLL = (LinearLayout) findViewById(R.id.switch_layout);
        timerTV = (TextView) findViewById(R.id.video_timer);
        mVideoFlashStatusTV = (TextView) findViewById(R.id.video_flash_status);
        mVideoFrame = (FrameLayout) findViewById(R.id.video_frame);
    }

    @Override
    public void doBusiness(Context mContext) {
        getData();
        if (!checkPermission())
            return;
        initCamera();
    }

    private void initCamera() {
        //获取代理
        mCameraManager = CameraManager.getCameraManagerInstance(mContext);
        mMediaRecorderManager = MediaRecorderManager.getMediaRecorderManagerInstance(mContext);

        mSurfaceView = new SurfaceView(this);
        //找出最合适的分辨率
        findRightResolution();
        //计算并设置预览窗口大小
        setSurfaceViewSize();
        // 绑定预览视图
        SurfaceHolder holder = mSurfaceView.getHolder();
        // 设置分辨率
        holder.setFixedSize(mResolutionWidth, mResolutionHeight);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                if (mCameraManager.getCurrentCamera() != null) {
                    if (isPreview) {
                        mCameraManager.stopCameraPreview();
                        isPreview = false;
                    }
                    mCameraManager.releaseCamera();
                    mCameraManager.destroyCamera();
                }
                mSurfaceView = null;
                mSurfaceHolder = null;
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mCameraManager.startPreview(mCameraType, holder, mResolutionWidth, mResolutionHeight)) {
                    isPreview = true;
                    if (mSurfaceView != null) {
                        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (mCameraManager.isAutoFocus()) {
                                    if (mSurfaceView != null && mCameraManager.getCurrentCamera() != null) {
                                        try {
                                            mCameraManager.getCurrentCamera().autoFocus(mCameraManager.getAutoFocusCallback());
                                        } catch (Exception e) {

                                        }
                                    }
                                }
                                return false;
                            }
                        });
                    }
                }
                mSurfaceHolder = holder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                mSurfaceHolder = holder;
                //设置自动对焦
//                mCameraManager.getCurrentCamera().autoFocus(new Camera.AutoFocusCallback() {
//                    @Override
//                    public void onAutoFocus(boolean success, Camera camera) {
//                        if (success) {
//                            mCameraManager.startPreview(mCameraType,mSurfaceHolder,
//                                    mResolutionWidth,mResolutionHeight);
//                        }
//                    }
//                });
            }
        });
        //holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 这个方法已被弃用
        mVideoStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartStopRecord();
            }
        });
        //闪关灯按钮
        mVideoSwitchFlashLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSwitchFlash();
            }
        });
        mVideoSwitchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitchCamera();
            }
        });
        //如果只有一个摄像头  不能转换
        if (mCameraManager.getCameraCount() == 1) {
            mVideoSwitchImg.setVisibility(View.GONE);
        }
    }

    private void getData() {
        Intent intent = getIntent();
        mCameraType = intent.getIntExtra(CameraRecoderConstant.CAMERA_TYPE, CameraRecoderConstant.BACK_CAMERA);
        mResolutionHeight = intent.getIntExtra(CameraRecoderConstant.RESOLUTION_H, CameraRecoderConstant.RESOLUTION_H_DEFAULT_VALUE);
        mResolutionWidth = intent.getIntExtra(CameraRecoderConstant.RESOLUTION_W, CameraRecoderConstant.RESOLUTION_W_DEFAULT_VALUE);
        mMaxDuration = intent.getIntExtra(CameraRecoderConstant.MAX_DURATION, CameraRecoderConstant.MAX_DURATION_DEFAULT_VALUE);
        mRecVideoDirPath = intent.getStringExtra(CameraRecoderConstant.PATH_DIR);
        mEncodingBitRate = intent.getIntExtra(CameraRecoderConstant.ENCODING_BITRATE, CameraRecoderConstant.ENCODING_BITRATE_DEFAULT_VALUE);
        isMicInUse = intent.getBooleanExtra(CameraRecoderConstant.MIC_INUSE, false);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.CAMERA)) {
                PermissionsUtil.showDialog("External storage", this,
                        Manifest.permission.CAMERA, TAKE_PHOTO_REQUEST_CODE);

            } else {
                PermissionsUtil.getCameraPermissions(this);
            }
            return false;
        }
        return true;
    }

    //    权限监听
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case TAKE_PHOTO_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCamera();
                } else {
                    ToastUtils.showShort("无法获得照相机权限");
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    /**
     * 覆写返回键监听
     */
    @Override
    public void onBackPressed() {
        if (mMediaRecorderManager.getCurrentMediaRecorder() != null) {
            //mVideoStopBtn.performClick();
        }
        finish();
    }

    /**
     * 转换摄像头
     */
    private void onSwitchCamera() {
        if (isRecording) {
            ToastUtils.showShort("拍摄过程中不能进行摄像头切换");
        } else {
            mCameraType = mCameraManager.switchCamera(mCameraType, mSurfaceHolder, mResolutionWidth, mResolutionHeight);
            //重置闪光灯
            isFlashing = false;
        }
    }

    /**
     * 开始停止
     */
    private void onStartStopRecord() {
        if (!isRecording) {
            mVideoStartBtn.setText("停止");
            if (mCameraManager.getCurrentCamera() != null) {
                if (isPreview) {
                    isPreview = false;
                    mCameraManager.stopCameraPreview();
                    //解锁camera让另一个线程可以使用,虽然4.0以上系统会帮你unlock()，
                    //但是系统会让另一个线程只能默认打开后置摄像头，前置摄像头怎么办呢？所以要加上这里还是自己要调用unlock
                    mCameraManager.unlockCamera();
                }
            }
            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
            String format = s.format(new Date());
            mVideoDirs = new File(mRecVideoDirPath);
            if (!mVideoDirs.exists()) {
                mVideoDirs.mkdirs();
            }
            mRecVideoFile = new File(mVideoDirs, "Video" + format + ".mp4");
            mRecVideoFilePath = mRecVideoFile.getAbsolutePath();
//            mRecVideoFilePath = mRecVideoDirPath + "/Video" + format + ".mp4";
            if (mMediaRecorderManager.prepareRecorder(mCameraManager.getCurrentCamera(), mCameraType
                    , mSurfaceHolder, mResolutionWidth, mResolutionHeight, mRecVideoFilePath, mEncodingBitRate, isMicInUse)) {
                second = 0;
                minute = 0;
                hour = 0;
                isRecording = true;
            }

            timerTV.setVisibility(View.VISIBLE);
            handler.postDelayed(task, 1000);
            if (mMediaRecorderManager.startRecord()) {
                ToastUtils.showShort("开始录制");
            }
        } else {
            if (mMediaRecorderManager.stopRecord()) {
                isRecording = false;
                timerTV.setText(CameraRecoderConstant.format(minute) + ":"
                        + CameraRecoderConstant.format(second));
                mResultCode = Activity.RESULT_OK;
                ToastUtils.showShort("录制完成，已保存");
            }
            Intent data = new Intent();
            data.setData(Uri.fromFile(mRecVideoFile));
            data.putExtra(CameraRecoderConstant.PATH_FILE, mRecVideoFilePath);
            RecorderActivity.this.setResult(mResultCode, data);
            mCameraManager.releaseCamera();
            RecorderActivity.this.finish();
        }
    }

    /**
     * 闪光等开关
     */
    private void onSwitchFlash() {
        Camera.Parameters parameters = mCameraManager.getCurrentCamera().getParameters();
        if (parameters.getFlashMode() == null || parameters.getFlashMode().equals("null")) {
            ToastUtils.showShort("没有闪关灯");
        } else {
            if (isFlashing) {
                mVideoFlashStatusTV.setText("关闭");
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);//关闭
                mCameraManager.getCurrentCamera().setParameters(parameters);
                isFlashing = false;
                ToastUtils.showShort("关闭闪关灯");

            } else {

                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//开启长亮
                mCameraManager.getCurrentCamera().setParameters(parameters);
                isFlashing = true;
                mVideoFlashStatusTV.setText("打开");
                ToastUtils.showShort("打开闪关灯");
            }
        }
    }

    /**
     * 计算预览大小
     */
    private void setSurfaceViewSize() {
        double wVideo = mResolutionWidth;
        double hVideo = mResolutionHeight;
        // 获取屏幕尺寸
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            this.getWindowManager().getDefaultDisplay().getSize(size);
        } else {
            size.x = this.getWindowManager().getDefaultDisplay().getWidth();
            size.y = this.getWindowManager().getDefaultDisplay().getHeight();
        }
        double hLimit = size.x;
        double wLimit = size.y;
        //
        double r = hLimit / hVideo;
        int w = (int) hLimit;
        int h = (int) (wVideo * r);

        if (w != 0 && h != 0) {
            Log.v("SNAPSHOT", "Using" + w + "x" + h);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams((int) wLimit, (int) hLimit);
            p.addRule(RelativeLayout.CENTER_IN_PARENT);
            p = new RelativeLayout.LayoutParams(w, h);
            p.addRule(RelativeLayout.CENTER_IN_PARENT);
            mVideoFrame.setLayoutParams(p);
            ViewParent viewParent = mSurfaceView.getParent();
            if (viewParent != null && viewParent instanceof ViewGroup) {
                ((ViewGroup) (viewParent)).removeView(mSurfaceView);
            }
            mVideoFrame.addView(mSurfaceView);
            (findViewById(R.id.video_bottom)).bringToFront();
            (findViewById(R.id.video_top)).bringToFront();
        }
    }

    /**
     * 找出合适的分辨率
     */
    private void findRightResolution() {
        List<Camera.Size> list;
        boolean isPositive = true;
        if (mCameraType == CameraRecoderConstant.FRONT_CAMERA) {
            list = mCameraManager.getFrontCameraSizeList();
        } else {
            list = mCameraManager.getBackCameraSizeList();
        }
        if (list != null && list.size() > 1) {
            //这个list的顺序和手机品牌有关，所以判断返回的list是正序还是逆序，从而选出最合适的分辨率
            if (list.get(0).width > list.get(1).width) {
                isPositive = false;
            } else {
                isPositive = true;
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).width == mResolutionWidth && list.get(i).height == mResolutionHeight) {
                    return;
                } else if ((isPositive && list.get(i).width >= mResolutionWidth && i > 0)
                        || (!isPositive && list.get(i).width <= mResolutionWidth && i > 0)) {
                    if (Math.abs(list.get(i).width - mResolutionWidth) > Math.abs(mResolutionWidth - list.get(i - 1).width)) {
                        mResolutionWidth = list.get(i - 1).width;
                        mResolutionHeight = list.get(i - 1).height;
                    } else {
                        mResolutionWidth = list.get(i).width;
                        mResolutionHeight = list.get(i).height;
                    }
                    return;
                } else if (i == list.size() - 1) {
                    mResolutionWidth = list.get(i).width;
                    mResolutionHeight = list.get(i).height;
                    return;
                }
            }
        } else if (list != null && list.size() == 1) {
            mResolutionWidth = list.get(0).width;
            mResolutionHeight = list.get(0).height;
            return;
        }
    }


}