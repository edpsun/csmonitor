package com.gmail.edpsun.hystock.manager;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gmail.edpsun.hystock.model.Stock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-beans.xml" })
public class StockManagerImplTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    StockManager stockManger;

    @Test
    public void testGetStock() {
        String id = "000687";
        Stock stock = stockManger.getStock(id, false);
        assertNotNull(stock);
        assertEquals(id, stock.getId());
        assertEquals(0, stock.getHolderStats().size());
    }

    @Test
    public void testGetStockCascade() {
        String id = "000687";
        Stock stock = stockManger.getStock(id, true);
        assertNotNull(stock);
        assertEquals(id, stock.getId());
        assertTrue(stock.getHolderStats().size() > 0);
    }

    @Test
    public void testGetNoStock() {
        String id = "xx000687";
        Stock stock = stockManger.getStock(id, true);
        assertNull(stock);
    }

}
