package org.reactivecouchbase.common;

public class FastException extends Exception {

    public FastException() {}

    public FastException(String s) {
        super(s);
    }

    public FastException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FastException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }
}
