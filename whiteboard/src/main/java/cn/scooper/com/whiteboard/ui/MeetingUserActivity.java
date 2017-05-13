package cn.scooper.com.whiteboard.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scooper.cn.whiteboard.R;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import cn.scooper.com.easylib.rxbus.RxBus;
import cn.scooper.com.easylib.ui.BaseActivity;
import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.whiteboard.adpter.MemberAdapter;
import cn.scooper.com.whiteboard.db.domain.UserBean;
import cn.scooper.com.whiteboard.event.QueryMemberEvent;
import cn.scooper.com.whiteboard.relogic.minaclient.Request;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Aguai on 2016/11/24.
 * <p>
 * 查看会议用户列表
 */

public class MeetingUserActivity extends BaseActivity {

    private MemberAdapter memberAdapter;
    private RecyclerView recyclerView;

    @Override
    public int bindLayout() {
        return R.layout.activity_meetinguser;
    }

    @Override
    public boolean translucentStatus() {
        return false;
    }

    @Override
    public void initView(View view) {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitle("会议用户");
    }


    @Override
    public void doBusiness(Context mContext) {
        Request.IINSTANCE.queryMember(Request.meetingId);
        initRxjava();
        List<UserBean> userBeens = new ArrayList<>();
        memberAdapter = new MemberAdapter(getBaseContext(), userBeens, recyclerView);
        recyclerView.setAdapter(memberAdapter);
    }

    private void initRxjava() {
        RxBus.get().toObservable(QueryMemberEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(this.<QueryMemberEvent>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<QueryMemberEvent>() {
                    @Override
                    public void call(final QueryMemberEvent inviteEvent) {
                        if (inviteEvent.users != null) {
                            memberAdapter.setUserList(inviteEvent.users);
                            memberAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "rxbus失败");
                    }
                });
    }

}
