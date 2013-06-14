package com.gmail.edpsun.hystock.inbound;

import java.util.HashMap;

import com.gmail.edpsun.hystock.inbound.collect.InboundCollector.Quarter;

public class InboundContext extends HashMap<String, Object> {
    private String PARAM_QUARTER = "quarter";
    private String PARAM_EBK = "ebk";
    private String PARAM_KEEP_ALL = "keep_all";
    private String PARAM_SCHEMA = "schema_name";

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

}
