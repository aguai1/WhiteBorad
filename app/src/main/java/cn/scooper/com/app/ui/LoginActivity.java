package cn.scooper.com.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.trello.rxlifecycle.ActivityEvent;
import butterknife.Bind;
import butterknife.OnClick;
import cn.scooper.com.app.Setting;
import cn.scooper.com.easylib.rxbus.RxBus;
import cn.scooper.com.easylib.ui.BaseActivity;
import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.easylib.utils.SharedPreferencesUtils;
import cn.scooper.com.easylib.utils.ToastUtils;
import cn.scooper.com.whiteboard.R;
import cn.scooper.com.whiteboard.event.NotifyEvent;
import cn.scooper.com.whiteboard.event.UserOpEvent;
import cn.scooper.com.whiteboard.relogic.Const;
import cn.scooper.com.whiteboard.relogic.minaclient.Request;
import cn.scooper.com.whiteboard.utils.ZlibUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class LoginActivity extends BaseActivity {
    private final String USERNAME="username";
    private final String PASSWD="passwd";
    @Bind(R.id.username)EditText username;
    @Bind(R.id.password)EditText password;
    @Bind(R.id.inviteId)TextView inviteId;
    @OnClick({R.id.btn_ok})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_ok:

                String v_name = username.getText().toString();
                String v_pass= password.getText().toString();
//                if(TextUtils.isEmpty(v_name)){
//                    ToastUtils.showShort("用户名为空");
//                    return;
//                }
                SharedPreferencesUtils.put(USERNAME,v_name);
                SharedPreferencesUtils.put(PASSWD,v_pass);

                loadingDialog.setMsg("正在登陆请稍候。。。");
                loadingDialog.show();
                Request.IINSTANCE.stopConnectServer();
                Request.IINSTANCE.connectServer(Setting.CONNECT_IP, Setting.CONNECT_PORT);
                Request.IINSTANCE.login(v_name,v_pass);

        }
    }
    @Override
    public int bindLayout() {
        return R.layout.activity_login;

    }

    @Override
    public boolean translucentStatus() {
        return true;
    }

    @Override
    public void initView(View view) {

        username.setText(SharedPreferencesUtils.get( USERNAME, "").toString());
        password.setText(SharedPreferencesUtils.get(PASSWD, "").toString());
    }

    @Override
    public void doBusiness(Context mContext) {
        registerBroadcastReceiver();

    }



    private void registerBroadcastReceiver() {
        RxBus.get().toObservable(UserOpEvent.class)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<UserOpEvent>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<UserOpEvent>() {
                    @Override
                    public void call(UserOpEvent userChangeEvent) {
                        loadingDialog.dismiss();
                        if(userChangeEvent.vo.token.equals("")){
                            ToastUtils.showShort("用户名密码错误，或你已经登录");
                        }else {
                            ToastUtils.showShort(userChangeEvent.vo.uname);
                            inviteId.setText(userChangeEvent.vo.uid + "");
                            startActivity(new Intent(LoginActivity.this, CreateMeetingActivity.class));
                        }
                    }
                });
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
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e("MeetingContext", "显示对话框失败");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
