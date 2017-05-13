package cn.scooper.com.easylib.network;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.scooper.com.easylib.network.callback.ResultCallback;
import cn.scooper.com.easylib.utils.JsonUtil;


public class OkHttpClientManager {

    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;

    public OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient();
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = JsonUtil.getParser();
        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 异步请求,使用okHttp的callback, 自己处理response
     */
    public void execute(Request request, Callback callback) {
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    public void execute(Request request, final ResultCallback callback) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String str = response.body().string();
                try {
                    final Object res = mGson.fromJson(str, callback.getClazz());
                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(res);
                        }
                    });
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(null);
                        }
                    });
                }
            }
        });
    }

    /**
     * 同步请求
     */
    public Response execute(Request request) throws IOException {
        Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    public void cancelTag(Object tag) {
        mOkHttpClient.cancel(tag);
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
