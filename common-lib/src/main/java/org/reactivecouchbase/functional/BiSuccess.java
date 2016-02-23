package org.reactivecouchbase.functional;

public class BiSuccess<T, E> extends BiTry<T, E> {

    private final T success;

    public BiSuccess(T success) {
        this.success = success;
    }

    @Override
    public Boolean isFailure() {
        return false;
    }

    @Override
    public Boolean isSuccess() {
        return true;
    }

    @Override
    public String toString() {
        return "Success { " + success + " }";
    }

    @Override
    public T getSuccess() {
        return success;
    }

    @Override
    public E getFailure() {
        throw new RuntimeException("Not a BiFailure " + toString() + " !!!");
    }
}