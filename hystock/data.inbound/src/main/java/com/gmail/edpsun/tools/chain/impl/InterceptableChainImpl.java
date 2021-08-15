package com.gmail.edpsun.tools.chain.impl;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gmail.edpsun.tools.chain.Activity;
import com.gmail.edpsun.tools.chain.Chain;
import com.gmail.edpsun.tools.chain.ErrorCallback;
import com.gmail.edpsun.tools.chain.Intercepter;
import com.gmail.edpsun.tools.chain.PreExecuteChecker;

/**
 * Interceptable chain Impl with AOP based intercepter support.
 *
 * @author sunp
 */
public class InterceptableChainImpl extends ChainImpl {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InterceptableChainImpl.class);
    /**
     * class list to be wrappered by dynamic proxy.
     */
    private static final Class< ? >[] CLASSES_TO_PROXY = new Class< ? >[] {Chain.class, Activity.class,
            PreExecuteChecker.class, ErrorCallback.class};
    /**
     * activity proxy list.
     */
    private final List<Activity> activityProxies = new ArrayList<Activity>();
    /**
     * itercepter list.
     */
    private List<Intercepter> intercepters = new ArrayList<Intercepter>();

    /**
     * default constructor.
     */
    public InterceptableChainImpl() {
        super();
    }

    /**
     * Initialize the chain with name.
     *
     * @param name chain name
     */
    public InterceptableChainImpl(final String name) {
        super(name);
    }

    /**
     * Protection shallow copy to work on cache.ccf new list object and generate dynamic
     * proxy for each object in CLASSES_TO_PROXY array.
     *
     * @return prepared activity list.
     */
    @Override
    protected synchronized List<Activity> prepareActivities() {
        if (activityProxies.size() != 0) {
            return activityProxies;
        }

        Activity proxy = null;
        ArrayList<Class< ? >> interfaces = new ArrayList<Class< ? >>();
        for (Activity a : getActivitiesDirectly()) {
            if (a == null) {
                throw new NullPointerException(CURRENT_CHAIN_CONTAINS_NULL_ACTIVITY);
            }

            interfaces.clear();
            for (Class< ? > c : CLASSES_TO_PROXY) {
                if (c.isInstance(a)) {
                    interfaces.add(c);
                }
            }
            proxy = (Activity) createProxy(interfaces.toArray(new Class< ? >[] {}), a);
            activityProxies.add(proxy);
        }
        return activityProxies;
    }

    /**
     * Logic to generate dynamic proxy.
     *
     * @param interfaces interfaces
     * @param obj target object
     * @return proxy proxy object
     */
    protected Object createProxy(final Class< ? >[] interfaces, final Object obj) {
        ActivityInvocationHandler invokeHandler = new ActivityInvocationHandler(obj);
        Object proxy = Proxy.newProxyInstance(obj.getClass().getClassLoader(), interfaces, invokeHandler);

        if (!(obj instanceof Chain)) {
            invokeHandler.setInterceptors(intercepters);
        }
        return proxy;
    }

    /**
     * Getter.
     *
     * @return intercepter list.
     */
    public List<Intercepter> getIntercepters() {
        return intercepters;
    }

    /**
     * Setter.
     *
     * @param intercepters intercepter list
     */
    public void setIntercepters(final List<Intercepter> intercepters) {
        this.intercepters = intercepters;
    }

}
