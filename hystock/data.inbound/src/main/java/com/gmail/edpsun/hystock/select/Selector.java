package com.gmail.edpsun.hystock.select;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.intf.AbstractProcessor;
import com.gmail.edpsun.hystock.manager.StockManager;
import com.gmail.edpsun.hystock.model.AnalyzeVO;
import com.gmail.edpsun.hystock.model.Stock;
import com.gmail.edpsun.hystock.select.chain.ChainManager;
import com.gmail.edpsun.hystock.util.EBKUtil;
import com.gmail.edpsun.tools.chain.Chain;

public class Selector extends AbstractProcessor {
    public static Logger LOGGER = Logger.getLogger(Selector.class);
    @Autowired
    StockManager stockManger;

    @Override
    public int process(InboundContext ctx) {
        LOGGER.info("start processing. Path: " + ctx.getEbk());
        List<String> list = EBKUtil.getStockList(ctx.getEbk());

        int count = 0;
        int fail = 0;

        for (String id : list) {
            try {
                count++;
                System.out.println("[" + count
                        + "]=============================================================================");

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

    boolean process(String id) {
        Stock stock = stockManger.getStock(id, true);
        if (stock == null) {
            LOGGER.error("Cannot find stock id :" + id);
            return false;
        }

        LOGGER.info(String.format("Processing Stock: %s    Name: %s    Quanter Count: %d", stock.getId(),
                stock.getName(), stock.getHolderStats().size()));
        Chain chain = new ChainManager().getChain();
        InboundContext ctx = new InboundContext();
        ctx.setStock(stock);
        ctx.setAnalyzeVO(new AnalyzeVO(id));

        try {
            chain.execute(ctx);
        } catch (Exception e) {
            throw new RuntimeException("Error while processing stock: " + id, e);
        }
        return true;
    }
}
