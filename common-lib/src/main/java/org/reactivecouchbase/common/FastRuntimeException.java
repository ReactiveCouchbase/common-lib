package org.reactivecouchbase.common;

public class FastRuntimeException extends RuntimeException {

    public FastRuntimeException() {}

    public FastRuntimeException(String s) {
        super(s);
    }

    public FastRuntimeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FastRuntimeException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }
}
