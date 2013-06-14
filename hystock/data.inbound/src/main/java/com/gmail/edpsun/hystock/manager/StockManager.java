package com.gmail.edpsun.hystock.manager;

import java.util.List;

import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;

public interface StockManager {
    void save(Stock stock);

    List<HolderStat> getHolderStats(String id);
}
