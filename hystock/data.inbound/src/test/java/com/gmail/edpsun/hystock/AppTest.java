package com.gmail.edpsun.hystock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gmail.edpsun.hystock.dao.MemReportDao;
import com.gmail.edpsun.hystock.model.MemReport;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-beans.xml" })
public class AppTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    MemReportDao dao;

    @Test
    public void testHibernate() throws Exception {
        MemReport r = dao.findByMemReportId("3");
        if (r != null) {
            System.out.println("Existing report 3");
            return;
        }

        MemReport r1 = new MemReport();
        r1.setId("3");
        r1.setName("Ed");
        dao.save(r1);
    }
}
