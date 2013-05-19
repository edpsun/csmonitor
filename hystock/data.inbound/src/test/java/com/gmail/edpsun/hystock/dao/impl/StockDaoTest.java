package com.gmail.edpsun.hystock.dao.impl;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.gmail.edpsun.hystock.dao.IDao;
import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-beans.xml" })
public class StockDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Resource(name = "stockDao")
    IDao<Stock> stockDao;

    @Resource(name = "holderStatDao")
    IDao<HolderStat> holderStatDao;

    @Test
    public void testQuery() {
        String id = "600123";
        List<HolderStat> list = holderStatDao.query(" from HolderStat as hs where hs.stockId = " + "'600123'");
        for (HolderStat holderStat : list) {
            System.out.println(holderStat.getId());
            System.out.println(holderStat);
        }

        HolderStat hs = new HolderStat();
        hs.setStockId(id);
        hs.setYear(2013);
        hs.setQuarter(3);

        assertEquals(2, list.size());
        assertTrue(list.contains(hs));

        list = holderStatDao.query(" from HolderStat as hs where hs.stockId = " + "'600123xx'");
        assertEquals(0, list.size());
    }

    @Test
    public void testFindById() {
        Stock st = stockDao.findById("600123");
        System.out.println(st.getName());
        assertEquals("PIG Pig", st.getName());
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testAdd() {
        Stock st = stockDao.findById("600123");
        if (st != null)
            return;

        st = new Stock();
        st.setId("600123");
        st.setName("PIG Pig");
        stockDao.add(st);

        HolderStat stat = new HolderStat();
        stat.setStockId("600123");
        stat.setYear(2013);
        stat.setQuarter(4);
        stat.setDelta("0.4");
        stat.setTotalShare(1);
        stat.setAverageHolding(2);
        stat.setAverageHolding(3);
        holderStatDao.add(stat);
    }

    @Test
    public void testDelete() {

    }

    @Test
    @Transactional
    @Rollback(false)
    public void testUpdate() {
        Stock st = stockDao.findById("600123");
        if (st == null)
            return;

        String comment = new Date().toString();
        st.setComment(comment);
        stockDao.update(st);
    }
}
