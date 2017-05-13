package cn.scooper.com.easylib.network.request;

import android.text.TextUtils;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.Map;


public class OkHttpPutRequest extends OkHttpRequest {

    private String mDataType;

    private byte[] mDataByte;

    private File mDataFile;


    public OkHttpPutRequest(String url, final byte[] data, String dataType, Map<String, String> headers) {
        super(url, null, headers);
        mDataType = dataType;
        mDataByte = data;
    }

    public OkHttpPutRequest(String url, File data, String dataType, Map<String, String> headers) {
        super(url, null, headers);
        mDataType = dataType;
        mDataFile = data;
    }


    @Override
    protected Request buildRequest() {
        if (TextUtils.isEmpty(mUrl)) {
            throw new IllegalArgumentException("url can not be empty!");
        }

        mUrl = appendParams(mUrl, mParams);
        Request.Builder builder = new Request.Builder();

        appendHeaders(builder, mHeaders);
        builder.url(mUrl).tag(mTag).put(mRequestBody);
        return builder.build();
    }


    @Override
    protected RequestBody buildRequestBody() {
        RequestBody body = null;
        if (mDataByte != null) {
            body = RequestBody.create(MediaType.parse(mDataType), mDataByte);
        } else if (mDataFile != null) {
            body = RequestBody.create(MediaType.parse(mDataType), mDataFile);
        }
        return body;
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
