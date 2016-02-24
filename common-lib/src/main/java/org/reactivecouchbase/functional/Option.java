package org.reactivecouchbase.functional;


import org.reactivecouchbase.common.Invariant;
import org.reactivecouchbase.common.Throwables;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Option<T> implements Iterable<T>, Serializable {

    public enum Type {
        SOME, NONE
    }

    public abstract boolean isDefined();

    public abstract boolean isEmpty();

    public abstract T get();

    public abstract Type type();

    public abstract Iterable<T> asSome();

    public abstract Iterable<Unit> asNone();

    public <V> Option<V> as(final Class<V> clazz) {
        if (isDefined()) {
            return this.map(clazz::cast);
        }
        return Option.none();
    }

    public Option<T> orElse(T value) {
        if (isEmpty()) {
            return Option.apply(value);
        }
        else return this;
    }

    public Option<T> orElse(Supplier<T> value) {
        if (isEmpty()) {
            return Option.apply(value.get());
        }
        else return this;
    }

    public Option<T> orElse(Option<T> value) {
        if (isEmpty()) {
            return value;
        }
        else return this;
    }

    public T getOrElse(T value) {
        if (isEmpty()) {
            return value;
        }
        else return get();
    }

    public T getOrElse(Supplier<T> function) {
        if (isEmpty()) {
            return function.get();
        }
        else return get();
    }

    public T getOrThrow(Throwable t) {
        if (isEmpty()) {
            throw Throwables.propagate(t);
        } else {
            return get();
        }
    }

    public void foreach(Function<T, ?> f) {
        if (!isEmpty()) {
            f.apply(get());
        }
    }

    public Boolean forall(Function<? super T, Boolean> f) {
        return isEmpty() || f.apply(get());
    }

    public Boolean exists(Function<? super T, Boolean> f) {
        return !isEmpty() && f.apply(get());
    }

    public int count(Predicate<T> predicate) {
        return filter(predicate).isDefined() ? 1 : 0;
    }

    public boolean exist(Predicate<T> predicate) {
        return filter(predicate).isDefined();
    }

    public T getOrNull() {
        if (isEmpty()) {
            return null;
        }
        else return get();
    }

    public Option<T> filter(Predicate<? super T> predicate) {
        if (isDefined()) {
            if (predicate.test(get())) {
                return this;
            } else {
                return Option.none();
            }
        }
        return Option.none();
    }

    public Option<T> filterNot(Predicate<? super T> predicate) {
        if (isDefined()) {
            if (!predicate.test(get())) {
                return this;
            } else {
                return Option.none();
            }
        }
        return Option.none();
    }

    /**
     * Pattern matching like (with extraction) on clazz
     */
    public <O> Option<T> notMatch(Class<O> clazz) {
        if (isDefined()) {
            T obj = get();
            if (clazz.isAssignableFrom(obj.getClass())) {
                return Option.none();
            } else {
                return Option.some(get());
            }
        } else {
            return Option.none();
        }
    }

    /**
     * Pattern matching like (with extraction) on clazz
     */
    public <O> Option<O> match(Class<O> clazz) {
        if (isDefined()) {
            T obj = get();
            if (clazz.isAssignableFrom(obj.getClass())) {
                return Option.some(clazz.cast(obj));
            } else {
                return Option.none();
            }
        } else {
            return Option.none();
        }
    }

    /**
     * Pattern matching like (with extraction) on clazz and predicate
     */
    public <O> Option<O> match(Class<O> clazz, Function<O, Boolean> predicate) {
        if (isDefined()) {
            T obj = get();
            if (clazz.isAssignableFrom(obj.getClass())) {
                O newObj = clazz.cast(obj);
                if (predicate.apply(newObj)) {
                    return Option.some(newObj);
                } else {
                    return Option.none();
                }
            } else {
                return Option.none();
            }
        } else {
            return Option.none();
        }
    }

    public <R> Option<R> map(Function<? super T, ? extends R> function) {
        if (isDefined()) {
            return Option.apply(function.apply(get()));
        }
        return Option.none();
    }

    public Option<T> map(Callable<T> function) {
        if (isDefined()) {
            try {
                return Option.apply(function.call());
            } catch (Exception e) {
                return Option.none();
            }
        }
        return Option.none();
    }

    public <R> Option<R> flatMap(Callable<Option<R>> action) {
        if (isDefined()) {
            try {
                return action.call();
            } catch (Exception e) {
                return Option.none();
            }
        }
        return Option.none();
    }

    public <R> Option<R> flatMap(Function<? super T, Option<R>> action) {
        if (isDefined()) {
            return action.apply(get());
        }
        return Option.none();
    }

    public Optional<T> toJdkOptional() {
        if (isDefined()) {
            return Optional.of(get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns the globally defined None object
     */
    @SuppressWarnings("unchecked")
    public static <T> None<T> none() {
        return (None<T>) None.NONE_INSTANCE;
    }

    /**
     * Create a new Some object containing the object 'value'
     */
    public static <T> Some<T> some(T value) {
        return new Some<>(value);
    }

    /**
     * Create an object None if value == null else a Some object
     */
    public static <T> Option<T> apply(T value) {
        if (value == null) {
            return Option.none();
        } else {
            return Option.some(value);
        }
    }

    public static <T> Option<T> fromJdkOptional(Optional<T> optional) {
        if (optional.isPresent()) {
            return apply(optional.get());
        } else {
            return none();
        }
    }

    /**
     * Some if match clazz and predicate
     */
    public static <A> Option<A> matching(final Object value, Class<A> clazz, Predicate<A> predicate) {
        if (value == null) {
            return none();
        }
        Invariant.checkNotNull(clazz);
        Invariant.checkNotNull(predicate);
        if (clazz.isAssignableFrom(value.getClass())) {
            A actual = clazz.cast(value);
            if (predicate.test(actual)) {
                return some(actual);
            } else {
                return none();
            }
        }
        return none();
    }

    /**
     * Some if match clazz
     */
    public static <T> Option<T> of(final Object value, final Class<T> clazz) {
        if (value == null) {
            return none();
        }
        Invariant.checkNotNull(clazz);
        if (clazz.isAssignableFrom(value.getClass())) {
            return some(clazz.cast(value));
        }
        return none();
    }

    /**
     * Some if match predicate
     */
    public static <T> Option<T> matching(T value, Predicate<T> predicate) {
        if (value == null) {
            return none();
        }
        Invariant.checkNotNull(predicate);
        if (predicate.test(value)) {
            return some(value);
        }
        return none();
    }

    public <O> O fold(Supplier<O> errorHandler, Function<T, O> successHandler) {
        if (isEmpty()) {
            return errorHandler.get();
        } else {
            return successHandler.apply(get());
        }
    }

    public T fold(Supplier<T> errorHandler) {
        if (isEmpty()) {
            return errorHandler.get();
        } else {
            return get();
        }
    }
}