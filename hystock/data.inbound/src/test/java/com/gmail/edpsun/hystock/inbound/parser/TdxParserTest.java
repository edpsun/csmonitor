package com.gmail.edpsun.hystock.inbound.parser;

import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TdxParserTest {
    private TdxParser underTest;

    @Before
    public void setUp() {
        underTest = new TdxParser("/tmp");
    }

    @Test
    public void testParse() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("2022-Q3");

        final String content = FileUtils.readFileToString(new File(url.getFile()), "GB2312");

        Stock stock = underTest.parse("002296", "辉煌科技", content);
        assertEquals("002296", stock.getId());
        assertEquals("辉煌科技", stock.getName());

        List<HolderStat> holderStats = stock.getHolderStats();
        assertEquals(52, holderStats.size());

        HolderStat holderStat_2019_q2 = holderStats.get(13);
        assertEquals(2019, holderStat_2019_q2.getYear());
        assertEquals(2, holderStat_2019_q2.getQuarter());
        assertEquals("002296", holderStat_2019_q2.getStockId());

        assertEquals(43244, holderStat_2019_q2.getHolderNum());
        assertEquals(7607, holderStat_2019_q2.getAverageHolding());
        assertEquals("2.37", holderStat_2019_q2.getDelta());
        assertEquals(32895, holderStat_2019_q2.getTotalShare());
        assertEquals(32895, holderStat_2019_q2.getCirculatingShare());

        HolderStat holderStat_2018_q3 = holderStats.get(16);
        assertEquals(2018, holderStat_2018_q3.getYear());
        assertEquals(3, holderStat_2018_q3.getQuarter());
        assertEquals("002296", holderStat_2018_q3.getStockId());
        assertEquals(40892, holderStat_2018_q3.getHolderNum());

        HolderStat holderStat_2018_q4 = holderStats.get(15);
        assertEquals(2018, holderStat_2018_q4.getYear());
        assertEquals(4, holderStat_2018_q4.getQuarter());

        HolderStat holderStat_2019_q1 = holderStats.get(14);
        assertEquals(2019, holderStat_2019_q1.getYear());
        assertEquals(1, holderStat_2019_q1.getQuarter());
    }

}