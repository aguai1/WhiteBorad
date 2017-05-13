package cn.scooper.com.easylib.ui.recorder;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.List;

import cn.scooper.com.easylib.utils.ToastUtils;

/**
 * Created by zhaoboliang on 15/3/27.
 */
public class CameraManager {
    //camera代理
    private static CameraManager mCameraManager;
    public Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                camera.startPreview();
            }
        }
    };
    //camera实例
    private Camera mCamera;
    private Context mContext;
    private boolean isAutoFocus = false;

    /**
     * 私有构造函数
     *
     * @param c context
     */
    private CameraManager(Context c) {
        mContext = c;
    }

    /**
     * 获取cameraManager实例
     *
     * @param c context
     * @return mCameraManager
     */
    public static CameraManager getCameraManagerInstance(Context c) {

        if (mCameraManager == null) {
            mCameraManager = new CameraManager(c);
        }
        return mCameraManager;
    }

    public boolean isAutoFocus() {
        return isAutoFocus;
    }

    public Camera.AutoFocusCallback getAutoFocusCallback() {
        return mAutoFocusCallback;
    }

    public Camera getCamera(int type) {

        if (type == CameraRecoderConstant.FRONT_CAMERA) {
            mCamera = Camera.open(FindFrontCamera());
        } else if (type == CameraRecoderConstant.BACK_CAMERA) {
            mCamera = Camera.open(FindBackCamera());
        }
        return mCamera;
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
     * @return 获取当前相机（前置或后置）
     */
    public Camera getCurrentCamera() {
        return mCamera;
    }

    /**
     * 获取前置摄像头
     *
     * @return 相机id 如果找不到返回-1
     */
    public int FindFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        // get cameras number
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            // get cameraInfo
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    /**
     * 获取后置摄像头
     *
     * @return 相机id 如果找不到返回-1
     */
    public int FindBackCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return camIdx;
            }
        }
        return -1;
    }

    public int getCameraCount() {
        int cameraCount = 0;
        cameraCount = Camera.getNumberOfCameras();
        return cameraCount;
    }

    /**
     * 释放manager管理的camera
     */
    public void releaseCamera() {
        if (mCamera != null) {
            try {
                if (isAutoFocus) {
                    mCamera.cancelAutoFocus();
                }
                mCamera.release();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 一般跟在releaseCamera后释放camera
     */
    public void destroyCamera() {
        mCamera = null;
    }

    /**
     * 开始预览模式
     *
     * @param type          摄像头类别（前置或后置）
     * @param surfaceHolder surfaceView的holder
     * @param height        分辨率（高）
     * @param width         分辨率（宽）
     * @return
     */
    public boolean startPreview(int type, SurfaceHolder surfaceHolder, int width, int height) {
        if (mCamera == null) {
            mCamera = mCameraManager.getCamera(type);
        }
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                // fps设置 这个预览帧数还是不可控的，只能是个区间，并且在小米5.0的系统上设置这个会出错，所以可以去了
                //parameters.setPreviewFpsRange(10000, 25000);
                // 设置输出格式
                //parameters.setPictureFormat(ImageFormat.JPEG);
                // 照片质量
                //parameters.set("jpeg-quality", 85);
                //连续对焦
                try {
                    List<String> focusModes = parameters.getSupportedFocusModes();
                    if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                        mCamera.setParameters(parameters);
                        isAutoFocus = false;
                    } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        mCamera.setParameters(parameters);
                        isAutoFocus = false;
                    } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        mCamera.setParameters(parameters);
                        isAutoFocus = true;
                    } else {
                        isAutoFocus = false;
                    }
                } catch (Exception e) {
                    Log.e("record autoFocus error:", e.getMessage());
                }
                try {
                    parameters.setPreviewSize(width, height);
                } catch (Exception e) {
                    Log.e("setPreviewSize error:", e.getMessage());
                }
                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(surfaceHolder);
                if (isScreenLandScape()) {

                } else {
                    mCamera.setDisplayOrientation(90);
                }
                mCamera.startPreview();
                if (isAutoFocus) {
                    mCamera.autoFocus(mAutoFocusCallback);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return false;
    }

    /**
     * 获取前置摄像头所支持拍摄video的分辨率
     *
     * @return 支持拍摄video分辨率列表
     */
    public List<Camera.Size> getFrontCameraSizeList() {
        //取得前置摄像头
        Camera FrontCamera = getCamera(CameraRecoderConstant.FRONT_CAMERA);
        //获取摄像头参数
        Camera.Parameters parameters = FrontCamera.getParameters();
        List<Camera.Size> supportedVideoSizes = parameters.getSupportedVideoSizes();
        //获取完后记得释放camera
        mCameraManager.releaseCamera();
        mCameraManager.destroyCamera();
        return supportedVideoSizes;
    }

    /**
     * 获取后置摄像头所支持拍摄video的分辨率
     *
     * @return 支持拍摄video分辨率列表
     */
    public List<Camera.Size> getBackCameraSizeList() {
        Camera backCamera = getCamera(CameraRecoderConstant.BACK_CAMERA);
        Camera.Parameters parameters = backCamera.getParameters();
        List<Camera.Size> supportedVideoSizes = parameters.getSupportedVideoSizes();
        mCameraManager.releaseCamera();
        mCameraManager.destroyCamera();
        return supportedVideoSizes;
    }

    /**
     * 停止当前摄像头预览
     */
    public void stopCameraPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void unlockCamera() {
        if (mCamera != null) {
            mCamera.unlock();
        }
    }

    public int switchCamera(int mCameraType, SurfaceHolder holder, int width, int height) {
        if (mCamera != null) {
            if (mCameraType == CameraRecoderConstant.FRONT_CAMERA) {
                mCameraType = CameraRecoderConstant.BACK_CAMERA;
                ToastUtils.showShort("切换至后置摄像头");
            } else {
                mCameraType = CameraRecoderConstant.FRONT_CAMERA;
                ToastUtils.showShort("切换至前置摄像头");
            }
            stopCameraPreview();
            releaseCamera();
            destroyCamera();

            startPreview(mCameraType, holder, width, height);

        }
        return mCameraType;
    }
}
