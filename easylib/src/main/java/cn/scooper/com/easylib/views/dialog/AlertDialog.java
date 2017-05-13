package cn.scooper.com.easylib.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import cn.scooper.com.easylib.R;
import cn.scooper.com.easylib.utils.ScreenUtils;

public class AlertDialog {
    private Context mContext;
    private Dialog mDialog;
    private TextView mDialogTitle;
    private TextView mDialogMsg;
    private TextView mNegativeButton;
    private TextView mPositiveButton;
    private CheckBox mCheckbox;

    public AlertDialog(Context context) {
        this.mContext = context;
        builder();
    }

    public AlertDialog builder() {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_common, null);

        mDialogTitle = (TextView) view.findViewById(R.id.dialog_title);
        mDialogMsg = (TextView) view.findViewById(R.id.dialog_msg);
        mNegativeButton = (TextView) view.findViewById(R.id.left_button);
        mPositiveButton = (TextView) view.findViewById(R.id.right_button);
        mCheckbox = (CheckBox) view.findViewById(R.id.checkbox);

        mDialog = new Dialog(mContext, R.style.AlertDialogStyle);
        mDialog.setContentView(view);

        view.setLayoutParams(new FrameLayout.LayoutParams((int) (ScreenUtils.getScreenWidth(mContext) * 0.85), LayoutParams.WRAP_CONTENT));

        return this;
    }

    public AlertDialog setTitle(String title) {
        if (title == null) {
            mDialogTitle.setText(R.string.app_name);
        } else {
            mDialogTitle.setText(title);
        }
        return this;
    }

    public AlertDialog setMsg(String msg) {
        if ("".equals(msg)) {
            mDialogMsg.setText(R.string.dialog_no_msg);
        } else {
            mDialogMsg.setText(msg);
        }
        return this;
    }

    public AlertDialog setPositiveButton(String text,
                                         final OnClickListener listener) {
        if ("".equals(text)) {
            mPositiveButton.setText(R.string.confirm);
        } else {
            mPositiveButton.setText(text);
        }
        mPositiveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                }
                mDialog.dismiss();
            }
        });
        return this;
    }

    public AlertDialog setPositiveButtonColor(int color) {
        mPositiveButton.setTextColor(color);
        return this;
    }

    public void setPositiveButtonClick(boolean click) {
        mPositiveButton.setClickable(click);
    }

    public AlertDialog setNegativeButton(String text,
                                         final OnClickListener listener) {
        if ("".equals(text)) {
            mNegativeButton.setText(R.string.cancel);
        } else {
            mNegativeButton.setText(text);
        }
        mNegativeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                }
                mDialog.dismiss();
            }
        });
        return this;
    }

    public AlertDialog setCheckBox(String text, final OnCheckedChangeListener listener) {
        mCheckbox.setVisibility(View.VISIBLE);
        mCheckbox.setText(text);
        mCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    listener.onCheckedChanged(buttonView, true);
                } else {
                    listener.onCheckedChanged(buttonView, false);
                }
            }
        });
        return this;
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }
}
