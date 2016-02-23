package org.reactivecouchbase.functional;

import org.reactivecouchbase.common.Throwables;

import java.util.function.Function;

public interface UnsafeFunction<T, R> extends Function<T, R> {

    default R apply(T input) {
        try {
            return perform(input);
        } catch (Throwable e) {
            throw Throwables.propagate(e);
        }
    }

    R perform(T input) throws Throwable;
}
