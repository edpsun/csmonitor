package com.gmail.edpsun.hystock.manager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gmail.edpsun.hystock.dao.IDao;
import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;

@Service
public class StockManagerImpl implements StockManager {
    public static Logger LOGGER = Logger.getLogger(StockManagerImpl.class);

    @Resource(name = "stockDao")
    IDao<Stock> stockDao;

    @Resource(name = "holderStatDao")
    IDao<HolderStat> holderStatDao;

    @Transactional
    public void save(Stock stock) {
        // save stock
        Stock st = stockDao.findById(stock.getId());
        if (st == null) {
            stockDao.add(stock);
        }

        // save holder stat
        List<HolderStat> exists = holderStatDao.query(String.format(" from HolderStat as hs where hs.stockId = '%s' ",
                stock.getId()));
        List<HolderStat> doneList = new ArrayList<HolderStat>();

        List<HolderStat> list = stock.getHolderStats();
        for (HolderStat hs : list) {
            if (exists.contains(hs) || doneList.contains(hs)) {
                LOGGER.debug("[Skip] Existing holder stat object: " + hs);
            } else {
                LOGGER.info(String.format("Saving : %s %s %s", hs.getStockId(), hs.getYear(), hs.getQuarter()));
                holderStatDao.add(hs);
                doneList.add(hs);
            }
        }
    }
}
