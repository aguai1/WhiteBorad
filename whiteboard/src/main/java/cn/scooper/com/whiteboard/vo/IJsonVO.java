package cn.scooper.com.whiteboard.vo;

import org.json.JSONException;


public interface IJsonVO {
    /**
     * 从JSON解析数据
     *
     * @param o
     * @throws JSONException
     */
    void parse(String o) throws JSONException;
}
