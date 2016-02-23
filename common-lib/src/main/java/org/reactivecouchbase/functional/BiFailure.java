package org.reactivecouchbase.functional;

public class BiFailure<T, E> extends BiTry<T, E> {

    private final E failure;

    public BiFailure(E failure) {
        this.failure = failure;
    }

    @Override
    public Boolean isFailure() {
        return true;
    }

    @Override
    public Boolean isSuccess() {
        return false;
    }

    @Override
    public String toString() {
        return "BiFailure { " + failure + " }";
    }

    @Override
    public T getSuccess() {
        throw new RuntimeException("Not a BiSuccess " + toString() + " !!!");
    }

    @Override
    public E getFailure() {
        return failure;
    }
}