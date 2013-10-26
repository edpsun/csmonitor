package com.gmail.edpsun.hystock.inbound.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;

public class TecentParserTest {
    TecentParser p = new TecentParser();

    @Test
    public void testParse() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("tecent_000687.html");

        String content = FileUtils.readFileToString(new File(url.getFile()), "GB2312");
        Stock stock = p.parse(content);
        assertEquals("000687", stock.getId());
        assertEquals("恒天天鹅", stock.getName());

        List<HolderStat> holderStats = stock.getHolderStats();
        assertEquals(54, holderStats.size());

        assertEquals(2013, holderStats.get(1).getYear());
        assertEquals(1, holderStats.get(1).getQuarter());
        assertEquals("000687", holderStats.get(1).getStockId());

        assertEquals(53463, holderStats.get(1).getHolderNum());
        assertEquals(14166, holderStats.get(1).getAverageHolding());
        assertEquals("-0.83", holderStats.get(1).getDelta());
        assertEquals(75736, holderStats.get(1).getTotalShare());
        assertEquals(75736, holderStats.get(1).getCirculatingShare());

        assertEquals(2002, holderStats.get(43).getYear());
        assertEquals(3, holderStats.get(43).getQuarter());
        assertEquals("000687", holderStats.get(43).getStockId());
        assertEquals("0.55", holderStats.get(43).getDelta());
    }

    @Test
    public void testParseInvalidDelta() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("tecent_002027.html");

        String content = FileUtils.readFileToString(new File(url.getFile()), "GB2312");
        Stock stock = p.parse(content);
        assertEquals("002027", stock.getId());
        assertEquals("七喜控股", stock.getName());

        List<HolderStat> holderStats = stock.getHolderStats();
        assertEquals(36, holderStats.size());

        HolderStat stat = new HolderStat();
        stat.setYear(2004);
        stat.setQuarter(03);
        stat.setId("002027");
        assertFalse(holderStats.contains(stat));

        stat.setYear(2010);
        stat.setQuarter(01);
        stat.setId("002027");
        assertFalse(holderStats.contains(stat));

        stat.setYear(2010);
        stat.setQuarter(02);
        stat.setId("002027");
        assertFalse(holderStats.contains(stat));

    }

    @Test
    public void testParserQuarter() {
        int[] qs = p.parseReportDate("12-12-31");
        assertEquals(2012, qs[0]);
        assertEquals(04, qs[1]);

        qs = p.parseReportDate("12-09-30");
        assertEquals(2012, qs[0]);
        assertEquals(03, qs[1]);

        qs = p.parseReportDate("12-06-30");
        assertEquals(2012, qs[0]);
        assertEquals(02, qs[1]);

        qs = p.parseReportDate("12-03-31");
        assertEquals(2012, qs[0]);
        assertEquals(01, qs[1]);
    }
}
