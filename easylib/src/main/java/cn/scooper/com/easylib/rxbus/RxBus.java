package cn.scooper.com.easylib.rxbus;


import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {
    private static RxBus mInstance;

    private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());

    public static RxBus get() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    public void send(Object o) {
        mBus.onNext(o);
    }

    public <T extends Event> Observable<T> toObservable(final Class<T> type) {
        return mBus.filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object o) {
                return type.isInstance(o);
            }
        }).cast(type);
    }
}
