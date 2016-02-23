package org.reactivecouchbase.functional;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Lens<A, B> {

    private final Function<A, B> getter;
    private final BiFunction<A, B, A> setter;

    public Lens(Function<A, B> getter, BiFunction<A, B, A> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public static <A, B> Lens<A, B> of(Function<A, B> getter, BiFunction<A, B, A> setter) {
        return new Lens<>(getter, setter);
    }

    public B get(A target) {
        return getter.apply(target);
    }

    public void set(final A target, final B value) {
        modify(ignore -> value).apply(target);
    }

    public Function<B, A> set(final A target) {
        return b -> Lens.this.modify(ignore -> b).apply(target);
    }

    public Function<A, A> modify(final Function<B, B> mapper) {
        return oldValue -> {
            B extracted = getter.apply(oldValue);
            B transformed = mapper.apply(extracted);
            return setter.apply(oldValue, transformed);
        };
    }

    public Function<Function<B, B>, A> modify(final A oldValue) {
        return mapper -> {
            B extracted = getter.apply(oldValue);
            B transformed = mapper.apply(extracted);
            return setter.apply(oldValue, transformed);
        };
    }

    public <C> Lens<A, C> compose(final Lens<B, C> other) {
        return new Lens<>(
            a -> other.getter.apply(getter.apply(a)),
            (a, c) -> {
                B b = getter.apply(a);
                B newB = other.modify(ignored -> c).apply(b);
                return setter.apply(a, newB);
            }
        );
    }
}