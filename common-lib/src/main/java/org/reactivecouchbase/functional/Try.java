package org.reactivecouchbase.functional;

import org.reactivecouchbase.common.Invariant;
import org.reactivecouchbase.common.Throwables;

import java.util.function.Function;

public abstract class Try<T> {

    protected final T value;

    protected final Throwable e;

    public Option<Throwable> error() {
        if (isFailure()) {
            return Option.some(e);
        } else {
            return Option.none();
        }
    }

    Try(T value, Throwable e) {
        this.value = value;
        this.e = e;
    }

    public static <T> Try<T> success(T t) {
        return new Success<T>(t);
    }

    public static <T> Try<T> failure(Throwable t) {
        return new Failure<T>(t);
    }

    public static <T> Try<T> apply(Function<Unit, T> call) {
        try {
            return new Success<>(call.apply(Unit.unit()));
        } catch (Exception e) {
            return new Failure<>(e);
        }
    }

    public Try<T> filter(Function<T, Boolean> predicate) {
        Invariant.checkNotNull(predicate);
        if (isFailure()) {
            return new Failure<T>(e);
        } else {
            if (predicate.apply(value)) {
                return new Success<>(value);
            } else {
                return new Failure<>(e);
            }
        }
    }

    public <U> Try<U> flatMap(Function<T, Try<U>> call) {
        if (isFailure()) {
            return new Failure<U>(e);
        } else {
            return call.apply(value);
        }
    }

    public <U> void foreach(Function<T, U> call) {
        if (isFailure()) {
            call.apply(value);
        }
    }

    public T get() {
        if (isFailure()) {
            throw Throwables.propagate(e);
        } else {
            return value;
        }
    }

    public T getOrElse(T defaultValue) {
        if (isFailure()) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public T getOrElse(Function<Unit, T> function) {
        if (isFailure()) {
            return function.apply(Unit.unit());
        }
        else return get();
    }

    public abstract Boolean isFailure();

    public abstract Boolean isSuccess();

    public <U> Try<U> map(Function<T, U> call) {
        if (isFailure()) {
            return new Failure<>(e);
        } else {
            return new Success<>(call.apply(value));
        }
    }

    public Try<T> orElse(Try<T> t) {
        if (isFailure()) {
            return t;
        } else {
            return this;
        }
    }

    public <U> Try<U> recover(Function<Throwable, U> call) {
        if (isFailure()) {
            U u = call.apply(e);
            if (u == null) {
                return new Failure<U>(new RuntimeException("Can't recover !!!"));
            } else {
                return new Success<U>(u);
            }
        } else {
            return new Failure<U>(e);
        }
    }

    public <U> Try<U> recoverWith(Function<Throwable, Try<U>> call) {
        if (isFailure()) {
            return call.apply(e);
        } else {
            return new Failure<U>(e);
        }
    }

    public Option<T> toOption() {
        return Option.apply(value);
    }

    public Option<Throwable> asFailure() {
        if (isFailure()) {
            return Option.some(e);
        }
        return Option.none();
    }

    public Option<T> asSuccess() {
        if (isFailure()) {
            return Option.none();
        }
        return Option.some(value);
    }

    public <U> Try<U> transform(Function<T, Try<U>> s, Function<Throwable, Try<U>> f) {
        if (isSuccess()) {
            return s.apply(value);
        } else {
            return f.apply(e);
        }
    }

    public <O> O fold(Function<Throwable, O> errorHandler, Function<T, O> successHandler) {
        if (isSuccess()) {
            return errorHandler.apply(e);
        } else {
            return successHandler.apply(value);
        }
    }
}