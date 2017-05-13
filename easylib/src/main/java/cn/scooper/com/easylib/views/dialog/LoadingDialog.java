package cn.scooper.com.easylib.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.scooper.com.easylib.R;
import cn.scooper.com.easylib.utils.ScreenUtils;


public class LoadingDialog {

    private Context mContext;
    private Dialog mDialog;
    private TextView mLoadingPoint;

    public LoadingDialog(Context context) {
        this.mContext = context;
        builder();
    }

    public LoadingDialog builder() {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_loading, null);

        mLoadingPoint = (TextView) view.findViewById(R.id.loading_text);
        mDialog = new Dialog(mContext, R.style.AlertDialogStyle);
        mDialog.setCancelable(true);
        mDialog.setContentView(view);
        view.setLayoutParams(new FrameLayout.LayoutParams((int) (ScreenUtils.getScreenWidth(mContext) * 0.8), LinearLayout.LayoutParams.WRAP_CONTENT));
        return this;
    }

    public LoadingDialog setMsg(String msg) {
        if ("".equals(msg)) {
            mLoadingPoint.setText(R.string.loading_default_msg);
        } else {
            mLoadingPoint.setText(msg);
        }
        return this;
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }
}
