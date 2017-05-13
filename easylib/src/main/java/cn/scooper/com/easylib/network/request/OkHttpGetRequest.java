package cn.scooper.com.easylib.network.request;

import android.text.TextUtils;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.util.Map;

public class OkHttpGetRequest extends OkHttpRequest {
    public OkHttpGetRequest(String url, String tag, Map<String, String> params, Map<String, String> headers) {
        super(url, tag, params, headers);
    }

    @Override
    protected Request buildRequest() {
        if (TextUtils.isEmpty(mUrl)) {
            throw new IllegalArgumentException("url can not be empty!");
        }
        //append params , if necessary
        mUrl = appendParams(mUrl, mParams);
        Request.Builder builder = new Request.Builder();
        //add headers , if necessary
        appendHeaders(builder, mHeaders);
        builder.url(mUrl).tag(mTag);
        return builder.build();
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
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
