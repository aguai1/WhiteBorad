package cn.scooper.com.easylib.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonSyntaxException;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.io.IOException;

import butterknife.ButterKnife;
import cn.scooper.com.easylib.utils.ToastUtils;

/**
 * Created by Aguai on 2016/11/20.
 * fragment基类
 * 已集成rxjava，ButterKnife
 */
public abstract class BaseFragment extends RxFragment implements IBaseFragment {
    private String TAG = "BaseFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(bindLayout(), container, false);
        ButterKnife.bind(this, view);
        initView();
        doBusiness();
        return view;
    }

    //处理异常报错
    protected void handleThrowable(Throwable t) {
        Log.e(TAG, "handle throwable " + t.getMessage());
        if (t instanceof IOException ||
                t instanceof JsonSyntaxException) {
            ToastUtils.showShort("网络连接异常");
        }
        t.printStackTrace();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
