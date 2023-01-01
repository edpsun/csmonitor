package com.gmail.edpsun.hystock.inbound.collect;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.inbound.parser.ContextAware;
import com.gmail.edpsun.hystock.inbound.parser.Parser;
import com.gmail.edpsun.hystock.intf.AbstractProcessor;
import com.gmail.edpsun.hystock.manager.StockManager;
import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;
import com.gmail.edpsun.hystock.util.EBKUtil;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class InboundCollector extends AbstractProcessor {
    public static Logger LOGGER = Logger.getLogger(InboundCollector.class);
    private static final String URL_TO_GET_NAME = "https://hq.sinajs.cn/list=%s%s";

    @Autowired
    StockManager stockManger;

    @Resource(name = "hexunParser")
    Parser hexunParser;

    @Resource(name = "eastmoneyParser")
    Parser eastmoneyParser;

    @Resource(name = "jinRongJieParser")
    Parser jinRongJieParser;

    @Resource(name = "tdxParser")
    Parser tdxParser;

    @Resource(name = "HTTPDataRetriever")
    DataRetriever httpDataRetriever;

    @Resource(name = "CacheWrapperDataRetriever")
    DataRetriever cacheWrapperDataRetriever;

    @Override
    public int process(final InboundContext ctx) {
        LOGGER.info("start collecting. Path: " + ctx.getEbk());
        final List<String> list = getStockList(ctx.getEbk());

        final Parser parser = getParser(ctx);
        LOGGER.info("Parser: " + parser.getClass().getCanonicalName());

        final ExecutorService executorService = Executors.newFixedThreadPool(ctx.getThreadNumber());

        final AtomicInteger totalCounter = new AtomicInteger();
        final AtomicInteger failureCounter = new AtomicInteger();

        for (final String id : list) {
            LOGGER.info("  => Sub: " + id);
            executorService.submit(new Collector(id, ctx, totalCounter, failureCounter));
        }

        try {
            executorService.shutdown();
            executorService.awaitTermination(10000000, TimeUnit.HOURS);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.info(
                "==========================================================" + "\n# Total  : " + totalCounter.get() + "\n"
                        + "# Failure: " + failureCounter.get());

        return totalCounter.get();
    }

    private Parser getParser(final InboundContext ctx) {
        Parser p = hexunParser;
        if (ctx.getParser() != null) {
            if ("E".equals(ctx.getParser())) {
                p = eastmoneyParser;
            } else if ("H".equals(ctx.getParser())) {
                p = hexunParser;
            } else if ("J".equals(ctx.getParser())) {
                p = jinRongJieParser;
            } else if ("T".equals(ctx.getParser())) {
                p = tdxParser;
            }
            if(p instanceof ContextAware){
                ((ContextAware) p).setContext(ctx);
            }
        }
        return p;
    }

    boolean isNeedProcess(final String id, final Quarter quarter) {
        final List<HolderStat> holderStats = stockManger.getHolderStats(id);
        if (holderStats.size() == 0) {
            return true;
        }

        final int q = quarter.getYear() * 100 + quarter.getQuarter();
        final int q1 = holderStats.get(0).getYear() * 100 + holderStats.get(0).getQuarter();

        if (q > q1) {
            return true;
        } else {
            return false;
        }
    }

    private boolean processStock(final String id, final Parser parser) {
        final String url = parser.getTargetURL(id);
        LOGGER.debug(url);
        boolean ret = false;
        try {
            final String name = retrieveName(id);
            final String content = cacheWrapperDataRetriever.getData(url, parser.getEncoding());
            final Stock stock = parser.parse(id, name, content);
            stockManger.save(stock);
            ret = true;
        } catch (final Exception ex) {
            LOGGER.error("[Error] id: " + id, ex);
        }
        return ret;
    }

    /*PACKAGE*/ String retrieveName(final String id) {
        final String stockIdPrefix = id.startsWith("6") ? "sh" : "sz";
        final String stockUrl = String.format(URL_TO_GET_NAME, stockIdPrefix, id);
        final String rawContent = httpDataRetriever.getData(stockUrl);

        if (rawContent.indexOf("\"\"") > -1) {
            throw new RuntimeException("cannot get stock name for " + id);
        }

        return rawContent.substring(rawContent.indexOf("=\"") + 2, rawContent.indexOf(","));
    }

    public List<String> getStockList(final String path) {
        return EBKUtil.getStockList(path);
    }

    public static class Quarter {
        private int year = -1;
        private int quarter = -1;

        private Quarter(final int year, final int quarter) {
            super();
            this.year = year;
            this.quarter = quarter;
        }

        public static Quarter valueOf(final String q) {
            Validate.notNull(q);
            try {
                final String[] s = q.split("-");
                return new Quarter(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
            } catch (final Exception ex) {
                final String msg = "Valid quarter format is like 2013-01. While the passed in is: " + q;
                throw new RuntimeException(msg, ex);
            }
        }

        public int getYear() {
            return year;
        }

        public void setYear(final int year) {
            this.year = year;
        }

        public int getQuarter() {
            return quarter;
        }

        public void setQuarter(final int quarter) {
            this.quarter = quarter;
        }
    }

    class Collector implements Runnable {
        private final String id;
        private final InboundContext ctx;

        private final Integer sleepTime;

        private final AtomicInteger totalCounter;
        private final AtomicInteger failureCounter;

        private final Random random = new Random();

        public Collector(final String id, final InboundContext ctx, final AtomicInteger totalCounter, final AtomicInteger failureCounter) {
            this.id = id;
            this.ctx = ctx;
            this.totalCounter = totalCounter;
            this.failureCounter = failureCounter;
            this.sleepTime = ctx.getSleepTime();
        }

        @Override
        public void run() {
            if (!isNeedProcess(id, ctx.getQuarter())) {
                return;
            }

            try {
                final int count = totalCounter.getAndIncrement();
                LOGGER.info("[" + count + "] id: " + id + "-------------------------------------------");
                if (!processStock(id, getParser(ctx))) {
                    failureCounter.getAndIncrement();
                }
            } catch (final Exception ex) {
                LOGGER.error("[Error] id: " + id, ex);
            } finally {
                try {
                    final int millis = this.sleepTime + random.nextInt(1000);
                    System.out.println("I am sleeping(millis):" + millis);
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
