package com.gmail.edpsun.hystock.select;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gmail.edpsun.hystock.inbound.InboundContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-beans.xml" })
public class SelectorTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private Selector selector;

    @Test
    public void testProcess() {
        InboundContext globalCtx = new InboundContext();

        URL url = this.getClass().getClassLoader().getResource("own.EBK");
        globalCtx.setEbk(url.getFile());
        globalCtx.setKeepAll(false);
        globalCtx.setSchema("/tmp/" + new Date().getTime());
        new File(globalCtx.getSchema()).mkdirs();

        int p = selector.process(globalCtx);

        for (InboundContext stockCtx : globalCtx.getChosenList()) {
            System.out.println(stockCtx.getStock().getId() + "->" + stockCtx.getAnalyzeVO().getTags());
        }

        assertEquals(3, globalCtx.getChosenList().size());
    }

    @Test
    public void testProcessKeptAll() {
        InboundContext globalCtx = new InboundContext();

        URL url = this.getClass().getClassLoader().getResource("own.EBK");
        globalCtx.setEbk(url.getFile());
        globalCtx.setKeepAll(true);
        globalCtx.setSchema("/tmp/" + new Date().getTime());
        new File(globalCtx.getSchema()).mkdirs();

        int p = selector.process(globalCtx);

        for (InboundContext stockCtx : globalCtx.getChosenList()) {
            System.out.println(stockCtx.getStock().getId() + "->" + stockCtx.getAnalyzeVO().getTags());
            if ("300027".equals(stockCtx.getStock().getId())) {
                assertNotNull(stockCtx.getAnalyzeVO().getTags());
                assertEquals("By_ALL,", stockCtx.getAnalyzeVO().getTags().trim());
            }
        }

        assertEquals(4, globalCtx.getChosenList().size());
        
        System.out.println(System.getProperty("java.class.path"));
    }
}
