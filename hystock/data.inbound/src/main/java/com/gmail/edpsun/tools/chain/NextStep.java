package com.gmail.edpsun.tools.chain;

/**
 * enum to indicate what the chain does for next step.
 *
 * @author sunp
 *
 */
public enum NextStep {
    /**
     * continue to next activity.
     */
    NEXT,
    /**
     * skip the whole chain.
     */
    END_CHAIN,
    /**
     * skip current sub-chain.
     */
    SKIP_SUB_CHAIN;
}
