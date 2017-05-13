package cn.scooper.com.easylib.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;


/**
 * Created by Aguai on 2016/11/20.
 * android 23以上很多权限都需要手动授权
 */

public class PermissionsUtil {
    public static final int TAKE_PHOTO_REQUEST_CODE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    /**
     * 当用户忽略时下次默认是不弹出对话框的需要手动进行处理
     *
     * @param msg
     * @param context
     * @param permission
     */
    public static void showDialog(final String msg, final Context context, final String permission, final int requestCode) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                requestCode);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public static void getCameraPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA},
                TAKE_PHOTO_REQUEST_CODE);
    }

    public static void getReadExternalStoragePermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }
}
