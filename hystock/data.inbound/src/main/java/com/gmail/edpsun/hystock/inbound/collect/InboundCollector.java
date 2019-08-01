package com.gmail.edpsun.hystock.inbound.collect;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
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
    private static final String URL_TO_GET_NAME = "https://hq.sinajs.cn/list=%s%s";

    @Autowired
    StockManager stockManger;

    @Resource(name = "hexunParser")
    Parser hexunParser;

    @Resource(name = "eastmoneyParser")
    Parser eastmoneyParser;

    @Autowired
    DataRetriever dataRetriever;

    public int process(InboundContext ctx) {
        LOGGER.info("start collecting. Path: " + ctx.getEbk());
        List<String> list = getStockList(ctx.getEbk());

        Parser parser = getParser(ctx);
        LOGGER.info("Parser: " + parser.getClass().getCanonicalName());

        ExecutorService executorService = Executors.newFixedThreadPool(ctx.getThreadNumber());

        AtomicInteger totalCounter = new AtomicInteger();
        AtomicInteger failureCounter = new AtomicInteger();

        int pp = 0;
        for (String id : list) {
            executorService.submit(new Collector(id, ctx, totalCounter, failureCounter));
        }

        try {
            executorService.shutdown();
            executorService.awaitTermination(10000000, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.info(
                "==========================================================" + "\n# Total  : " + totalCounter.get() + "\n"
                        + "# Failure: " + failureCounter.get());

        return totalCounter.get();
    }

    private Parser getParser(InboundContext ctx) {
        Parser p = hexunParser;
        if (ctx.getParser() != null) {
            if ("E".equals(ctx.getParser())) {
                p = eastmoneyParser;
            } else if ("H".equals(ctx.getParser())) {
                p = hexunParser;
            }
        }
        return p;
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

    private boolean processStock(String id, Parser parser) {
        String url = parser.getTargetURL(id);
        LOGGER.debug(url);
        boolean ret = false;
        try {
            String name = retrieveName(id);
            String content = dataRetriever.getData(url);
            Stock stock = parser.parse(id, name, content);
            stockManger.save(stock);
            ret = true;
        } catch (Exception ex) {
            LOGGER.error("[Error] id: " + id, ex);
        }
        return ret;
    }

    /*PACKAGE*/ String retrieveName(String id) {
        String stockIdPrefix = id.startsWith("6") ? "sh" : "sz";
        String stockUrl = String.format(URL_TO_GET_NAME, stockIdPrefix, id);
        String rawContent = dataRetriever.getData(stockUrl);

        if (rawContent.indexOf("\"\"") > -1) {
            throw new RuntimeException("cannot get stock name for " + id);
        }

        return rawContent.substring(rawContent.indexOf("=\"") + 2, rawContent.indexOf(","));
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

    class Collector implements Runnable {
        private String id;
        private InboundContext ctx;

        private AtomicInteger totalCounter;
        private AtomicInteger failureCounter;

        public Collector(String id, InboundContext ctx, AtomicInteger totalCounter, AtomicInteger failureCounter) {
            this.id = id;
            this.ctx = ctx;
            this.totalCounter = totalCounter;
            this.failureCounter = failureCounter;
        }

        public void run() {
            if (!isNeedProcess(id, ctx.getQuarter())) {
                return;
            }

            Random random = new Random();
            try {
                int count = totalCounter.getAndIncrement();
                LOGGER.info("[" + count + "] id: " + id + "-------------------------------------------");
                if (!processStock(id, getParser(ctx))) {
                    failureCounter.getAndIncrement();
                } else {
                    Thread.currentThread().sleep(random.nextInt(1000));
                }
            } catch (Exception ex) {
                LOGGER.error("[Error] id: " + id, ex);
            }
        }
    }
}
