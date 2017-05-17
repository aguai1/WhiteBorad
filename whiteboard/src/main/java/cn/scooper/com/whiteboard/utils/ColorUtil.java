package cn.scooper.com.whiteboard.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by Aguai on 2016/11/29.
 */

public class ColorUtil {
    /**
     * COLORREF转color
     * COLORREF中颜色的排列是BGR
     */
    public static int getColor(int color) {
        int blue = Color.red(color);
        int green = Color.green(color);
        int red = Color.blue(color);
        return Color.rgb(red, green, blue);
    }

    /**
     * color转COLORREF
     * COLORREF中颜色的排列是BGR
     */
    public static int getColorRFT(int color) {

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(0,blue, green, red);
    }
    public static int randomColor(int alpha) {
        Random rnd = new Random();
        alpha = Math.min(Math.max(1, alpha), 255);
        return Color.argb(alpha, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
