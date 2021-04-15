package com.gmail.edpsun.hystock.inbound.collect;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HTTPDataRetrieverTest {
    HTTPDataRetriever dr = new HTTPDataRetriever();

    @Test
    public void testGetData() {
        final String s = dr.getData("https://hq.sinajs.cn/list=sh600036");
        System.out.println(s);
        assertTrue(s.indexOf("招商银行") > -1);
    }

    @Test
    public void testGetData2() {
        final String s = dr.getData("http://stock.jrj.com.cn/action/gudong/getGudongListByCode.jspa?vname=stockgudongList&stockcode=600036&page=1&psize=20&order=desc&sort=enddate",
                HTTPDataRetriever.UTF_8);
        System.out.println(s);
        assertTrue(s.indexOf("招商银行") > -1);
    }

}
