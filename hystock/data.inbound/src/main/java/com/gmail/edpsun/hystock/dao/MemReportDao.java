package com.gmail.edpsun.hystock.dao;

import com.gmail.edpsun.hystock.model.MemReport;

public interface MemReportDao {

    void save(MemReport report);

    void update(MemReport report);

    void delete(MemReport report);

    MemReport findByMemReportId(String id);
}
