package com.gmail.edpsun.hystock.intf;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.tools.chain.Activity;
import com.gmail.edpsun.tools.chain.Context;
import com.gmail.edpsun.tools.chain.NextStep;
import com.gmail.edpsun.tools.chain.PreExecuteChecker;

public abstract class AbstractActivity implements Activity, PreExecuteChecker {

    @Override
    public boolean checkPreCondition(Context ctx) throws Exception {
        return checkPreCondition((InboundContext) ctx);
    }

    public boolean checkPreCondition(InboundContext ctx) throws Exception {
        return true;
    }

    @Override
    public NextStep execute(Context ctx) throws Exception {
        return execute((InboundContext) ctx);
    }

    public abstract NextStep execute(InboundContext ctx) throws Exception;
}
