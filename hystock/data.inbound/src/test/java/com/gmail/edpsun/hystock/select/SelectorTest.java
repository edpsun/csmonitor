package com.gmail.edpsun.hystock.select;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.URL;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-beans.xml" })
public class SelectorTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private Selector selector;

    @Test
    public void testProcess() {
        final InboundContext globalCtx = new InboundContext();

        final URL url = getClass().getClassLoader().getResource("own.EBK");
        globalCtx.setEbk(url.getFile());
        globalCtx.setKeepAll(false);
        globalCtx.setSchema("/tmp/" + new Date().getTime());
        new File(globalCtx.getSchema()).mkdirs();

        final int p = selector.process(globalCtx);

        for (final InboundContext stockCtx : globalCtx.getChosenList()) {
            System.out.println(stockCtx.getStock().getId() + "-->" + stockCtx.getAnalyzeVO().getTags());
        }

        // 东睦股份 600114 is chosen, if not change to 0
        assertEquals(2, globalCtx.getChosenList().size());
    }

    @Test
    public void testProcessKeptAll() {
        final InboundContext globalCtx = new InboundContext();

        final URL url = getClass().getClassLoader().getResource("own.EBK");
        globalCtx.setEbk(url.getFile());
        globalCtx.setKeepAll(true);
        globalCtx.setSchema("/tmp/" + new Date().getTime());
        new File(globalCtx.getSchema()).mkdirs();

        final int p = selector.process(globalCtx);

        for (final InboundContext stockCtx : globalCtx.getChosenList()) {
            System.out.println(stockCtx.getStock().getId() + "->" + stockCtx.getAnalyzeVO().getTags());
            if ("600594".equals(stockCtx.getStock().getId())) {
                assertNotNull(stockCtx.getAnalyzeVO().getTags());
                assertEquals("By_ALL,", stockCtx.getAnalyzeVO().getTags().trim());
            }
        }

        assertEquals(4, globalCtx.getChosenList().size());
        
        System.out.println(System.getProperty("java.class.path"));
    }
}
