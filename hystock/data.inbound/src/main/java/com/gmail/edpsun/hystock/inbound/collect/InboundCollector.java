package com.gmail.edpsun.hystock.inbound.collect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.inbound.parser.Parser;
import com.gmail.edpsun.hystock.intf.AbstractProcessor;
import com.gmail.edpsun.hystock.manager.StockManager;
import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;
import com.gmail.edpsun.hystock.util.EBKUtil;

public class InboundCollector extends AbstractProcessor {
    public static Logger LOGGER = Logger.getLogger(InboundCollector.class);

    @Autowired
    StockManager stockManger;

    @Resource(name = "hexunParser")
    Parser hexunParser;

    @Autowired
    DataRetriever dataRetriever;

    @Override
    public int process(InboundContext ctx) {
        LOGGER.info("start collecting. Path: " + ctx.getEbk());
        List<String> list = getStockList(ctx.getEbk());

        Random random = new Random();
        int count = 0;
        int fail = 0;
        for (String id : list) {
            if (!isNeedProcess(id, ctx.getQuarter())) {
                continue;
            }

            try {
                count++;
                LOGGER.info("[" + count + "] id: " + id + "-------------------------------------------");
                if (!processStock(id)) {
                    fail++;
                } else {
                    Thread.currentThread().sleep(random.nextInt(1000));
                }
            } catch (Exception ex) {
                LOGGER.error("[Error] id: " + id, ex);
            }
        }

        LOGGER.info("==========================================================" + "\n# Total  : " + count + "\n"
                + "# Failure: " + fail);
        return count;
    }

    boolean isNeedProcess(String id, Quarter quarter) {
        List<HolderStat> holderStats = stockManger.getHolderStats(id);
        if (holderStats.size() == 0) {
            return true;
        }

        int q = quarter.getYear() * 100 + quarter.getQuarter();
        int q1 = holderStats.get(0).getYear() * 100 + holderStats.get(0).getQuarter();

        if (q > q1) {
            return true;
        } else {
            return false;
        }
    }

    private boolean processStock(String id) {
        String url = hexunParser.getTargetURL(id);
        LOGGER.debug(url);
        boolean ret = false;
        try {
            String content = dataRetriever.getData(url);
            Stock stock = hexunParser.parse(content);
            stockManger.save(stock);
            ret = true;
        } catch (Exception ex) {
            LOGGER.error("[Error] id: " + id, ex);
        }
        return ret;
    }

    public List<String> getStockList(String path) {
        return EBKUtil.getStockList(path);
    }

    public static class Quarter {
        private int year = -1;
        private int quarter = -1;

        private Quarter(int year, int quarter) {
            super();
            this.year = year;
            this.quarter = quarter;
        }

        public static Quarter valueOf(String q) {
            Validate.notNull(q);
            try {
                String[] s = q.split("-");
                return new Quarter(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
            } catch (Exception ex) {
                String msg = "Valid quarter format is like 2013-01. While the passed in is: " + q;
                throw new RuntimeException(msg, ex);
            }
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getQuarter() {
            return quarter;
        }

        public void setQuarter(int quarter) {
            this.quarter = quarter;
        }
    }
}
