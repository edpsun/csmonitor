package com.gmail.edpsun.tools.chain;

/**
 * The chain interface which is part of the responsibility chain impl.
 * @author sunp
 */
public interface Chain extends Activity {
    /**
     * Add activity instance into current chain.
     * @param command activity object
     */
    void addActivity(Activity command);
}
