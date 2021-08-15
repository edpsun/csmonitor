package com.gmail.edpsun.tools.chain.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.gmail.edpsun.tools.chain.Intercepter;
import com.gmail.edpsun.tools.chain.InterceptorHandleFailure;

/**
 * Activity invocation handler used by intercepter of chain.
 *
 * @author sunp
 */
public class ActivityInvocationHandler implements InvocationHandler {
    /**
     * target object.
     */
    private Object target = null;
    /**
     * intercepter list.
     */
    private List<Intercepter> interceptors = new ArrayList<Intercepter>();

    /**
     * Initialize invocation handler.
     *
     * @param target target object
     */
    public ActivityInvocationHandler(final Object target) {
        this.target = target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(final Object proxy, final Method method,
            final Object[] args) throws Throwable {
        // extension point for inteceptor
        try {
            executeBeforeInterceptors(method, args);
            Object result = method.invoke(target, args);
            executeAfterInterceptors(method, args);
            return result;
        } catch (Throwable t) {
            executeOnExceptionInterceptors(method, args, t);
            throw t;
        }
    }

    /**
     * Call 'before' method for each intercepter.
     *
     * @param method method name
     * @param args arguments passed into method
     */
    private void executeBeforeInterceptors(final Method method,
            final Object[] args) {
        try {
            for (Intercepter interceptor : interceptors) {
                interceptor.before(target, method, args);
            }
        } catch (Exception e) {
            throw new InterceptorHandleFailure(
                    "Intercpetor before method throws Exception.", e);
        }
    }

    /**
     * Call 'after' method for each intercepter.
     *
     * @param method method name
     * @param args arguments passed into method
     */
    private void executeAfterInterceptors(final Method method,
            final Object[] args) {
        try {
            for (Intercepter interceptor : interceptors) {
                interceptor.after(target, method, args);
            }
        } catch (Exception e) {
            throw new InterceptorHandleFailure(
                    "Intercpetor after method throws Exception.", e);
        }
    }

    /**
     * Call 'onException' callback for each intercepter when run into exception.
     *
     * @param method    method name
     * @param args      arguments passed into method
     * @param throwable exception occurs when invoke cache.ccf chain.
     */
    private void executeOnExceptionInterceptors(final Method method,
            final Object[] args, final Throwable throwable) {
        try {
            for (Intercepter interceptor : interceptors) {
                interceptor.onException(target, method, args, throwable);
            }
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Runtime Exception is thrown from Intercpetor "
                    + "onException method.\n");
            msg.append("The chain treats it as an unrecoverable event.\n");
            msg.append("The stack trace of orginal exception from Activity:\n");
            msg.append("+++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            msg.append(getStackTrace(e));
            msg.append("+++++++++++++++++++++++++++++++++++++++++++++++++++");
            throw new InterceptorHandleFailure(
                    "Intercpetor after method throws Exception.", e);
        }
    }

    /**
     * Getter.
     *
     * @return intercepter list
     */
    public List<Intercepter> getInterceptors() {
        return interceptors;
    }

    /**
     * Setter.
     *
     * @param interceptors intercepter list
     */
    public void setInterceptors(final List<Intercepter> interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * Generate the current stack trace.
     *
     * @param t throwable
     * @return stack trace
     */
    private String getStackTrace(final Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}
