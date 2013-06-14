package com.gmail.edpsun.hystock.select;

import java.util.List;

import org.apache.log4j.Logger;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.intf.AbstractProcessor;
import com.gmail.edpsun.hystock.util.EBKUtil;

public class Selector extends AbstractProcessor {
    public static Logger LOGGER = Logger.getLogger(Selector.class);

    @Override
    public int process(InboundContext ctx) {
        LOGGER.info("start processing. Path: " + ctx.getEbk());
        List<String> list = EBKUtil.getStockList(ctx.getEbk());

        int count = 0;
        int fail = 0;

        for (String id : list) {
            try {
                count++;
                if (!process(id)) {
                    fail++;
                } else {
                    Thread.currentThread().sleep(10);
                }
            } catch (Exception ex) {
                LOGGER.error("[Error] id: " + id, ex);
            }
        }
        return 0;
    }

    private boolean process(String id) {
        System.out.println("Id: " + id);
        return false;
    }
}
