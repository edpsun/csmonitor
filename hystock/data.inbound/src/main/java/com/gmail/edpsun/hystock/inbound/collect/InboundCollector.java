package com.gmail.edpsun.hystock.inbound.collect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.edpsun.hystock.inbound.parser.Parser;
import com.gmail.edpsun.hystock.manager.StockManager;
import com.gmail.edpsun.hystock.model.Stock;

public class InboundCollector {
    public static Logger LOGGER = Logger.getLogger(InboundCollector.class);

    @Autowired
    StockManager stockManger;

    @Resource(name = "hexunParser")
    Parser hexunParser;

    @Autowired
    DataRetriever dataRetriever;

    public void collect(String path) {
        Random random = new Random();

        LOGGER.info("start collecting. Path: " + path);
        List<String> list = getStockList(path);

        int count = 0;
        int fail = 0;
        for (String id : list) {
            count++;
            String url = hexunParser.getTargetURL(id);
            LOGGER.debug(url);

            try {
                LOGGER.info("[Start] id: " + id + "-------------------------------------------");
                String content = dataRetriever.getData(url);
                Stock stock = hexunParser.parse(content);
                stockManger.save(stock);

                Thread.currentThread().sleep(random.nextInt(300));
            } catch (Exception ex) {
                fail++;
                LOGGER.error("[Error] id: " + id, ex);
            }
        }

        LOGGER.info("==========================================================" + "\n# Total  : " + count + "\n"
                + "# Failure: " + fail);
    }

    List<String> getStockList(String path) {
        ArrayList<String> list = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }

                if (line.length() == 7) {
                    list.add(line.substring(1));
                } else if (line.length() == 6) {
                    list.add(line);
                } else {
                    LOGGER.warn("[SKIP] Wrong format of stock id: " + line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("[Error] Can not read stock list file. Path: " + path);
        }

        return list;
    }
}
