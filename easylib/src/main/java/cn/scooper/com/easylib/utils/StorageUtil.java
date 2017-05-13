package cn.scooper.com.easylib.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件工具
 */
public final class StorageUtil {

    public static final File MEDIA_STORE_DIR = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "scooper"
    );

    public static Uri getPictureOutputUri() {
        File dir = MEDIA_STORE_DIR;
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return null;
            }
        }
        return Uri.fromFile(new File(dir, getPicFileName()));
    }

    public static Uri getVideoOutputDirUri() {
        File dir = MEDIA_STORE_DIR;
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return Uri.fromFile(dir);
    }


    public static void saveImage(Activity activity, Bitmap map) {
        if (map != null) {
            String fileName = getPicFileName();
            File photoFile = savePhotoToSDCard(map, MEDIA_STORE_DIR.getPath(), fileName);
            if (photoFile != null) {
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(Uri.fromFile(photoFile));
                activity.sendBroadcast(scanIntent);
                ToastUtils.showShort("保存成功");
                try {
                    MediaStore.Images.Media.insertImage(activity.getContentResolver(), MEDIA_STORE_DIR.getPath(), fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(photoFile);
                intent.setData(uri);
                activity.sendBroadcast(intent);
            }
        }
    }

    /**
     * Save image to the SD card
     *
     * @param photoBitmap 相片文件本身
     * @param photoName   储存的相片名字
     */
    private static File savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName) {
        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File photoFile = new File(path, photoName);

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap.compress(Bitmap.CompressFormat.WEBP, 100, fileOutputStream)) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                return photoFile;
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                ToastUtils.showShort("图片保存失败");
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ToastUtils.showShort("sd卡不可用");
        }

        return null;
    }

    /**
     * Check the SD card
     *
     * @return 是否获能获取到SD卡
     */
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getPicFileName() {
        String timestamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        return "IMG_" + timestamp + ".jpg";
    }
}
