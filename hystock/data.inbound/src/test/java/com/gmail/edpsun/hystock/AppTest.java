package com.gmail.edpsun.hystock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.annotation.Resource;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gmail.edpsun.hystock.dao.MemReportDao;
import com.gmail.edpsun.hystock.model.MemReport;
import com.gmail.edpsun.hystock.test.GreetMeBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-beans.xml" })
public class AppTest extends AbstractJUnit4SpringContextTests {

    @Test
    public void testApp() throws Exception {
        assertTrue(true);
        Properties p = new Properties();
        p.load(this.getClass().getClassLoader()
                .getSystemResourceAsStream("properties/conf.properties"));
        System.out.println(p.get("scope"));
        applicationContext.getBean("greetMeBean", GreetMeBean.class).execute();
    }

    @Test
    public void testDerby() throws Exception {
        Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
        String nsURL = "jdbc:derby://localhost:1527/hystock";
        java.util.Properties props = new java.util.Properties();
        props.put("user", "admin");
        props.put("password", "admin123");
        Connection conn = DriverManager.getConnection(nsURL, props);
        Statement s = conn.createStatement();

        ResultSet rs = s.executeQuery("SELECT * FROM MEMREPORT");
        while (rs.next()) {
            System.out.println(rs.getString(1) + " : " + rs.getString(2));
        }
    }

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
