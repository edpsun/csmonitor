package com.gmail.edpsun.hystock.select.chain;

import java.util.ArrayList;
import java.util.List;

import com.gmail.edpsun.tools.chain.Activity;
import com.gmail.edpsun.tools.chain.Chain;
import com.gmail.edpsun.tools.chain.impl.InterceptableChainImpl;

public class ChainManager {
    public Chain getChain() {
        ArrayList<Activity> list = new ArrayList<Activity>();
        list.add(new HolderDataCalculatorActivity());
        return getChain(list);
    }

    public Chain getChain(List<Activity> list) {
        InterceptableChainImpl chain = new InterceptableChainImpl();
        chain.setActivities(list);
        return chain;
    }
}
