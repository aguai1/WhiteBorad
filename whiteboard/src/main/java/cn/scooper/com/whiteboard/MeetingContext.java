package cn.scooper.com.whiteboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import cn.scooper.com.easylib.rxbus.RxBus;
import cn.scooper.com.easylib.utils.AppActivities;
import cn.scooper.com.easylib.utils.LogUtil;
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
