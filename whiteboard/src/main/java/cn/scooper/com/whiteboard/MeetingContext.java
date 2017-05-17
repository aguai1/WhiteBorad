package cn.scooper.com.whiteboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;

import cn.scooper.com.easylib.rxbus.RxBus;
import cn.scooper.com.easylib.utils.AppActivities;
import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.easylib.utils.ToastUtils;
import cn.scooper.com.easylib.views.dialog.AlertDialog;
import cn.scooper.com.whiteboard.event.InviteEvent;
import cn.scooper.com.whiteboard.relogic.minaclient.Request;
import cn.scooper.com.whiteboard.ui.MeetingActivity;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Aguai on 2016/11/24.
 * 该类用于邀请事件的监听
 */
public class MeetingContext {
    private CompositeSubscription mCompositeSubscription;

    public MeetingContext(Context context) {
        mCompositeSubscription = new CompositeSubscription();
    }

    /**
     * 开始监听
     */
    public void startEventListener() {
        Subscription subscribe = RxBus.get().toObservable(InviteEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<InviteEvent>() {
                    @Override
                    public void call(final InviteEvent inviteEvent) {
                        final Activity current = AppActivities.instance().current();
                        if (current == null) return;
                        AlertDialog dialog = new AlertDialog(current);
                        dialog.setMsg(inviteEvent.fromUserId + "正在邀请你加入会议" + inviteEvent.meetingName)
                                .setTitle("会议邀请")
                                .setPositiveButton("同意", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Request.IINSTANCE.join(inviteEvent.meetingId);
                                        current.startActivity(new Intent(current, MeetingActivity.class));
                                    }
                                }).setNegativeButton("拒绝", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        dialog.show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e("MeetingContext", "显示对话框失败");
                    }
                });
        mCompositeSubscription.add(subscribe);

        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                try {
                    EMClient.getInstance().contactManager().addContact(username, reason);
                    WhiteBoardApplication.getInstance().getAllGroup();
                    LogUtil.e("MeetingContext","添加成功"+username);

                } catch (HyphenateException e) {
                    LogUtil.e("MeetingContext","添加失败"+e.getDescription());

                }
            }

            @Override
            public void onFriendRequestAccepted(String username) {
                //好友请求被同意
            }

            @Override
            public void onFriendRequestDeclined(String username) {
                //好友请求被拒绝
            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
            }
        });
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            final Activity currentActivity = WhiteBoardApplication.getInstance().getCurrentActivity();
            if (currentActivity != null) {
                currentActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (error == EMError.USER_REMOVED) {
                            ToastUtils.showShort("帐号已经被移除");
                            // 显示帐号已经被移除
                        } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                            ToastUtils.showShort("帐号在其他设备登录");
                            // 显示帐号在其他设备登录
                        } else if (NetUtils.hasNetwork(currentActivity)) {
                            ToastUtils.showShort("连接不到聊天服务器");
                            //连接不到聊天服务器
                        } else {
                            ToastUtils.showShort("当前网络不可用,请检查网络设置");
                        }
                    }
                });
            }
        }
    }
    /**
     * 取消监听
     */
    public void stopEventListener() {
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
        Request.IINSTANCE.stopConnectServer();

    }

}
