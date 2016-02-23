package org.reactivecouchbase.common;

import org.reactivecouchbase.functional.Option;

import java.util.Iterator;
import java.util.function.Function;

public class Generator<T> implements Iterable<T> {

    private final Function<T, Option<T>> nextItem;
    private Option<T> internalState;

    private Generator(T from, Function<T, Option<T>> nextItem) {
        Invariant.checkNotNull(from);
        Invariant.checkNotNull(nextItem);
        this.nextItem = nextItem;
        this.internalState = Option.some(from);
    }

    private Generator(Function<T, Option<T>> nextItem) {
        Invariant.checkNotNull(nextItem);
        this.nextItem = nextItem;
        this.internalState = nextItem.apply(null);
    }

    public static <T> Generator<T> from(T from, Function<T, Option<T>> next) {
        return new Generator<>(from, next);
    }

    public static <T> Generator<T> of(Function<T, Option<T>> next) {
        return new Generator<>(next);
    }

    public static <T extends Number> Generator<T> numeric(T from, Function<T, Option<T>> next) {
        return new Generator<>(from, next);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return internalState.isDefined();
            }

            @Override
            public T next() {
                T value = internalState.get();
                internalState = nextItem.apply(value);
                return value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
