package org.reactivecouchbase.functional;

public class Success<T> extends Try<T> {

    public Success(T value) {
        super(value, null);
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
        return "Success { " + value + " }";
    }
}