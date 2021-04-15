package com.gmail.edpsun.hystock.inbound.parser;

import com.gmail.edpsun.hystock.model.Stock;

public interface Parser {
    String getEncoding();

    String getTargetURL(String id);

    Stock parse(String id, String name, String content);
}
