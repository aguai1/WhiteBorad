package cn.scooper.com.whiteboard.relogic.imagecache;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Aguai on 2016/11/24.
 */

public class WBImageLoader {
    private static WBImageLoader instance;
    private ImageMemoryCache memoryCache;
    private ImageFileCache fileCache;
    private WBImageLoader(Context context) {
        memoryCache = new ImageMemoryCache(context);
        fileCache = new ImageFileCache();
    }

    public static void initLoader(Context context) {
        instance = new WBImageLoader(context);
    }

    public static WBImageLoader getInstance() {
        return instance;
    }

    public Bitmap loadBitmap(String url) {
        // 从内存缓存中获取图片
        Bitmap result = memoryCache.getBitmapFromCache(url);
        if (result == null) {
            // 文件缓存中获取
            result = fileCache.getImage(url);
            if (result != null) {
                // 添加到内存缓存
                memoryCache.addBitmapToCache(url, result);
            }
        }
        return result;
    }

    public void saveBitmap(Bitmap bitmap, String path) {
        // 添加到内存缓存
        memoryCache.addBitmapToCache(path, bitmap);
        fileCache.saveBitmap(bitmap, path);
    }


}
