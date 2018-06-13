package net.androidcart.androidutils.eventbus;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Amin Amini on 5/28/18.
 */

public class RxBus<T> {

    private PublishSubject<T> subject ;
    private Map<Object, CompositeDisposable> sSubscriptionsMap = new HashMap<>();

    public RxBus() {
        subject = PublishSubject.create();
        subject.subscribeOn(AndroidSchedulers.mainThread());
    }

    private CompositeDisposable getCompositeDisposable(@NonNull Object object) {
        CompositeDisposable compositeDisposable = sSubscriptionsMap.get(object);
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
            sSubscriptionsMap.put(object, compositeDisposable);
        }

        return compositeDisposable;
    }

    public void subscribe(@NonNull Consumer<T> action) {
        Disposable disposable = subject.subscribe(action);
        getCompositeDisposable(action).add(disposable);
    }
    public void unregister(@NonNull Consumer<T> action) {

        //We have to remove the composition from the map, because once you dispose it can't be used anymore
        CompositeDisposable compositeDisposable = sSubscriptionsMap.remove(action);
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
    public void publish(@NonNull T message) {
        subject.onNext(message);
    }
}
