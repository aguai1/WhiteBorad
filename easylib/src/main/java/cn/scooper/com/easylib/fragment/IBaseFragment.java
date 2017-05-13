package cn.scooper.com.easylib.fragment;

/**
 * Activity 接口
 *
 * @author 阿怪
 * @version 1.0
 */
public interface IBaseFragment {

    /**
     * 绑定渲染视图的布局文件
     *
     * @return 布局文件资源id
     */
    int bindLayout();

    /**
     * 初始化控件
     */
    void initView();

    /**
     * 实务操作
     */
    void doBusiness();


}
