package cn.scooper.com.easylib.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.IOException;

import butterknife.ButterKnife;
import cn.scooper.com.easylib.R;
import cn.scooper.com.easylib.umdata.AnalysisService;
import cn.scooper.com.easylib.utils.AppActivities;
import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.easylib.utils.ToastUtils;
import cn.scooper.com.easylib.views.dialog.AlertDialog;
import cn.scooper.com.easylib.views.dialog.LoadingDialog;

/**
 * Created by Aguai on 2016/11/18.
 * activity基类
 * 已集成 Rxjava,loadingDialog,alertdlg，
 * 友盟统计，ButterKnife
 */
public abstract class BaseActivity extends RxAppCompatActivity implements
        IBaseActivity {
    protected String TAG;
    protected AlertDialog alertDialog;
    protected LoadingDialog loadingDialog;
    protected MenuItem mPointsItem, mLimitItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppActivities.instance().add(this);

        TAG = getLocalClassName();

        // 设置渲染视图View
        View mContextView = LayoutInflater.from(this).inflate(bindLayout(), null);
        setContentView(mContextView);
        if (translucentStatus()) {
            setTranslucentStatus(true);
        }
        ButterKnife.bind(this);
        loadingDialog = new LoadingDialog(this);
        alertDialog = new AlertDialog(this);
        // 初始化控件
        initView(mContextView);
        Context mContext = this.getBaseContext();
        doBusiness(mContext);
    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    //处理异常报错
    protected void handleThrowable(Throwable t) {
        LogUtil.e(TAG, "handle throwable", t);
        if (t instanceof IOException ||
                t instanceof JsonSyntaxException) {
            ToastUtils.showShort("网络连接异常");
        }
        t.printStackTrace();
    }

    /**
     * 隐藏软键盘 hideSoftInputView
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void onBack(View v) {
        finish();
    }

    public void onResume() {
        super.onResume();
        AnalysisService.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        AnalysisService.onPause(this);
    }


    @Override
    protected void onDestroy() {
        System.gc();
        ButterKnife.unbind(this);
        AppActivities.instance().remove(this);
        super.onDestroy();
    }

    protected void setTitle(String string) {
        TextView textView = (TextView) findViewById(R.id.tv_toobar);
        textView.setText(string);
    }


    protected void exitApplication() {
        AppActivities.instance().finishAll();
    }

}
