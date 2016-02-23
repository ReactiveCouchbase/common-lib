package org.reactivecouchbase.functional;

import org.reactivecouchbase.common.Invariant;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class BiTry<T, E> {

    public abstract T getSuccess();

    public abstract E getFailure();

    public abstract Boolean isFailure();

    public abstract Boolean isSuccess();

    public static <A, B> BiTry<A, B> success(A a) {
        return new BiSuccess<>(a);
    }

    public static <A, B> BiTry<A, B> failure(B b) {
        return new BiFailure<>(b);
    }

    public BiTry<T, E> filter(Predicate<T> predicate) {
        Invariant.checkNotNull(predicate);
        if (isFailure()) {
            return new BiFailure<>(getFailure());
        } else {
            if (predicate.test(getSuccess())) {
                return new BiSuccess<>(getSuccess());
            } else {
                return new BiFailure<>(getFailure());
            }
        }
    }

    public <U> BiTry<U, E> flatMap(Function<T, BiTry<U, E>> call) {
        if (isFailure()) {
            return new BiFailure<>(getFailure());
        } else {
            return call.apply(getSuccess());
        }
    }

    public <U> void foreach(Function<T, U> call) {
        if (!isFailure()) {
            call.apply(getSuccess());
        }
    }

    public T getOrElse(T defaultValue) {
        if (isFailure()) {
            return defaultValue;
        } else {
            return getSuccess();
        }
    }

    public <U> BiTry<U, E> map(Function<T, U> call) {
        if (isFailure()) {
            return new BiFailure<>(getFailure());
        } else {
            return new BiSuccess<>(call.apply(getSuccess()));
        }
    }

    public BiTry<T, E> orElse(BiTry<T, E> t) {
        if (isFailure()) {
            return t;
        } else {
            return this;
        }
    }

    public <U> BiTry<U, E> recover(Function<E, U> call) {
        if (isFailure()) {
            U u = call.apply(getFailure());
            return new BiSuccess<>(u);
        } else {
            return new BiFailure<>(getFailure());
        }
    }

    public <U> BiTry<U, E> recoverWith(Function<E, BiTry<U, E>> call) {
        if (isFailure()) {
            return call.apply(getFailure());
        } else {
            return new BiFailure<>(getFailure());
        }
    }

    public Option<T> toOption() {
        return asSuccess();
    }

    public Option<E> asFailure() {
        if (isFailure()) {
            return Option.some(getFailure());
        }
        return Option.none();
    }

    public Option<T> asSuccess() {
        if (isFailure()) {
            return Option.none();
        }
        return Option.some(getSuccess());
    }

    public <U> BiTry<U, E> transform(Function<T, BiTry<U, E>> s, Function<E, BiTry<U, E>> f) {
        if (isSuccess()) {
            return s.apply(getSuccess());
        } else {
            return f.apply(getFailure());
        }
    }

    public <O> O fold(Function<E, O> errorHandler, Function<T, O> successHandler) {
        if (isSuccess()) {
            return errorHandler.apply(getFailure());
        } else {
            return successHandler.apply(getSuccess());
        }
    }
}
