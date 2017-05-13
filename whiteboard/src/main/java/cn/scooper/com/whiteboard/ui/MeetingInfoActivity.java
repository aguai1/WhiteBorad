package cn.scooper.com.whiteboard.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;

import com.scooper.cn.whiteboard.R;

import cn.scooper.com.easylib.ui.BaseActivity;

/**
 * Created by Aguai on 2016/11/24.
 * <p>
 * 会议详情
 */

public class MeetingInfoActivity extends BaseActivity implements View.OnClickListener {
    @Override
    public int bindLayout() {
        return R.layout.activity_meetinginfo;
    }

    @Override
    public boolean translucentStatus() {
        return false;
    }

    @Override
    public void initView(View view) {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setTitle("会议信息");
        findViewById(R.id.lay_users).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_exit) {
            onBackPressed();
        } else if (id == R.id.lay_users) {
            Intent intent = new Intent(this, MeetingUserActivity.class);
            startActivity(intent);
        }
    }

}
