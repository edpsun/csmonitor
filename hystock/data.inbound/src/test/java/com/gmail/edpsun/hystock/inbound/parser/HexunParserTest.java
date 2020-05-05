package com.gmail.edpsun.hystock.inbound.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;

public class HexunParserTest {
    HexunParser p = new HexunParser();

    @Test
    public void testGetTargetURL() {
        System.out.println(p.getTargetURL(null));
    }

    @Test
    public void testParse() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("2009_cgjzd_000687.shtml");

        String content = FileUtils.readFileToString(new File(url.getFile()), "GB2312");
        Stock stock = p.parse(null, null, content);
        assertEquals("000687", stock.getId());
        assertEquals("保定天鹅", stock.getName());

        List<HolderStat> holderStats = stock.getHolderStats();
        assertEquals(43, holderStats.size());

        assertEquals(2013, holderStats.get(0).getYear());
        assertEquals(1, holderStats.get(0).getQuarter());
        assertEquals("000687", holderStats.get(0).getStockId());

        assertEquals(53463, holderStats.get(0).getHolderNum());
        assertEquals(14166, holderStats.get(0).getAverageHolding());
        assertEquals("-0.84", holderStats.get(0).getDelta());
        assertEquals(75736, holderStats.get(0).getTotalShare());
        assertEquals(75736, holderStats.get(0).getCirculatingShare());

        assertEquals(2002, holderStats.get(42).getYear());
        assertEquals(3, holderStats.get(42).getQuarter());
        assertEquals("000687", holderStats.get(42).getStockId());
    }

    @Test
    public void testParseInvalidDelta() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("2009_cgjzd_002027.shtml");

        String content = FileUtils.readFileToString(new File(url.getFile()), "GB2312");
        Stock stock = p.parse(null, null, content);
        assertEquals("002027", stock.getId());
        assertEquals("七喜控股", stock.getName());

        List<HolderStat> holderStats = stock.getHolderStats();
        assertEquals(32, holderStats.size());

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
        int[] qs = p.parseReportDate("12年年度");
        assertEquals(2012, qs[0]);
        assertEquals(04, qs[1]);

        qs = p.parseReportDate("12年前3季");
        assertEquals(2012, qs[0]);
        assertEquals(03, qs[1]);

        qs = p.parseReportDate("12年中期");
        assertEquals(2012, qs[0]);
        assertEquals(02, qs[1]);

        qs = p.parseReportDate("05年第2季");
        assertEquals(2005, qs[0]);
        assertEquals(02, qs[1]);

        qs = p.parseReportDate("12年第1季");
        assertEquals(2012, qs[0]);
        assertEquals(01, qs[1]);
    }

    @Test
    public void testParseForInvalidStock() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("2009_cgjzd_000748.shtml");

        String content = FileUtils.readFileToString(new File(url.getFile()), "GB2312");
        Stock stock = p.parse("000748", "Invalid", content);
        assertEquals("000748", stock.getId());
        assertEquals("Invalid", stock.getName());
    }
}
