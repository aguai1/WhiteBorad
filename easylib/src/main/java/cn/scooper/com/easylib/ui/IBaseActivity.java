package cn.scooper.com.easylib.ui;

import android.content.Context;
import android.view.View;

/**
 * Activity 接口
 *
 * @author 阿怪
 * @version 1.0
 */
public interface IBaseActivity {

    /**
     * 绑定渲染视图的布局文件
     *
     * @return 布局文件资源id
     */
    int bindLayout();

    /**
     * 绑定渲染视图的布局文件
     *
     * @return 布局文件资源id
     */
    boolean translucentStatus();

    /**
     * 初始化控件
     */
    void initView(final View view);

    /**
     * 实务操作
     */
    void doBusiness(Context mContext);

}
