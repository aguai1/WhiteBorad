package cn.scooper.com.easylib.network.request;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.util.Map;

public class OkHttpPostRequest extends OkHttpRequest {

    public OkHttpPostRequest(String url, String tag, Map<String, String> parga, Map<String, String> headers) {
        super(url, null, parga, headers);
    }


    @Override
    protected Request buildRequest() {
        Request.Builder builder = new Request.Builder();
        appendHeaders(builder, mHeaders);
        builder.url(mUrl).tag(mTag).post(mRequestBody);
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


}
