package org.reactivecouchbase.common;

import org.reactivecouchbase.functional.Option;

/**
 * Mutable non atomic reference. Useful to handle `final` fields in lambda context.
 */
public class Holder<T> {

    private T value;

    Holder(T value) {
        this.value = value;
    }

    public static <T> Holder<T> of(T value) {
        return new Holder<T>(value);
    }

    public static <T> Holder<T> ofNull() {
        return new Holder<T>(null);
    }

    public static <T> Holder<T> ofNull(Class<T> clazz) {
        return new Holder<T>(null);
    }

    public T get() {
        return value;
    }

    public Option<T> getOpt() {
        return Option.apply(value);
    }

    public void set(T value) {
        this.value = value;
    }
}
