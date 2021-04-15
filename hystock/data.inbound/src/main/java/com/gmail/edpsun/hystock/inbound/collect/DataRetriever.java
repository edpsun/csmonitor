package com.gmail.edpsun.hystock.inbound.collect;

import java.nio.charset.StandardCharsets;

public interface DataRetriever {
    public static final String GBK = "GBK";
    public static final String UTF_8 = StandardCharsets.UTF_8.name();
    
    String getData(String url);

    String getData(String url, String encoding);
}
