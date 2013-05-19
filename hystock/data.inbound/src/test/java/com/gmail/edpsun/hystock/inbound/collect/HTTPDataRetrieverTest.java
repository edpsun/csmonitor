package com.gmail.edpsun.hystock.inbound.collect;

import static org.junit.Assert.*;

import org.junit.Test;

public class HTTPDataRetrieverTest {
    HTTPDataRetriever dr = new HTTPDataRetriever();

    @Test
    public void testGetData() {
        String s = dr.getData("http://www.baidu.com");
        assertNotNull(s);
    }

}
