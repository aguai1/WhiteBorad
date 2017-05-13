package cn.scooper.com.whiteboard.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scooper.cn.whiteboard.R;

import java.util.ArrayList;
import java.util.List;

import cn.scooper.com.easylib.rxbus.RxBus;
import cn.scooper.com.easylib.ui.BaseActivity;
import cn.scooper.com.whiteboard.adpter.BlackBoardAdapter;
import cn.scooper.com.whiteboard.db.domain.PageBean;
import cn.scooper.com.whiteboard.db.domain.ShapeBean;
import cn.scooper.com.whiteboard.db.helper.ShapeHelper;
import cn.scooper.com.whiteboard.event.NotifyEvent;
import cn.scooper.com.whiteboard.relogic.Const;
import cn.scooper.com.whiteboard.relogic.minaclient.Request;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.AbsShape;

/**
 * Created by Aguai on 2016/11/24.
 * <p>
 * 白板列表
 */

public class BoardListActivity extends BaseActivity {

    private BlackBoardAdapter memberAdapter;
    private RecyclerView recyclerView;
    private ShapeHelper shapeHelper;

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
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
        setTitle("白板列表");
    }


    @Override
    public void doBusiness(Context mContext) {
        shapeHelper = new ShapeHelper(getApplicationContext());
        List<PageBean> shapesPage = shapeHelper.getAllShapesPageByMeetingId(Request.meetingId);
        List<List<AbsShape>> listList = new ArrayList<>();
        if (shapesPage != null) {
            for (int i = 0; i < shapesPage.size(); ++i) {
                String id = shapesPage.get(i).getServicePageId();
                List<ShapeBean> shapesByPage = shapeHelper.getShapesByMeetingPage(Request.meetingId, id);
                listList.add(ShapeBean.getAbsShape(shapesByPage));
            }
        }
        memberAdapter = new BlackBoardAdapter(this, listList, recyclerView);
        memberAdapter.setItemOnClickListener(new BlackBoardAdapter.OnItemClickListener() {
            @Override
            public void onClick(List<AbsShape> absShapeList) {
                if (absShapeList.size() <= 0) return;
                NotifyEvent event = new NotifyEvent();
                event.eventType = NotifyEvent.NOTIFY_PAGE_NEXT;
                event.pageId = absShapeList.get(0).getMeetingPage();
                RxBus.get().send(event);

                Request.IINSTANCE.sendPageCom(Integer.parseInt(event.pageId), -1, Const.PAGE_NEXT);
            }
        });
        recyclerView.setAdapter(memberAdapter);
    }

}
