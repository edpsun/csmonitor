package com.gmail.edpsun.hystock.inbound.collect;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.inbound.collect.InboundCollector.Quarter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-beans.xml" })
public class InboundCollectorTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    InboundCollector collector;
    
    @Test
    @Transactional
    @Rollback(true)
    public void testCollectUpdateAllTecent() {
        InboundContext ctx = new InboundContext();
        URL url = this.getClass().getClassLoader().getResource("own.EBK");
        ctx.setEbk(url.getFile());
        ctx.setQuarter(Quarter.valueOf("2019-01"));
        ctx.setParser("Q");
        int c = collector.process(ctx);
        assertEquals(4, c);
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testCollectUpdateAll() {
        InboundContext ctx = new InboundContext();
        URL url = this.getClass().getClassLoader().getResource("own.EBK");
        ctx.setEbk(url.getFile());
        ctx.setQuarter(Quarter.valueOf("2019-01"));
        int c = collector.process(ctx);
        assertEquals(4, c);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testCollectNoUpdate() {
        InboundContext ctx = new InboundContext();
        URL url = this.getClass().getClassLoader().getResource("own.EBK");
        ctx.setEbk(url.getFile());
        ctx.setQuarter(Quarter.valueOf("2010-01"));
        int c = collector.process(ctx);
        assertEquals(0, c);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testCollectNoUpdate2() {
        InboundContext ctx = new InboundContext();
        URL url = this.getClass().getClassLoader().getResource("own.EBK");
        ctx.setEbk(url.getFile());
        ctx.setQuarter(Quarter.valueOf("2013-01"));
        int c = collector.process(ctx);
        assertEquals(0, c);
    }

    @Test
    public void testGetStockList() {
        URL url = this.getClass().getClassLoader().getResource("own.EBK");
        List<String> list = collector.getStockList(url.getFile());
        assertEquals(4, list.size());
        assertTrue(list.contains("002010"));
        assertTrue(list.contains("600114"));
        assertTrue(list.contains("000687"));
    }
}
