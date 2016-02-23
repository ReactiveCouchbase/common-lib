package org.reactivecouchbase.functional;

public class Failure<T> extends Try<T> {

    public Failure(Throwable value) {
        super(null, value);
    }

    public Throwable throwable() {
        return e;
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
        return "Failure { " + e.getMessage() + " }";
    }
}
