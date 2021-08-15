package com.gmail.edpsun.tools.chain.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.gmail.edpsun.tools.chain.Activity;
import com.gmail.edpsun.tools.chain.Chain;
import com.gmail.edpsun.tools.chain.Context;
import com.gmail.edpsun.tools.chain.ErrorCallback;
import com.gmail.edpsun.tools.chain.ExceptionHandleFailure;
import com.gmail.edpsun.tools.chain.NextStep;
import com.gmail.edpsun.tools.chain.PreExecuteChecker;

/**
 * Chain impl.
 * <ul>
 * <li>Support sub-chain mixed with activities in chain
 * <li>Ability to skip rest activities on the same sub chain
 * <li>Support conditional branch chain.
 * <li>Error handling on exception
 * </ul>
 *
 * @author sunp
 *
 */
public class ChainImpl implements Chain, ErrorCallback {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ChainImpl.class);
    /**
     * Error message for null activity.
     */
    protected static final String CURRENT_CHAIN_CONTAINS_NULL_ACTIVITY =
            "Current chain contains NULL activity.";
    /**
     * Thread local object to save chain state for each thread.
     */
    protected static final ThreadLocal<Map<Chain, Stack<Activity>>> ACTIVITY_STACK_MAP =
            new ThreadLocal<Map<Chain, Stack<Activity>>>() {
                @Override
                protected Map<Chain, Stack<Activity>> initialValue() {
                    return new HashMap<Chain, Stack<Activity>>();
                }
            };
    /**
     * Chain id.
     */
    private final String id = UUID.randomUUID().toString();
    /**
     * Activities list in current chain.
     */
    private List<Activity> activities = new ArrayList<Activity>();
    /**
     * Chain name.
     */
    private String name = null;
    /**
     * is changeable.
     */
    private boolean isChangeable = true;

    /**
     * Default constructor.
     */
    public ChainImpl() {
        this.name = this.getClass().getSimpleName();
    }

    /**
     * Initialize chain impl with name.
     *
     * @param name chain name
     */
    public ChainImpl(final String name) {
        this.name = name;
    }

    /**
     * Getter.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter.
     *
     * @param name chain name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public NextStep execute(final Context ctx) throws Exception {
        isChangeable = false;

        if (ctx == null) {
            throw new NullPointerException(
                    "The passed in context object is NULL.");
        }

        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("Executing chain: \n" + dumpChain(false));
        // }

        Stack<Activity> activityStack = new Stack<Activity>();
        ACTIVITY_STACK_MAP.get().put(this, activityStack);

        NextStep nextStep = NextStep.NEXT;
        Exception exception = null;
        for (Activity activity : prepareActivities()) {
            if (activity == null) {
                throw new NullPointerException(
                        "Current chain contains NULL activity.");
            }

            try {
                activityStack.push(activity);
                // PreExecuteChecker
                if (activity instanceof PreExecuteChecker) {
                    PreExecuteChecker preChecker = (PreExecuteChecker) activity;
                    if (!preChecker.checkPreCondition(ctx)) {
                        // skip execute method invocation on current activity
                        continue;
                    }
                }

                // execute
                nextStep = activity.execute(ctx);

                if (nextStep == NextStep.SKIP_SUB_CHAIN
                        || nextStep == NextStep.END_CHAIN) {
                    break;
                }
            } catch (Exception e) {
                exception = e;
                break; // skip rest of activities
            }
        }

        // error handling
        if (exception != null) {
            onException(ctx, exception);
            // propagate the exception.
            throw exception;
        }

        if (nextStep == NextStep.END_CHAIN) {
            return NextStep.END_CHAIN;
        } else {
            return NextStep.NEXT;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onException(final Context ctx, final Exception e) {
        Stack<Activity> activityStack = ACTIVITY_STACK_MAP.get().get(this);

        if (e != null) {
            Activity activity = null;
            while (!activityStack.isEmpty()) {
                activity = activityStack.pop();
                if (activity instanceof Chain
                        && activity instanceof ErrorCallback) {
                    try {
                        ((ErrorCallback) activity).onException(ctx, e);
                    } catch (RuntimeException re) {
                        StringBuilder msg = new StringBuilder();
                        msg.append("Unexpected runtime exception is thrown from onException method.\n");
                        msg.append("The chain treats it as an unrecoverable event.\n");
                        msg.append("The stack trace of orginal exception from Activity:\n");
                        msg.append("+++++++++++++++++++++++++++++++++++++++\n");
                        msg.append(getStackTrace(e));
                        msg.append("+++++++++++++++++++++++++++++++++++++++");
                        throw new ExceptionHandleFailure(msg.toString(), re);
                    }
                } else if (activity instanceof ErrorCallback) {
                    ((ErrorCallback) activity).onException(ctx, e);
                }
            }
        }
    }

    /**
     * Get activities included by current chain. This method should NOT be used
     * by public purpose.
     *
     * @return Activity list
     */
    public synchronized List<Activity> getActivities() {
        List<Activity> list = new ArrayList<Activity>();
        if (activities.size() > 0) {
            list.addAll(activities);
        }
        return list;
    }

    /**
     * Get activities included by current chain directly.
     *
     * @return Activity list
     */
    protected List<Activity> getActivitiesDirectly() {
        return activities;
    }

    /**
     * Setter.
     *
     * @param activities activity chains.
     */
    public void setActivities(final List<Activity> activities) {
        if (!isChangeable) {
            throw new IllegalStateException(
                    "Chain is not changable after having been executed.");
        }
        this.activities = activities;
    }

    /**
     * Protection shallow copy to work on cache.ccf new list object.
     *
     * @return activity list
     */
    protected synchronized List<Activity> prepareActivities() {
        List<Activity> list = new ArrayList<Activity>();
        if (activities.size() > 0) {
            list.addAll(activities);
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addActivity(final Activity activity) {
        if (!isChangeable) {
            throw new IllegalStateException(
                    "Chain is not changable after having been executed.");
        }
        activities.add(activity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return dumpChain(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChainImpl)) {
            return false;
        }

        ChainImpl chain = (ChainImpl) o;
        return this.id.contentEquals(chain.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // CHECKSTYLE:OFF
        int hashCode = 19;
        hashCode += 31 * hashCode + id.hashCode();
        hashCode += 31 * hashCode + (name == null ? 0 : name.hashCode());
        // CHECKSTYLE:ON
        return hashCode;
    }

    /**
     * Dump the activities info for cache.ccf chain.
     *
     * @param isRecursive boolean to determine whether do it recursively
     * @return string
     */
    public String dumpChain(final boolean isRecursive) {
        return dumpChain(this, 0, isRecursive);
    }

    /**
     * Dump the activities info for cache.ccf chain.
     *
     * @param chain       chain
     * @param level       the level of current sub-chain
     * @param isRecursive boolean to determine whether do it recursively
     * @return string
     */
    private String dumpChain(final ChainImpl chain, final int level,
            final boolean isRecursive) {
        // CHECKSTYLE:OFF
        String indent =
                String.format((level == 0) ? "%s" : "%" + (level * 4) + "s", "");
        // CHECKSTYLE:ON
        String subIndent = "    ";
        StringBuilder sb = new StringBuilder();

        sb.append(getChainDesc(chain, indent));
        int p = 0;
        for (Activity activity : chain.getActivities()) {
            if (activity instanceof ChainImpl) {
                if (isRecursive) {
                    sb.append(dumpChain((ChainImpl) activity, level + 1,
                            isRecursive));
                } else {
                    sb.append(getChainDesc((ChainImpl) activity, subIndent
                            + indent));
                }
            } else {
                sb.append(indent).append(subIndent);
                if (activity == null) {
                    sb.append(String.format("[-] [ERROR] Activity is NULL\n"));
                } else {
                    sb.append(String.format("[-] Activity: %s\n", activity
                            .getClass().getCanonicalName()));
                }
            }
        }
        return sb.toString();
    }

    /**
     * Human readable info about the chain.
     *
     * @param chain chain
     * @param indence indent string
     * @return string
     */
    private String getChainDesc(final ChainImpl chain, final String indence) {
        StringBuilder sb = new StringBuilder();
        sb.append(indence).append(
                String.format("[+] Chain %s:  Size: %d  Impl: %s\n", chain
                        .getName(), chain.getActivities().size(), this
                        .getClass().getCanonicalName()));
        return sb.toString();
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

    /**
     * @return the isChangeable
     */
    public boolean isChangeable() {
        return isChangeable;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
}
