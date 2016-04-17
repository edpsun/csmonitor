package com.gmail.edpsun.hystock.dao.impl;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.gmail.edpsun.hystock.dao.IDao;

public abstract class AbstractDaoImpl<T> implements IDao<T> {
    private SessionFactory sessionFactory;

    @Resource(name = "sessionFactory")
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional
    public void add(Object object) {
        getCurrentSession().save(object);
    }

    @Transactional
    public void delete(String id) {
        getCurrentSession().delete(findById(id));
    }

    @Transactional
    public void update(Object object) {
        getCurrentSession().update(object);
    }
}
