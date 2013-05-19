package com.gmail.edpsun.hystock.dao;

import java.util.List;

public interface IDao<T> {
    public void add(Object object);

    public void delete(String id);

    public void update(Object object);

    public List<T> query(String queryString);

    public T findById(String id);
}
