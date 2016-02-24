package org.reactivecouchbase.functional;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Functions {

    private Functions() {}

    public static <T> Function<T, String> inputToString() {
        return input -> input == null ? "" : input.toString();
    }

    public static <T> Function<T, T> identity() {
        return t -> t;
    }

    public static <T> Function<Object, T> constant(final T elem) {
        return t -> elem;
    }

    public static Function<String, String> toUpperCase() {
        return String::toUpperCase;
    }

    public static Function<String, String> toLowerCase() {
        return String::toLowerCase;
    }

    public static <T> Function<T, Boolean> alwaysTrue() {
        return input -> true;
    }

    public static <T> Function<T, Boolean> alwaysFalse() {
        return input -> false;
    }

    public static <T> Predicate<T> toPredicate(final Function<T, Boolean> predicate) {
        if (predicate == null) return null;
        return predicate::apply;
    }

    public static <T> Function<T, Boolean> fromPredicate(final Predicate<T> predicate) {
        if (predicate == null) return null;
        return predicate::test;
    }

    public static <T> Consumer<T> toConsumer(final Function<T, Unit> f) {
        if (f == null) return null;
        return f::apply;
    }

    public static <T> Function<T, Unit> fromConsumer(final Consumer<T> action) {
        if (action == null) return null;
        return input -> {
            action.accept(input);
            return Unit.unit();
        };
    }
}
