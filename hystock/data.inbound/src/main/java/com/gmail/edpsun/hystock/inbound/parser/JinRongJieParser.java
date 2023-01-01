package com.gmail.edpsun.hystock.inbound.parser;

import com.gmail.edpsun.hystock.inbound.collect.DataRetriever;
import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

enum FieldName {
    TOTAL_QUARTER_NUM, COLUMN
}

@Component("jinRongJieParser")
public class JinRongJieParser implements Parser {
    private static final String URL = "http://stock.jrj.com.cn/action/gudong/getGudongListByCode.jspa?vname=stockgudongList&stockcode=%s&page=1&psize=50&order=desc&sort=enddate";
    private static final String Q1 = "-03-31 00:00:00.0";
    private static final String Q2 = "-06-30 00:00:00.0";
    private static final String Q3 = "-09-30 00:00:00.0";
    private static final String Q4 = "-12-31 00:00:00.0";
    private static final String[] REPORT_QS = new String[]{Q1, Q2, Q3, Q4};
    private static final int[] REPORT_QS_NUM = new int[]{1, 2, 3, 4};

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
            throw new RuntimeException("Content is null from JinRongJie.");
        }

        final JsExecutor jsExecutor = getScriptExecutor();
        jsExecutor.eval(content);

        final Stock stock = new Stock();
        stock.setId(id);
        stock.setName(name);

        final Map<String, Integer> columnToIndexMapping = (Map) jsExecutor.getField(FieldName.COLUMN);
        // parse each quarter
        final List<HolderStat> hsList = new ArrayList<>();
        int totalQuarterCount = (Integer) jsExecutor.getField(FieldName.TOTAL_QUARTER_NUM);
        totalQuarterCount = totalQuarterCount > 50 ? 50 : totalQuarterCount;

        for (int rowId = 0; rowId < totalQuarterCount; rowId++) {
            Optional<HolderStat> holderState = null;
            try {
                holderState = createHolderState(jsExecutor, rowId, columnToIndexMapping);
            } catch (final Exception ex) {
                System.out.println(String.format("[SKIP] Cannot parse for %s at line %s. Error message: %s", id, rowId, ex.getMessage()));
                continue;
            }

            if (holderState.isPresent()) {
                hsList.add(holderState.get());
            }
        }

        stock.setHolderStats(hsList);
        return stock;
    }

    private Optional<HolderStat> createHolderState(final JsExecutor jsExecutor, final int rowId, final Map<String, Integer> columnToIndexMapping) {
        final HolderStat hs = new HolderStat();

        final String id = jsExecutor.getDataCell(rowId, columnToIndexMapping.get("stockcode"));

        // "2013-03-31 00:00:00.0"
        final String reportDate = jsExecutor.getDataCell(rowId, columnToIndexMapping.get("enddate"));
        // 53463
        final String holderNum = jsExecutor.getDataCell(rowId, columnToIndexMapping.get("new_num"));
        // 14166
        final String avg = jsExecutor.getDataCell(rowId, columnToIndexMapping.get("avg_num"));
        // 0.843141693073789(use the negative value of holder increase to simulate)
        final String delta = jsExecutor.getDataCell(rowId, columnToIndexMapping.get("huanbi"));


        final String cirTotHoldNum = jsExecutor.getDataCell(rowId, columnToIndexMapping.get("cir_tot_hold_num"));
        final String cirTotShrPct = jsExecutor.getDataCell(rowId, columnToIndexMapping.get("cir_tot_shr_pct"));

        final String totHoldNum = jsExecutor.getDataCell(rowId, columnToIndexMapping.get("tot_hold_num"));
        final String totShrPct = jsExecutor.getDataCell(rowId, columnToIndexMapping.get("tot_shr_pct"));

        final int[] yAndQ = parseReportDate(reportDate);
        if (yAndQ.length == 0) {
            throw new RuntimeException("Cannot parse report date: " + reportDate);
        }

        hs.setYear(yAndQ[0]);
        hs.setQuarter(yAndQ[1]);

        hs.setHolderNum(parseLong(holderNum));
        hs.setAverageHolding(parseLong(avg));

        final long totalShare = (long) (parseLong(totHoldNum) * 100 / NumberUtils.createFloat(totShrPct));
        final long circulatingShare = (long) (parseLong(cirTotHoldNum) * 100 / NumberUtils.createFloat(cirTotShrPct));

        hs.setTotalShare(totalShare);
        hs.setCirculatingShare(circulatingShare);

        // use holder change rate to approximate the amount change delta.(negative of the holder change rate)
        final float fdelta = -NumberUtils.createFloat(delta);
        hs.setDelta("" + fdelta);
        hs.setStockId(id);
        hs.setId(id + ":" + hs.getYear() + ":" + hs.getQuarter());
        return Optional.of(hs);
    }

    private JsExecutor getScriptExecutor() {
        final ScriptEngineManager sem = new ScriptEngineManager();
        final ScriptEngine scriptEngine = sem.getEngineByName("graal.js");
        return new JsExecutor(scriptEngine);
    }

    int[] parseReportDate(final String r) {
        int year = -1;
        int quarter = -1;
        for (int i = 0; i < REPORT_QS.length; i++) {
            if (r.indexOf(REPORT_QS[i]) > -1) {
                quarter = REPORT_QS_NUM[i];
                year = Integer.parseInt(r.replace(REPORT_QS[i], ""));
                if (year < 90) {
                    year += 2000;
                }
            }
        }

        if (year == -1 || quarter == -1) {
            return new int[]{};
        }
        return new int[]{year, quarter};
    }

    private long parseLong(String s) {
        s = s.replace(",", "");
        if (s.indexOf(".") > -1) {
            s = s.substring(0, s.indexOf("."));
        }
        return Long.parseLong(s);
    }

}

class JsExecutor {
    private static final Map<FieldName, String> FIELD_MAPPING = new HashMap<>();

    static {
        FIELD_MAPPING.put(FieldName.TOTAL_QUARTER_NUM, "stockgudongList.summary.total");
        FIELD_MAPPING.put(FieldName.COLUMN, "stockgudongList.column");
    }

    private final ScriptEngine scriptEngine;

    public JsExecutor(final ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public Object getField(final FieldName fieldName) {
        return eval(FIELD_MAPPING.getOrDefault(fieldName, "Field Name is not defined: " + fieldName));
    }

    public Object eval(final String content) {
        try {
            return scriptEngine.eval(content);
        } catch (final ScriptException e) {
            throw new RuntimeException("Cannot evaluate js: " + content);
        }
    }

    public String getDataCell(final int rowId, final int columnIndex) {

        final Object result = eval(String.format("stockgudongList.data[%d][%d]", rowId, columnIndex));

        return result == null ? "" : result.toString();
    }
}
