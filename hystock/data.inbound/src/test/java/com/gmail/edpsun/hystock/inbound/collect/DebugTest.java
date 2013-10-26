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
public class DebugTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    InboundCollector collector;

    @Test
    @Transactional
    @Rollback(true)
    public void testDebug() {
        InboundContext ctx = new InboundContext();
        URL url = this.getClass().getClassLoader().getResource("test.EBK");
        ctx.setEbk(url.getFile());
        ctx.setQuarter(Quarter.valueOf("2019-01"));
        ctx.setParser("Q");
        int c = collector.process(ctx);
        assertEquals(1, c);
    }
}
