package cn.scooper.com.whiteboard.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.scooper.cn.whiteboard.R;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import cn.scooper.com.easylib.WidgetStartHelper;
import cn.scooper.com.easylib.rxbus.Event;
import cn.scooper.com.easylib.rxbus.RxBus;
import cn.scooper.com.easylib.ui.BaseActivity;
import cn.scooper.com.easylib.utils.BitmapUtils;
import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.easylib.utils.ToastUtils;
import cn.scooper.com.whiteboard.WhiteBoardApplication;
import cn.scooper.com.whiteboard.adpter.ChatAdapter;
import cn.scooper.com.whiteboard.db.domain.ShapeBean;
import cn.scooper.com.whiteboard.db.helper.ShapeHelper;
import cn.scooper.com.whiteboard.event.MyEventBus;
import cn.scooper.com.whiteboard.event.NotifyEvent;
import cn.scooper.com.whiteboard.event.ShapeEvent;
import cn.scooper.com.whiteboard.relogic.Const;
import cn.scooper.com.whiteboard.relogic.imagecache.WBImageLoader;
import cn.scooper.com.whiteboard.relogic.minaclient.Request;
import cn.scooper.com.whiteboard.utils.AnimUtil;
import cn.scooper.com.whiteboard.utils.ChatUtil;
import cn.scooper.com.whiteboard.utils.ColorUtil;
import cn.scooper.com.whiteboard.views.ChatRecyclerView;
import cn.scooper.com.whiteboard.views.Slider;
import cn.scooper.com.whiteboard.views.arcmenu.ArcMenu;
import cn.scooper.com.whiteboard.views.whiteboardview.Constants;
import cn.scooper.com.whiteboard.views.whiteboardview.SfDisplayInfoView;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.AbsShape;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.PathShape;
import cn.scooper.com.whiteboard.views.whiteboardview.shape.PicShape;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Aguai on 2016/11/24.
 * <p>
 * 电子白板会议主activity
 */

public class MeetingActivity extends BaseActivity implements View.OnClickListener {

    public float colorPos;
    //父控件
    private RelativeLayout rv_parent;
    private RelativeLayout rv_dlg;
//    private ChatRecyclerView recyclerView;
    private boolean dlgIsOpen = false;
    private SfDisplayInfoView displayInfoView;
    private ShapeHelper shapeHelper;
    private String currentPage = "-1";
    private int maxPageNum = 0;
    private MyEventBus.ListenerPage listenerPage;
    private MyEventBus.OnReciveBroadCastListener onReciveBroadCastListener = new MyEventBus.OnReciveBroadCastListener() {
        @Override
        public void onRecive(Event e) {

            ShapeEvent shapeEvent = (ShapeEvent) e;
            AbsShape absShape = ShapeBean.getAbsShape(shapeEvent.shapeBean);
            if (shapeEvent.opType == ShapeEvent.OP_ADD) {
                int pageNum = Integer.parseInt(shapeEvent.shapeBean.getPageId());
                if (currentPage.equals("-1")) {
                    currentPage = pageNum + "";
                    maxPageNum = pageNum;
                }
                if (pageNum > maxPageNum) {
                    maxPageNum = pageNum;
                }
                if (currentPage.equals(absShape.getMeetingPage())) {
                    displayInfoView.addShape(absShape);
                }
                shapeHelper.insetShape(Request.meetingId, absShape.getMeetingPage(), shapeEvent.shapeBean);
            } else if (shapeEvent.opType == ShapeEvent.OP_DELETE) {
                displayInfoView.removeShape(absShape.getServiceId());
                shapeHelper.deleteShape(absShape.getServiceId());
            } else if (shapeEvent.opType == ShapeEvent.OP_MOVE) {
                displayInfoView.moveShape(absShape.getServiceId(), absShape.getOffsetX(), absShape.getOffsetY());
                shapeHelper.moveShape(Request.meetingId, absShape.getMeetingPage(), absShape.getServiceId(), absShape.getOffsetX(), absShape.getOffsetY());
            } else if (shapeEvent.opType == ShapeEvent.OP_ADJUST_BUNDS) {
                displayInfoView.adjustShapeBunds(absShape.getServiceId(), absShape.getStartX(), absShape.getStartY(), absShape.getEndx(), absShape.getEndy());
                shapeHelper.adjustShapeBunds(Request.meetingId, absShape.getMeetingPage(), absShape.getServiceId(), absShape.getStartX(), absShape.getStartY(), absShape.getEndx(), absShape.getEndy());
            } else if (shapeEvent.opType == ShapeEvent.OP_ADJUST_COLOR) {
                displayInfoView.adjustShapeColor(absShape.getServiceId(), absShape.getColor());
                shapeHelper.adjustShapeColor(Request.meetingId, absShape.getMeetingPage(), absShape.getServiceId(), absShape.getColor());
            } else {
                displayInfoView.adjustShapeWidth(absShape.getServiceId(), absShape.getWidth());
                shapeHelper.adjustShapeWidth(Request.meetingId, absShape.getMeetingPage(), absShape.getServiceId(), absShape.getWidth());
            }

        }
    };

    private DanmakuView danmakuView;

    private DanmakuContext danmakuContext;

    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    private Dialog mInviteDialog;
//    private ChatAdapter chatAdapter;
//    private List<EMMessage> messageList;
    private boolean isCreate;
    private boolean showDanmaku;

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public boolean translucentStatus() {
        return false;
    }

    @Override
    public void initView(View view) {
//        messageList=new ArrayList<>();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        displayInfoView = (SfDisplayInfoView) findViewById(R.id.drawerView);
        rv_parent = (RelativeLayout) findViewById(R.id.rv_lay);
//        recyclerView= (ChatRecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        chatAdapter = new ChatAdapter(getBaseContext(), messageList, recyclerView);
//        recyclerView.setAdapter(chatAdapter);

        TextView title= (TextView) findViewById(R.id.title);
        title.setText(Request.meetingName);
        initArcMenu();
        initListener();
        initDanmuView();
    }

    private void initDanmuView() {
        danmakuView = (DanmakuView) findViewById(R.id.danmaku_view);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
                danmakuView.start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser, danmakuContext);
    }
    /**
     * 向弹幕View中添加一条弹幕
     * @param content
     *          弹幕的具体内容
     * @param  withBorder
     *          弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(30);
        danmaku.textColor = ColorUtil.randomColor(100);
        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }
    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    @Override
    public void doBusiness(Context mContext) {
        isCreate = getIntent().getBooleanExtra("isCreate", false);

        initRxBus();
        displayInfoView.setSizes(1280, 700);
        displayInfoView.setOnDrawLineListener(new SfDisplayInfoView.OnDrawLineListener() {
            @Override
            public void onNewLine(PathShape lineShape) {
                if (currentPage.equals("-1")) {
                    currentPage = "0";
                }
                lineShape.setMeetingPage(currentPage);
                lineShape.setMeetingId(Request.meetingId);
                shapeHelper.insetShape(Request.meetingId, currentPage, lineShape.toShapeBean());
                Request.IINSTANCE.sendNewShapeMsg(lineShape);
            }
        });
        shapeHelper = new ShapeHelper(getApplicationContext());
        shapeHelper.clearByMeeting(Request.meetingId);

        EMClient.getInstance().chatManager().addMessageListener(msgListener);
//        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
//        //获取此会话的所有消息
//        List<EMMessage> messages = conversation.getAllMessages();
//        //SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
//        //获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
//        List<EMMessage> messages = conversation.loadMoreMsgFromDB(startMsgId, pagesize);
    }

    private void initListener() {
        findViewById(R.id.iv_udo).setOnClickListener(this);
        findViewById(R.id.iv_file).setOnClickListener(this);
        findViewById(R.id.iv_saveandnew).setOnClickListener(this);
        findViewById(R.id.iv_clear).setOnClickListener(this);
        findViewById(R.id.iv_image).setOnClickListener(this);
        findViewById(R.id.iv_info).setOnClickListener(this);
        findViewById(R.id.iv_video).setOnClickListener(this);
        findViewById(R.id.iv_clear).setOnClickListener(this);
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(final List<EMMessage> messages) {
            //收到消息
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    messageList.addAll(messages);
//                    chatAdapter.notifyDataSetChanged();
//                    recyclerView.scrollToPosition(messageList.size()-1);
                    for (EMMessage emMessage:messages){
                        if (emMessage.getType()==EMMessage.Type.TXT){
                            EMTextMessageBody textMsg = (EMTextMessageBody) emMessage.getBody();
                            addDanmaku(textMsg.getMessage(),false);
                        }
                    }
                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };
    private void initRxBus() {
        RxBus.get().toObservable(NotifyEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(this.<NotifyEvent>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<NotifyEvent>() {
                    @Override
                    public void call(final NotifyEvent inviteEvent) {
                        if (inviteEvent.eventType == NotifyEvent.NOTIFY_DELETE_ALL_OBJ) {
                            displayInfoView.removeAllShape();
                            shapeHelper.deleteShapeByPage(Request.meetingId, inviteEvent.pageId);
                        } else if (inviteEvent.eventType == NotifyEvent.NOTIFY_PAGE_ADD) {
                            displayInfoView.removeAllShape();
                            shapeHelper.insetPage(Request.meetingId, inviteEvent.pageId);
                            currentPage = inviteEvent.pageId;
                        } else if (inviteEvent.eventType == NotifyEvent.NOTIFY_PAGE_NEXT) {
                            displayInfoView.removeAllShape();
                            List<ShapeBean> shapesByMeetingPage = shapeHelper.getShapesByMeetingPage(Request.meetingId, inviteEvent.pageId);
                            displayInfoView.addShapesAndShowResult(ShapeBean.getAbsShape(shapesByMeetingPage));
                            currentPage = inviteEvent.pageId;
                        } else if (inviteEvent.eventType == NotifyEvent.NOTIFY_FORCE_EXIT) {
                            if (!isCreate){
                                alertDialog.setTitle(getString(R.string.app_name))
                                        .setMsg("您已被迫退出会议")
                                        .setNegativeButton("取消", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finish();
                                            }
                                        })
                                        .setPositiveButton("确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finish();
                                            }
                                        }).show();
                            }
                        } else if (inviteEvent.eventType == NotifyEvent.NOTIFY_USER_JOIN) {

                            ChatUtil.invite(Request.meetingId,inviteEvent.userId);
                            List<ShapeBean> currentShapes = shapeHelper.getShapesByMeetingPage(Request.meetingId, currentPage);

                            List<ShapeBean> allPageBean = shapeHelper.getAllShapesByMeetingIdInstandOfPageId(Request.meetingId, currentPage);
                            allPageBean.addAll(0, currentShapes);
                            for (ShapeBean shapeBean : allPageBean) {
                                Request.IINSTANCE.sendShapeMsg(shapeBean, inviteEvent.userId);
                            }
                        }else if(inviteEvent.eventType == NotifyEvent.NOTIFY_CONNECT_CLOSE){
                            ToastUtils.showShort("服务器连接断开");
                            finish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "rxbus失败");
                    }
                });
        listenerPage = MyEventBus.get().addEventListener(ShapeEvent.class, onReciveBroadCastListener);
    }

    private void initArcMenu() {
        ArcMenu menu = (ArcMenu) findViewById(R.id.arc_menu);
        menu.setmPosition(ArcMenu.Position.RIGHT_BOTTOM);
        menu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                int id = view.getId();
                if (id == R.id.iv_eraser) {
                    displayInfoView.setCurrentMode(SfDisplayInfoView.MODE_ERASER);
                } else if (id == R.id.iv_tool) {
                    if (dlgIsOpen) return;
                    startOpenAnim(view);
                    dlgIsOpen = true;
                } else if (id == R.id.iv_pen) {
                    displayInfoView.setCurrentMode(SfDisplayInfoView.MODE_PAINT);
//                    recyclerView.setOpMode(true);
                } else if (id == R.id.iv_invite) {
                    View view2 = LayoutInflater.from(getBaseContext()).inflate(
                            R.layout.activity_invit, null);
                    final com.rey.material.widget.EditText userId = (com.rey.material.widget.EditText) view2.findViewById(R.id.userId);
                    view2.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Request.IINSTANCE.invite(Request.meetingId, userId.getText().toString());
                            mInviteDialog.dismiss();
                        }
                    });
                    mInviteDialog = new Dialog(MeetingActivity.this, cn.scooper.com.easylib.R.style.AlertDialogStyle);
                    mInviteDialog.setCancelable(true);
                    mInviteDialog.setContentView(view2);
                    mInviteDialog.show();
                }else if (id == R.id.iv_chat) {
                    View view2 = LayoutInflater.from(getBaseContext()).inflate(
                            R.layout.activity_chat, null);
                    final com.rey.material.widget.EditText userId = (com.rey.material.widget.EditText) view2.findViewById(R.id.userId);
                    view2.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EMMessage emMessage = ChatUtil.sendMsg(userId.getText().toString(), Request.meetingId);
                            if (emMessage!=null){
                                if (emMessage.getType()==EMMessage.Type.TXT){
                                    EMTextMessageBody textMsg = (EMTextMessageBody) emMessage.getBody();
                                    addDanmaku(textMsg.getMessage(),false);
                                }
                            }
                            mInviteDialog.dismiss();
                        }
                    });
                    mInviteDialog = new Dialog(MeetingActivity.this, cn.scooper.com.easylib.R.style.AlertDialogStyle);
                    mInviteDialog.setCancelable(true);
                    mInviteDialog.setContentView(view2);
                    mInviteDialog.show();
                }
            }
        });

    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.close) {
            AnimUtil.startCloseAnim(rv_dlg,rv_parent);
            dlgIsOpen = false;
        } else if (id == R.id.iv_udo) {
            displayInfoView.moveToCenter();
        } else if (id == R.id.iv_info) {

            Intent intent = new Intent(this, MeetingUserActivity.class);
            startActivity(intent);
        } else if (id == R.id.iv_clear) {
            alertDialog.setTitle(getString(R.string.app_name))
                    .setMsg("是否清空白板，清空将无法恢复")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Request.IINSTANCE.sendComDeleteAll(Integer.parseInt(currentPage), -1);
                            displayInfoView.removeAllShape();
                            shapeHelper.deleteShapeByPage(Request.meetingId, currentPage);
                        }
                    }).show();
        } else if (id == R.id.iv_image) {
            WidgetStartHelper.showAlbum(this, 100);
        } else if (id == R.id.iv_file) {
            Intent intent = new Intent(this, BoardListActivity.class);
            startActivity(intent);
        } else if (id == R.id.iv_video) {
            displayInfoView.setCurrentMode(SfDisplayInfoView.MODE_SHOW);
//            recyclerView.setOpMode(false);
        } else if (id == R.id.iv_saveandnew) {
            displayInfoView.removeAllShape();

            Request.IINSTANCE.sendPageCom(++maxPageNum, -1, Const.PAGE_CREATE);
            currentPage = maxPageNum + "";
        }
    }
    /**
     * 打开对话框
     *
     * @param view
     */
    private void startOpenAnim(View view) {
        if (dlgIsOpen) return;
        addDlgView();
        AnimUtil.startOpenAnim(view,rv_dlg);
        dlgIsOpen = true;

    }

    /**
     * 增加对话框试图
     */
    private void addDlgView() {
        rv_dlg = (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.dlg_colorsel, null);
        Slider slider = (Slider) rv_dlg.findViewById(R.id.slider_color);
        slider.setPosition(colorPos, false);
        slider.setOnColorChangeListener(new Slider.OnColorChangeListener() {
            @Override
            public void changeColor(int color, float pos) {
                colorPos = pos;
                displayInfoView.setBrushColor(color);
            }
        });

        com.rey.material.widget.Slider slider_width = (com.rey.material.widget.Slider) rv_dlg.findViewById(R.id.slider_width);
        slider_width.setValue(displayInfoView.getBrushWidth(), false);
        slider_width.setOnPositionChangeListener(new com.rey.material.widget.Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(com.rey.material.widget.Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                displayInfoView.setBrushWidth(newValue);
            }
        });
        rv_dlg.findViewById(R.id.close).setOnClickListener(this);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rv_parent.addView(rv_dlg, layoutParams);
    }

    @Override
    protected void onDestroy() {
        onReciveBroadCastListener = null;
        MyEventBus.get().removeListener(listenerPage);
        listenerPage = null;

        showDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
//        final EMGroupInfo groupByMeetingId = WhiteBoardApplication.getInstance().getGroupByMeetingId(Request.meetingId);
//        if (groupByMeetingId!=null) {
//            new Thread(){
//                @Override
//                public void run() {
//                    try {
//                        if (isCreate){
//                            LogUtil.d(TAG,"destroyGroup"+groupByMeetingId.getGroupId());
//                            EMClient.getInstance().groupManager().destroyGroup(groupByMeetingId.getGroupId());//需异步处理
//                            WhiteBoardApplication.getInstance().getCreateMeetingList().remove(groupByMeetingId.getGroupName());
//                        }else{
//                            LogUtil.d(TAG,"leaveGroup"+groupByMeetingId.getGroupId());
//                            EMClient.getInstance().groupManager().leaveGroup(groupByMeetingId.getGroupId());//需异步处理
//                        }
//                    }catch (HyphenateException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();

//        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        alertDialog.setTitle(getString(R.string.app_name))
                .setMsg("是否退出会议")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Request.IINSTANCE.exit(1);
                        shapeHelper.clearByMeeting(Request.meetingId);
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == 100) {
            String path = data.getStringExtra("FILE_PATH");
            Glide.with(this).load("file:///" + path)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(1000, 1000) {

                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            if (resource == null) return;
                            Bitmap smallBitmapByY = BitmapUtils.getSmallBitmapByY(resource, displayInfoView.getMeasuredHeight() / 2.5);
                            String id= (int)System.currentTimeMillis()*1000+"";
                            String path = Request.meetingId +id ;
                            AbsShape shape = new PicShape(id , path);
                            if (currentPage.equals("-1")) {
                                currentPage = "0";
                            }
                            shape.setMeetingPage(currentPage);
                            shape.setMeetingId(Request.meetingId);
                            shape.setShapeType(Constants.SHAPE_PIC);
                            shape.onLayout(0, 0, smallBitmapByY.getWidth(), smallBitmapByY.getHeight());
                            WBImageLoader.getInstance().saveBitmap(smallBitmapByY, path);
                            displayInfoView.addShape(shape);
                            Request.IINSTANCE.sendNewShapeMsg(shape);
                            shapeHelper.insetShape(Request.meetingId, currentPage, shape.toShapeBean());
                        }
                    });

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }
}
