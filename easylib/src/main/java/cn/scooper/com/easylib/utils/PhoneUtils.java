package cn.scooper.com.easylib.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;

/**
 * 手机相关功能调用
 */
public final class PhoneUtils {

    /**
     * 获取存储的媒体文件
     *
     * @param context    activity
     * @param storageUri 媒体文件存储路径Uri
     * @param data       onActivityResult 参数中的
     * @return 媒体文件存储文件
     */
    public static File getActivyResultMediaFile(Context context, Uri storageUri, Intent data) {
        Uri uri = data == null ? (storageUri == null ? null : storageUri) : data.getData();
        File file = null;
        if (uri != null) {
            if ("file".equals(uri.getScheme())) {
                file = new File(uri.getPath());
            } else {
                String[] proj = {MediaStore.Images.Media.DATA};
                CursorLoader loader = new CursorLoader(context, uri, proj, null, null, null);
                Cursor cursor = loader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                file = new File(cursor.getString(column_index));
            }
        }
        return file;
    }

    /**
     * 复制文本到剪切板
     *
     * @param context
     * @param text    需要复制的文本
     */
    public static void copyText(Context context, String text) {
        ClipboardManager clipManager = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText(null, text);
        clipManager.setPrimaryClip(cd);
        Toast.makeText(context, "已复制到剪切板", Toast.LENGTH_SHORT).show();
    }
}
