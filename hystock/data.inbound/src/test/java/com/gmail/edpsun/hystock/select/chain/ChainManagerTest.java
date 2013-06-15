package com.gmail.edpsun.hystock.select.chain;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.intf.AbstractActivity;
import com.gmail.edpsun.tools.chain.Activity;
import com.gmail.edpsun.tools.chain.Chain;
import com.gmail.edpsun.tools.chain.NextStep;

public class ChainManagerTest {
    ChainManager mgr = new ChainManager();

    @Test
    public void testGetChain() throws Exception {
        ArrayList<Activity> list = new ArrayList<Activity>();
        list.add(new MyActivity("A", true));
        list.add(new MyActivity("B", false));
        list.add(new MyActivity("C", true));
        Chain chain = mgr.getChain(list);
        InboundContext ctx = new InboundContext();
        chain.execute(ctx);

        List<String> r = (List<String>) ctx.get("result");
        assertEquals(2, r.size());
        assertEquals("A", r.get(0));
        assertEquals("C", r.get(1));
    }
}

class MyActivity extends AbstractActivity {
    private String name = null;
    private boolean isExecute = true;

    public MyActivity(String name, boolean isExecute) {
        super();
        this.name = name;
        this.isExecute = isExecute;
    }

    @Override
    public boolean checkPreCondition(InboundContext ctx) throws Exception {
        return isExecute;
    }

    @Override
    public NextStep execute(InboundContext ctx) throws Exception {
        List<String> list = (List<String>) ctx.get("result");

        if (list == null) {
            list = new ArrayList<String>();
            ctx.put("result", list);
        }

        list.add(name);
        return NextStep.NEXT;
    }
}