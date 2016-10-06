package org.reactivecouchbase.functional;

import java.util.function.Function;

public class Reader<A, B> {

    private final Function<A, B> function;

    private Reader(Function<A, B> f) {
        this.function = f;
    }

    public Function<A, B> function() {
        return function;
    }

    public static <A, B> Reader<A, B> of(Function<A, B> f) {
        return new Reader<>(f);
    }

    public static <A, B> Reader<A, B> constant(B b) {
        return of(a -> b);
    }

    public B run(A a) {
        return function.apply(a);
    }

    public <C> Reader<A, C> map(Function<B, C> f) {
        return of(function.andThen(f));
    }

    public <C> Reader<A, C> flatMap(Function<B, Reader<A, C>> f) {
        return of(a -> f.apply(function.apply(a)).run(a));
    }
}
