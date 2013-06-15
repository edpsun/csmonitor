package com.gmail.edpsun.hystock.select.chain;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.intf.AbstractActivity;
import com.gmail.edpsun.hystock.select.analyze.HolderDataCalculator;
import com.gmail.edpsun.tools.chain.NextStep;

public class HolderDataCalculatorActivity extends AbstractActivity {

    @Override
    public NextStep execute(InboundContext ctx) throws Exception {
        HolderDataCalculator cal = new HolderDataCalculator();
        cal.analyze(ctx);
        return NextStep.NEXT;
    }
}
