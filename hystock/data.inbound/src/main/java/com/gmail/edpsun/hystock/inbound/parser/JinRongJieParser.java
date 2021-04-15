package com.gmail.edpsun.hystock.inbound.parser;

import com.gmail.edpsun.hystock.inbound.collect.DataRetriever;
import com.gmail.edpsun.hystock.model.Stock;
import org.springframework.stereotype.Component;

@Component("jinRongJieParser")

public class JinRongJieParser implements Parser {
    public static final String URL = "http://stock.jrj.com.cn/action/gudong/getGudongListByCode.jspa?vname=stockgudongList&stockcode=%s&page=1&psize=50&order=desc&sort=enddate";

    @Override
    public String getEncoding() {
        return DataRetriever.UTF_8;
    }

    @Override
    public String getTargetURL(final String id) {
        return String.format(URL, id);
    }

    @Override
    public Stock parse(final String id, final String name, final String content) {
        if (content == null) {
            throw new RuntimeException("Content is null from Hexun.");
        }


        return null;
    }
}
