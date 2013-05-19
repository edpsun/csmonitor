package com.gmail.edpsun.hystock.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gmail.edpsun.hystock.model.Stock;

@Repository(value = "stockDao")
public class StockDao extends AbstractDaoImpl<Stock> {
    @Override
    public List<Stock> query(String queryString) {
        return getCurrentSession().createQuery(queryString).list();
    }

    @Transactional()
    @Override
    public Stock findById(String id) {
        return (Stock) getCurrentSession().get(Stock.class, id);
    }
}
