package com.gmail.edpsun.hystock.inbound.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;

@Component("eastmoneyParser")
public class EastmoneyParser implements Parser {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String getTargetURL(final String id) {
        return String.format(
                "http://data.eastmoney.com/DataCenter_V3/gdhs/GetDetial" +
                        ".ashx?code=%s&js=&pagesize=50&page=1&sortRule=-1&sortType=EndDate",
                id);
    }

    public Stock parse(final String id, final String name, final String content) {
        final Stock stock = new Stock();
        stock.setId(id);
        stock.setName(name);

        try {
            JsonNode node = objectMapper.readTree(content);
            JsonNode dataNode = node.findPath("data");
            if (dataNode.isArray()) {
                ArrayNode an = (ArrayNode) dataNode;
                Iterator<JsonNode> iterator = an.iterator();

                List<HolderStat> hsList = new ArrayList<HolderStat>();
                while (iterator.hasNext()) {
                    Optional<HolderStat> stat = createHolderState(stock, iterator.next());
                    if (stat.isPresent()) {
                        hsList.add(stat.get());
                    }
                }

                cleanup(hsList);
                
                stock.setHolderStats(hsList);
            } else {
                throw new RuntimeException("Eastmoney data format is not as expected: " + content);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stock;
    }

    private void cleanup(final List<HolderStat> hsList) {
    }

    private Optional<HolderStat> createHolderState(final Stock stock, final JsonNode node) {
        HolderStat hs = new HolderStat();

        String reportDate = node.findPath("EndDate").asText();
        String holderNum = node.findPath("HolderNum").asText();

        String avg = node.findPath("HolderAvgStockQuantity").asText();
        String delta = node.findPath("HolderNumChangeRate").asText();
        String totalShare = node.findPath("CapitalStock").asText();
        String circulating = node.findPath("CapitalStock").asText();

        int[] yAndQ = parseReportDate(reportDate);
        if (yAndQ.length == 0) {
            return Optional.empty();
        }

        hs.setYear(yAndQ[0]);
        hs.setQuarter(yAndQ[1]);

        hs.setHolderNum(parseLong(holderNum));
        hs.setAverageHolding(parseLong(avg));
        hs.setTotalShare(parseLong(totalShare) / 10000);
        hs.setCirculatingShare(parseLong(circulating) / 10000);

        float fdelta = -NumberUtils.createFloat(delta);
        hs.setDelta("" + fdelta);
        hs.setStockId(stock.getId());
        hs.setId(stock.getId() + ":" + hs.getYear() + ":" + hs.getQuarter());
        return Optional.of(hs);
    }

    private static final String Q1 = "-03-31T00:00:00";
    private static final String Q2 = "-06-30T00:00:00";
    private static final String Q3 = "-09-30T00:00:00";
    private static final String Q4 = "-12-31T00:00:00";
    private static final String[] REPORT_QS = new String[]{Q1, Q2, Q3, Q4};
    private static final int[] REPORT_QS_NUM = new int[]{1, 2, 3, 4};

    int[] parseReportDate(String r) {
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
