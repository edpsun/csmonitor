package com.gmail.edpsun.hystock.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gmail.edpsun.hystock.model.HolderStat;

@Repository(value = "holderStatDao")
public class HolderStatDao extends AbstractDaoImpl<HolderStat> {

    @Transactional
    public List<HolderStat> query(String queryString) {
        return getCurrentSession().createQuery(queryString).list();
    }

    @Transactional
    public HolderStat findById(String id) {
        return (HolderStat) getCurrentSession().get(HolderStat.class, id);
    }
}
