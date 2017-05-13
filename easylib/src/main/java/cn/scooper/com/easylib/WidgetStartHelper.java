package cn.scooper.com.easylib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import cn.scooper.com.easylib.ui.album.PhotoAlbumActivity;
import cn.scooper.com.easylib.ui.image.ImageDetailActivity;
import cn.scooper.com.easylib.ui.recorder.CameraRecoderConstant;
import cn.scooper.com.easylib.ui.recorder.RecorderActivity;

/**
 * Created by Aguai on 2016/11/20.
 * easyLib组件启动helper类
 */

public class WidgetStartHelper {
    /**
     * 使用手机拨打电话
     *
     * @param tel
     */
    public static void makeCall(Context context, String tel) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + tel));
        context.startActivity(intent);
    }

    /**
     * 使用相机拍照
     * （完成后，通过 onActivityResult 接收）
     *
     * @param storeUri 拍摄图片时保存的文件路径
     */
    public static void takePhoto(Activity activity, Uri storeUri, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (storeUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, storeUri);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开相册以供选择
     */
    public static void pickPhoto(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * 调用系统自带录像功能
     * （完成后，通过 onActivityResult 接收）
     *
     * @param storeUri 录像文件保存的文件路径
     */
    public static void takeVideo(Activity activity, Uri storeUri, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (storeUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, storeUri);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 调用系统自带的录像功能
     * 特别说明：对于限制质量等不同手机会有不同的表现，不一定有效，而且即使有效也有不同的表现。
     * 最好的方式还是自己实现视频录制功能，这样对视频的质量能够统一控制。
     * （完成后，通过 onActivityResult 接收）
     *
     * @param storeUri    录像文件保存的文件路径
     * @param requestCode 返回代码
     * @param quality     0 差(176x144)，1 高(1920x1080)
     * @param duration    录制时长(s)
     * @param size        视频大小(bytes)
     */
    public static void takeVideo(Activity activity, Uri storeUri, int requestCode, int quality, int duration, int size) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (storeUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, storeUri);
        }
        //
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, duration);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, size);
        //
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 启动摄像RecorderActivity
     */
    public static void takeVideo(Activity activity, String storeUri, int requestCode, int duration, int size, int w, int h, boolean isMicInUse) {
        Intent intent = new Intent(activity, RecorderActivity.class);
        //设置参数 时长前后摄像头等
        intent.putExtra(CameraRecoderConstant.CAMERA_TYPE, CameraRecoderConstant.BACK_CAMERA);
        intent.putExtra(CameraRecoderConstant.RESOLUTION_W, w);
        intent.putExtra(CameraRecoderConstant.RESOLUTION_H, h);
        intent.putExtra(CameraRecoderConstant.PATH_DIR, storeUri.toString());
        intent.putExtra(CameraRecoderConstant.MAX_DURATION, duration);
        intent.putExtra(CameraRecoderConstant.ENCODING_BITRATE, size);
        intent.putExtra(CameraRecoderConstant.MIC_INUSE, isMicInUse);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开相册以选择录像文件
     */
    public static void pickVideo(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Toast.makeText(activity, "无法打开摄像文件", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 启动图片查看器
     */
    public static void showPicDetail(Activity activity, ImageView imageView, String picUrl) {
        int[] locations = new int[2];
        imageView.getLocationOnScreen(locations);
        int height = imageView.getHeight();
        int width = imageView.getWidth();
        Intent intent = new Intent(activity, ImageDetailActivity.class);
        intent.putExtra("image_url", picUrl);
        intent.putExtra("height", height);
        intent.putExtra("width", width);
        intent.putExtra("x", locations[0]);
        intent.putExtra("y", locations[1]);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
        ImageDetailActivity.setStartView(imageView);
    }


    /**
     * 启动自定义相册单选
     */
    public static void showAlbum(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, PhotoAlbumActivity.class), requestCode);
    }

    /**
     * 启动自定义相册多选
     * num 选择数量待实现
     */
    public static void showAlbumMoreSel(Activity activity, int num, int requestCode) {
        Intent intent1 = new Intent(activity, PhotoAlbumActivity.class);
        intent1.putExtra("moreSel", true);
        intent1.putExtra("num", num);
        activity.startActivityForResult(intent1, requestCode);
    }
}
