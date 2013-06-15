package com.gmail.edpsun.hystock.select.chain;

import java.util.ArrayList;
import java.util.List;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.intf.AbstractActivity;
import com.gmail.edpsun.hystock.select.analyze.HolderDataAnalyzer;
import com.gmail.edpsun.tools.chain.NextStep;

public class HolderDataAnalyzerActivity extends AbstractActivity {

    @Override
    public NextStep execute(InboundContext stockCtx) throws Exception {
        HolderDataAnalyzer dataAnalyzer = new HolderDataAnalyzer();

        InboundContext globalContext = (InboundContext) stockCtx.get(InboundContext.PARAM_GLOBAL_CONTEXT);
        stockCtx.setKeepAll(globalContext.getKeepAll());

        if (dataAnalyzer.analyze(stockCtx)) {
            globalContext.getChosenList().add(stockCtx);
        }

        return NextStep.NEXT;
    }
}
