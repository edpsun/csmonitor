package com.gmail.edpsun.hystock.inbound.parser;

import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JinRongJieParserTest {
    private JinRongJieParser underTest;

    @Before
    public void setUp() {
        underTest = new JinRongJieParser();
    }

    @Test
    public void testParsing() throws IOException {
        final URL url = getClass().getClassLoader().getResource("jrj_000687.response");

        final String content = FileUtils.readFileToString(new File(url.getFile()), "UTF-8");

        final Stock stock = underTest.parse("000687", "保定天鹅", content);
        assertEquals("000687", stock.getId());
        assertEquals("保定天鹅", stock.getName());

        final List<HolderStat> holderStats = stock.getHolderStats();

        final int index = 30;
        assertEquals(2013, holderStats.get(index).getYear());
        assertEquals(1, holderStats.get(index).getQuarter());
        assertEquals("000687", holderStats.get(index).getStockId());

        assertEquals(53463, holderStats.get(index).getHolderNum());
        assertEquals(14166, holderStats.get(index).getAverageHolding());
        assertEquals("-0.8431417", holderStats.get(index).getDelta());
        assertEquals(75736, holderStats.get(index).getTotalShare());
        assertEquals(75285, holderStats.get(index).getCirculatingShare());

        assertEquals(2020, holderStats.get(1).getYear());
        assertEquals(2, holderStats.get(1).getQuarter());
        assertEquals("000687", holderStats.get(1).getStockId());

        assertEquals(50, holderStats.size());
    }

    @Test
    public void testParsing2() throws IOException {
        final URL url = getClass().getClassLoader().getResource("jrj_300529.response");

        final String content = FileUtils.readFileToString(new File(url.getFile()), "UTF-8");

        final Stock stock = underTest.parse("300529", "健帆生物", content);
        System.out.println(stock);
    }

}