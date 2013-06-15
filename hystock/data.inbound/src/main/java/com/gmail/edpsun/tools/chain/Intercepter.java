package com.gmail.edpsun.tools.chain;

import java.lang.reflect.Method;

/**
 * The intercepter interface used with {@link InterceptableChainImpl}.
 *
 * @author sunp
 */
public interface Intercepter {
    /**
     * Method to be called before invoking an Activity object.
     *
     * @param target the underlying target object
     * @param method the method to be called
     * @param args the arguments passed into the method.
     */
    void before(Object target, Method method, Object[] args);

    /**
     * Method to be called after invoking an Activity object.
     *
     * @param target the underlying target object
     * @param method the method to be called
     * @param args the arguments passed into the method.
     */
    void after(Object target, Method method, Object[] args);

    /**
     * Method to be called when running into exception while invoking an
     * Activity object.
     *
     * @param target the underlying target object
     * @param method the method to be called
     * @param args the arguments passed into the method.
     * @param throwable the exception occurs in executing an activity object.
     */
    void onException(Object target, Method method, Object[] args,
            Throwable throwable);
}
