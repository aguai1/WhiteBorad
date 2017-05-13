package cn.scooper.com.easylib.network;


public interface ProgressResponseListener {
    void onResponseProgress(long bytesRead, long contentLength, boolean done);
}
