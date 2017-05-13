package cn.scooper.com.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.trello.rxlifecycle.ActivityEvent;

import butterknife.Bind;
import butterknife.OnClick;
import cn.scooper.com.easylib.rxbus.RxBus;
import cn.scooper.com.easylib.ui.BaseActivity;
import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.easylib.utils.ToastUtils;
import cn.scooper.com.whiteboard.R;
import cn.scooper.com.whiteboard.event.NotifyEvent;
import cn.scooper.com.whiteboard.relogic.minaclient.Request;
import cn.scooper.com.whiteboard.ui.MeetingActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class CreateMeetingActivity extends BaseActivity {

    @Bind(R.id.meetingName)EditText meetingName;
    @Bind(R.id.meetingId)TextView meetingId;
    @Bind(R.id.userId) TextView userId;
    @OnClick({R.id.btn_ok})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_ok:
                Request.IINSTANCE.creatMeeting(meetingName.getText().toString(),0);
        }
    }
    @Override
    public int bindLayout() {
        return R.layout.activity_menu;
    }

    @Override
    public boolean translucentStatus() {
        return false;
    }

    @Override
    public void initView(View view) {
        userId.setText(Request.userId+"");
    }

    @Override
    public void doBusiness(Context mContext) {
        registerBroadcastReceiver();
    }


    private void registerBroadcastReceiver() {

        RxBus.get().toObservable(NotifyEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(this.<NotifyEvent>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<NotifyEvent>() {
                    @Override
                    public void call(final NotifyEvent inviteEvent) {
                        if (inviteEvent.eventType == NotifyEvent.NOTIFY_CONNECT_ERROR) {
                            LogUtil.e(TAG,"连接服务器失败");
                            loadingDialog.dismiss();
                            ToastUtils.showShort("连接服务器失败");
                        }else if (inviteEvent.eventType == NotifyEvent.NOTIFY_CREATE_SUCCEED) {
                            Request.IINSTANCE.join(inviteEvent.meetingId);
                            startActivity(new Intent(CreateMeetingActivity.this, MeetingActivity.class));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "Rxjava error");
                    }
                });
    }

}