package net.chris.ui.slick.comm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


public class RxSubject<T> {

    private final Subject<T> subject;

    public RxSubject() {
        this.subject = (Subject<T>) PublishSubject.create().toSerialized();
    }

    @NonNull
    final public Observable<T> listen() {
        return subject;
    }

    public void post(@Nullable final T data) {
        if (subject.hasObservers()) {
            subject.onNext(data);
        }
    }
}
