package com.gmail.edpsun.hystock.inbound.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;

public class EastmoneyParserTest {
    private EastmoneyParser underTest;

    @Before
    public void setUp() {
        underTest = new EastmoneyParser();
    }

    @Test
    public void testParse() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("002296.json");

        String content = FileUtils.readFileToString(new File(url.getFile()));
        Stock stock = underTest.parse("002296", "辉煌科技", content);
        assertEquals("002296", stock.getId());
        assertEquals("辉煌科技", stock.getName());

        List<HolderStat> holderStats = stock.getHolderStats();
        assertEquals(4, holderStats.size());

        assertEquals(2019, holderStats.get(0).getYear());
        assertEquals(2, holderStats.get(0).getQuarter());
        assertEquals("002296", holderStats.get(0).getStockId());

        assertEquals(43244, holderStats.get(0).getHolderNum());
        assertEquals(8710, holderStats.get(0).getAverageHolding());
        assertEquals("0.73", holderStats.get(0).getDelta());
        assertEquals(37665, holderStats.get(0).getTotalShare());
        assertEquals(37665, holderStats.get(0).getCirculatingShare());

        assertEquals(2018, holderStats.get(3).getYear());
        assertEquals(3, holderStats.get(3).getQuarter());
        assertEquals("002296", holderStats.get(3).getStockId());
    }

    @Test
    public void testParserQuarter() {
        int[] qs = underTest.parseReportDate("2012-12-31T00:00:00");
        assertEquals(2012, qs[0]);
        assertEquals(04, qs[1]);

        qs = underTest.parseReportDate("2012-09-30T00:00:00");
        assertEquals(2012, qs[0]);
        assertEquals(03, qs[1]);

        qs = underTest.parseReportDate("2012-06-30T00:00:00");
        assertEquals(2012, qs[0]);
        assertEquals(02, qs[1]);

        qs = underTest.parseReportDate("2012-03-31T00:00:00");
        assertEquals(2012, qs[0]);
        assertEquals(01, qs[1]);
    }
}
