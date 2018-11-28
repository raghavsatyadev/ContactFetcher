package com.rocky.contactfetcher;

public interface ContactListener<T> {
    void onNext(T t);

    void onError(Throwable error);

    void onComplete();
}
