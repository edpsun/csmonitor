package com.gmail.edpsun.tools.chain;

/**
 * When an Activity impl also implements this call back interface, the chain
 * invokes the onException method when an exception occurs on the chain.
 *
 * @author sunp
 */
public interface ErrorCallback {
    /**
     * when there is any activity in the chain throwing exception, each of the
     * activity will be notified by calling the onException method in reverse
     * order which means last activity will get the notification first.
     *
     * @param ctx context ojbect
     * @param e the exception object passed in by chian impl.
     */
    void onException(final Context ctx, final Exception e);
}
