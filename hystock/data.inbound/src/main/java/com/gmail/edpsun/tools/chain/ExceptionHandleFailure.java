package com.gmail.edpsun.tools.chain;

/**
 * This class is thrown when catching RuntimeException from
 * ErrorCallback.onException. It means while handling activity exception, an
 * unexpected runtime exception is thrown from ErrorCallback. The chain regards
 * it as an unrecoverable event.
 *
 * @author sunp
 */
public class ExceptionHandleFailure extends Error {
    /**
     * serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Initializes cache.ccf exception handling failure object.
     *
     * @param msg message for the failure
     */
    public ExceptionHandleFailure(final String msg) {
        super(msg);
        new String("");
    }

    /**
     * Initializes cache.ccf exception handling failure object.
     *
     * @param msg message for the failure
     * @param t   throwable object
     */
    public ExceptionHandleFailure(final String msg, final Throwable t) {
        super(msg, t);
    }
}
