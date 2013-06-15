package com.gmail.edpsun.tools.chain;

/**
 * Interceptor handling failure.
 *
 * @author sunp
 *
 */
public class InterceptorHandleFailure extends Error {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Initializes a intercepter handling failure object.
     *
     * @param msg message for the failure
     */
    public InterceptorHandleFailure(final String msg) {
        super(msg);
    }

    /**
     * Initializes a intercepter handling failure object.
     *
     * @param msg message for the failure
     * @param t throwable object
     */
    public InterceptorHandleFailure(final String msg, final Throwable t) {
        super(msg, t);
    }
}
