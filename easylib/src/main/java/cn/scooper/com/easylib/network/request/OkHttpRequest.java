package cn.scooper.com.easylib.network.request;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;

import cn.scooper.com.easylib.network.OkHttpClientManager;
import cn.scooper.com.easylib.network.callback.ResultCallback;


public abstract class OkHttpRequest {

    protected OkHttpClientManager mOkHttpClientManager = OkHttpClientManager.getInstance();

    protected RequestBody mRequestBody;
    protected Request mRequest;

    protected String mUrl;
    protected Object mTag;
    protected Map<String, String> mParams;
    protected Map<String, String> mHeaders;

    public OkHttpRequest(String url, Object tag,
                         Map<String, String> params, Map<String, String> headers) {
        mUrl = url;
        mTag = tag;
        mParams = params;
        mHeaders = headers;
    }


    public OkHttpRequest(String url, Object tag,
                         Map<String, String> headers) {
        mUrl = url;
        mTag = tag;
        mHeaders = headers;
    }


    /**
     * 构建request
     */
    protected abstract Request buildRequest();

    /**
     * 构建request body
     */
    protected abstract RequestBody buildRequestBody();

    protected void prepareInvoked() {
        mRequestBody = buildRequestBody();
        mRequest = buildRequest();
    }

    /**
     * 异步请求
     *
     * @param callback okHttpCallback
     */
    public OkHttpRequest execute(Callback callback) {
        prepareInvoked();
        mOkHttpClientManager.execute(mRequest, callback);
        return this;
    }

    /**
     * 异步请求
     */
    public OkHttpRequest execute(ResultCallback callback) {
        prepareInvoked();
        mOkHttpClientManager.execute(mRequest, callback);
        return this;
    }

    /**
     * 同步请求
     *
     * @return okHttpResponse
     * @throws IOException
     */
    public Response execute() throws IOException {
        prepareInvoked();
        return mOkHttpClientManager.execute(mRequest);
    }


    protected void appendHeaders(Request.Builder builder, Map<String, String> headers) {
        if (builder == null) {
            throw new IllegalArgumentException("builder can not be empty!");
        }

        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty())
            return;

        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }

    protected String getParamsEncoding() {
        return "utf-8";
    }

    protected String getBodyContentType() {
        return "application/x-www-form-urlencoded;charset=" + getParamsEncoding();
    }

    public OkHttpRequest setTag(Object tag) {
        mTag = tag;
        return this;
    }

    public void cancel() {
        mOkHttpClientManager.cancelTag(mTag);
    }


}
