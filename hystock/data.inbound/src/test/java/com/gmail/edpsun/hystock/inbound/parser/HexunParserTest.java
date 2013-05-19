package com.gmail.edpsun.hystock.inbound.parser;

import static org.junit.Assert.*;

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
        Stock stock = p.parse(content);
        assertEquals("000687", stock.getId());
        assertEquals("保定天鹅", stock.getName());

        List<HolderStat> holderStats = stock.getHolderStats();
        assertEquals(47, holderStats.size());

        assertEquals(2013, holderStats.get(0).getYear());
        assertEquals(1, holderStats.get(0).getQuarter());
        assertEquals("000687", holderStats.get(0).getStockId());

        assertEquals(53463, holderStats.get(0).getHolderNum());
        assertEquals(14166, holderStats.get(0).getAverageHolding());
        assertEquals("-0.84", holderStats.get(0).getDelta());
        assertEquals(75736, holderStats.get(0).getTotalShare());
        assertEquals(75736, holderStats.get(0).getCirculatingShare());

        assertEquals(2000, holderStats.get(46).getYear());
        assertEquals(2, holderStats.get(46).getQuarter());
        assertEquals("000687", holderStats.get(46).getStockId());
    }
}
