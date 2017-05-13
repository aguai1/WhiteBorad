package cn.scooper.com.easylib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.easylib.utils.ToastUtils;

/**
 * Created by Aguai on 2016/11/20.
 * 网络连接是否具备
 */
public class NetworkManager extends BroadcastReceiver {
    private static final String TAG = NetworkManager.class.getSimpleName();

    private boolean connected = true;

    private Context mContext;

    public void startup(Context context) {
        mContext = context;
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        connected = info != null;
        //注册CONNECTIVITY_ACTION监听
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
    }

    public void shutdown(Context context) {
        try {
            context.unregisterReceiver(this);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (connected && (info == null || info.getState() == NetworkInfo.State.DISCONNECTED)) {
                ToastUtils.showShort("最遥远的距离就是没网，请检查设置！");
                connected = false;
            } else if (!connected && (info != null && info.getState() == NetworkInfo.State.CONNECTED)) {
                ToastUtils.showShort("网络连接恢复");
                connected = true;
            }
        }
    }
}

