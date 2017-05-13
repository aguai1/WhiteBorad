package cn.scooper.com.whiteboard.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhenglikun on 2016/11/28.
 */

public class StringUtils {
    public static String getStringYH(String a, String b) {
        return "\"" + a + "\":" + "\"" + b + "\"";
    }

    public static String getStringYH(String a, int b) {
        return "\"" + a + "\":" + b;
    }

    public static String getStringKH(String a) {
        return "{" + a + "}";
    }

    public static String getStringDKH(String a) {
        return "[" + a + "]";
    }

    public static String getStringYHFIR(String a, String b) {
        return "\"" + a + "\":" + b;
    }

    /**
     * 从目标串中查询到value值(string)
     *
     * @param content
     * @param findString
     * @return
     */
    public static String getStringContent(String content, String findString) {
        int position = -1;
        switch (findString) {
            case "op":
            case "type":
                position = content.lastIndexOf(findString);
                break;
            default:
                position = content.indexOf(findString);
        }

        if (position == -1) {
            return "";
        }
        int yhCount = 0;
        int fir = 0;
        int last = 0;
        for (int i = position; i < content.length(); i++) {
            char a = content.charAt(i);
            if (a == '\"') {
                yhCount++;
                if (yhCount == 2) {
                    fir = i;
                } else if (yhCount == 3) {
                    last = i;
                    break;
                }
            }

        }
        return content.substring(fir + 1, last);
    }

    /**
     * 从目标串中查询到value值(int)
     *
     * @param content
     * @param findString
     * @return
     */
    public static int getIntContent(String content, String findString) {
        int position = content.indexOf(findString);
        if (position == -1) {
            return 0;
        }
        int result = 0;
        boolean isNum = false;
        for (int i = position; i < content.length(); i++) {
            char a = content.charAt(i);
            if (a >= '0' && a <= '9') {
                int num = a - '0';
                result = result * 10 + num;
                if (isNum == false) {
                    isNum = true;
                }
            } else {
                if (isNum == true) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 从目标串中查询到数组(int)
     *
     * @param content
     * @param findString
     * @return
     */
    public static List<Integer> getIntArrContent(String content, String findString) {
        List<Integer> mList = new ArrayList<>();
        int index = 0;
        int position = content.indexOf(findString, index);
        while (position != -1) {
            int result = 0;
            boolean isNum = false;
            for (int i = position; i < content.length(); i++) {
                char a = content.charAt(i);
                if (a >= '0' && a <= '9') {
                    int num = a - '0';
                    result = result * 10 + num;
                    if (isNum == false) {
                        isNum = true;
                    }
                } else {
                    if (isNum == true) {
                        mList.add(result);
                        index = i;
                        break;
                    }
                }
            }
            position = content.indexOf(findString, index);
        }
        return mList;
    }

    public static List<String> getStringArrContent(String content, String findString) {
        List<String> mList = new ArrayList<>();
        int index = 0;
        int position = content.indexOf(findString, index);
        while (position != -1) {
            int yhCount = 0;
            int fir = 0;
            int last = 0;
            for (int i = position; i < content.length(); i++) {
                char a = content.charAt(i);
                if (a == '\"') {
                    yhCount++;
                    if (yhCount == 2) {
                        fir = i;
                    } else if (yhCount == 3) {
                        last = i;
                        index = last;
                        break;
                    }
                }

            }
            mList.add(content.substring(fir + 1, last));
            position = content.indexOf(findString, index);
        }
        return mList;
    }

}
