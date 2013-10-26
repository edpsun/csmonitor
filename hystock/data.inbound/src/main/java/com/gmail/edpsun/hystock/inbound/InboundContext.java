package com.gmail.edpsun.hystock.inbound;

import java.util.ArrayList;
import java.util.List;

import com.gmail.edpsun.hystock.inbound.collect.InboundCollector.Quarter;
import com.gmail.edpsun.hystock.model.AnalyzeVO;
import com.gmail.edpsun.hystock.model.Stock;
import com.gmail.edpsun.tools.chain.impl.ContextImpl;

public class InboundContext extends ContextImpl {
    public static final String PARAM_PARSER = "parser";
    public static final String PARAM_QUARTER = "quarter";
    public static final String PARAM_EBK = "ebk";
    public static final String PARAM_KEEP_ALL = "keep_all";
    public static final String PARAM_SCHEMA = "schema_name";
    public static final String PARAM_STOCK = "stock";
    public static final String PARAM_ANALYZE_VO = "analyzeVO";
    public static final String PARAM_GLOBAL_CONTEXT = "GLOBAL_CONTEXT";
    public static final String PARAM_CHOSEN_LIST = "CHOSEN_LIST";

    public String getParser() {
        return (String) get(PARAM_PARSER);
    }

    public void setParser(String parser) {
        put(PARAM_PARSER, parser);
    }

    public Stock getStock() {
        return (Stock) get(PARAM_STOCK);
    }

    public void setStock(Stock stock) {
        put(PARAM_STOCK, stock);
    }

    public Quarter getQuarter() {
        return (Quarter) get(PARAM_QUARTER);
    }

    public void setQuarter(Quarter quarter) {
        put(PARAM_QUARTER, quarter);
    }

    public String getEbk() {
        return (String) get(PARAM_EBK);
    }

    public void setEbk(String ebk) {
        put(PARAM_EBK, ebk);
    }

    public String getSchema() {
        return (String) get(PARAM_SCHEMA);
    }

    public void setSchema(String schema) {
        put(PARAM_SCHEMA, schema);
    }

    public Boolean getKeepAll() {
        return (Boolean) get(PARAM_KEEP_ALL);
    }

    public void setKeepAll(Boolean keepAll) {
        put(PARAM_KEEP_ALL, keepAll);
    }

    public AnalyzeVO getAnalyzeVO() {
        return (AnalyzeVO) get(PARAM_ANALYZE_VO);
    }

    public void setAnalyzeVO(AnalyzeVO analyzeVO) {
        put(PARAM_ANALYZE_VO, analyzeVO);
    }

    public List<InboundContext> getChosenList() {
        List<InboundContext> list = (List<InboundContext>) get(InboundContext.PARAM_CHOSEN_LIST);
        if (list == null) {
            list = new ArrayList<InboundContext>();
            put(InboundContext.PARAM_CHOSEN_LIST, list);
        }
        return list;
    }
}
