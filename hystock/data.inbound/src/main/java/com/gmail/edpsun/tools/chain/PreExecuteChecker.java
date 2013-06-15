package com.gmail.edpsun.tools.chain;

/**
 * Pre-execute checker interface which is invoked before activity and indicate
 * whether the prerequisite for activity is satisfied.
 *
 * @author sunp
 */
public interface PreExecuteChecker {
    /**
     * This method is implemented by activity to indicate whether to execute
     * current activity.
     *
     * @param ctx context
     * @return boolean true - execute current activity and false - skip current
     *         activity
     * @throws Exception exception while doing pre-checking
     */
    boolean checkPreCondition(Context ctx) throws Exception;
}
