package cn.scooper.com.easylib.network.request;

import android.text.TextUtils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.util.Map;


public class OkHttpDeleteRequest extends OkHttpRequest {

    public OkHttpDeleteRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers) {
        super(url, tag, params, headers);
    }


    @Override
    protected Request buildRequest() {
        if (TextUtils.isEmpty(mUrl)) {
            throw new IllegalArgumentException("url can not be empty!");
        }
        Request.Builder builder = new Request.Builder();
        appendHeaders(builder, mHeaders);

        //如果没有RequestBody,就把参数加在url后面
        if (mRequestBody == null) {
            mUrl = appendParams(mUrl, mParams);
            builder.url(mUrl).tag(mTag).delete();
        } else {
            builder.url(mUrl).tag(mTag).delete(mRequestBody);
        }

        return builder.build();
    }

    @Override
    protected RequestBody buildRequestBody() {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

        if (mParams != null && !mParams.isEmpty()) {
            for (String key : mParams.keySet()) {
                formEncodingBuilder.add(key, mParams.get(key));
            }
        }
        RequestBody formBody = formEncodingBuilder.build();
        return formBody;
    }

    @Override
    protected String getBodyContentType() {
        return "application/json;charset=" + getParamsEncoding();
    }

    private String appendParams(String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url + "?");
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                sb.append(key).append("=").append(params.get(key)).append("&");
            }
        }

        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
