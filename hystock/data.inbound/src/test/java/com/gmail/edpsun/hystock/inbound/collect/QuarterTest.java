package com.gmail.edpsun.hystock.inbound.collect;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gmail.edpsun.hystock.inbound.collect.InboundCollector.Quarter;

public class QuarterTest {

    @Test
    public void test() {
        Quarter q = Quarter.valueOf("2013-02");
        assertEquals(2013, q.getYear());
        assertEquals(02, q.getQuarter());

        try {
            q = Quarter.valueOf("201201");
            fail("should failed.");
        } catch (Exception ex) {
            assertEquals(RuntimeException.class, ex.getClass());
        }
    }

}
