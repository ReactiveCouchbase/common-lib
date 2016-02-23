package org.reactivecouchbase.functional;


import java.util.function.Function;

public interface Action<T> extends Function<T, Void> {

    default Void apply(T t) {
        call(t);
        return null;
    }

    void call(T t);
}