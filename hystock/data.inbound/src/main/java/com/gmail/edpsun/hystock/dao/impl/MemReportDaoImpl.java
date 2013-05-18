package com.gmail.edpsun.hystock.dao.impl;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gmail.edpsun.hystock.dao.MemReportDao;
import com.gmail.edpsun.hystock.model.MemReport;

@Repository
@Transactional
public class MemReportDaoImpl implements MemReportDao {
    private SessionFactory sessionFactory;

    @Resource(name = "sessionFactory")
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(MemReport report) {
        sessionFactory.getCurrentSession().save(report);
    }

    @Override
    public void update(MemReport report) {
        sessionFactory.getCurrentSession().update(report);
    }

    @Override
    public void delete(MemReport report) {
        sessionFactory.getCurrentSession().delete(report);
    }

    @Override
    public MemReport findByMemReportId(String id) {
        return (MemReport) sessionFactory.getCurrentSession().get(
                MemReport.class, id);
    }

}
