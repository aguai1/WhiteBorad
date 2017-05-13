package cn.scooper.com.easylib.network.callback;

public abstract class ResultCallback<T> {
    public abstract void onResponse(Object res);

    public abstract void onFailure(Exception e);

    public abstract Class<T> getClazz();
}
