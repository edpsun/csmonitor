package com.gmail.edpsun.tools.chain;

/**
 * The interface for activity which is part of the responsibility chain impl.
 * @author sunp
 *
 */
public interface Activity {
    /**
     * The method contains the logic for current activity.
     * @param ctx context
     * @return next step enum value
     * @throws Exception exception raised
     */
    NextStep execute(Context ctx) throws Exception;
}
