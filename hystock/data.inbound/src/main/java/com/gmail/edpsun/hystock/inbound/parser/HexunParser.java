package com.gmail.edpsun.hystock.inbound.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;

@Component("hexunParser")
public class HexunParser implements Parser {
    String titleLine = "target=\"_blank\">(.*)</a>.* target=\"_blank\">(.*)</a>.*";
    Pattern titlePattern = Pattern.compile(titleLine);

    String holderStatLine = ".*<span class=\"font10\">(.*)</span>" + ".*<span class=\"font10\">(.*)</span>"
            + ".*<span class=\"font10\">(.*)</span>" + ".*<span class=\"font10\">(.*)</span>"
            + ".*<span class=\"font10\">(.*)</span>" + ".*<span class=\"font10\">(.*)</span>.*";
    Pattern holderStatPattern = Pattern.compile(holderStatLine);

    @Override
    public String getTargetURL(String id) {
        return String.format("http://stockdata.stock.hexun.com/2009_cgjzd_%s.shtml", id);
    }

    @Override
    public Stock parse(String content) {
        if (content == null) {
            throw new RuntimeException("Content is null from Hexun.");
        }

        int start = content.indexOf("<div id=\"rightpart\">");
        int end = content.indexOf("</table>", start);

        if (start == -1 || end == -1) {
            throw new RuntimeException("[Error] Hexun content might be changed.");
        }

        String payload = content.substring(start, end);
        String[] lines = payload.split("\n");
        Stock stock = null;
        for (String l : lines) {
            if (l.indexOf("id=\"title\"") > -1) {
                stock = parseStockMetaInfo(l);
            }

            if (l.indexOf("class=\"dotborder\"") > -1) {
                List<HolderStat> hsList = parseHolderStat(l, stock.getId());
                stock.setHolderStats(hsList);
            }
        }
        return stock;
    }

    private Stock parseStockMetaInfo(String _titleLine) {
        Matcher m = titlePattern.matcher(_titleLine);
        if (m.find()) {
            Stock stock = new Stock();
            stock.setId(m.group(2));
            stock.setName(m.group(1));
            return stock;
        } else {
            throw new RuntimeException("[Error] Can NOT get stock meta info. Title line might be changed.");
        }
    }

    private List<HolderStat> parseHolderStat(String _holderStatLine, String id) {
        ArrayList<HolderStat> list = new ArrayList<HolderStat>();
        String[] quarters = _holderStatLine.split("<tr");
        int i = 0;
        for (String q : quarters) {
            if (q.indexOf("<span class=\"font10\">") == -1) {
                continue;
            }

            HolderStat hs = new HolderStat();
            Matcher m = holderStatPattern.matcher(q);
            if (m.find()) {
                String reportDate = m.group(1);
                String holderNum = m.group(2);
                String avg = m.group(3);
                String delta = m.group(4);
                String totalShare = m.group(5);
                String circulating = m.group(6);

                StringBuilder sb = new StringBuilder();
                sb.append(holderNum).append("|");
                sb.append(avg).append("|");
                sb.append(delta).append("|");
                sb.append(totalShare).append("|");
                sb.append(circulating).append("|");
                if (sb.indexOf("-|") > -1) {
                    continue;
                }

                int[] yAndQ = parseReportDate(reportDate);
                hs.setYear(yAndQ[0]);
                hs.setQuarter(yAndQ[1]);

                hs.setHolderNum(parseInt(holderNum));
                hs.setAverageHolding(parseInt(avg));
                hs.setTotalShare(parseInt(totalShare));
                hs.setCirculatingShare(parseInt(circulating));

                delta = delta.replace("%", "");
                delta = delta.replace(",", "");
                float fdelta = NumberUtils.createFloat(delta);
                if (fdelta < -70 || fdelta > 300) {

                    continue;
                }
                hs.setDelta(delta);

                hs.setStockId(id);
                hs.setId(id + ":" + hs.getYear() + ":" + hs.getQuarter());
                list.add(hs);
            } else {
                throw new RuntimeException(
                        "[Error] Can NOT parse stock holder info. Stock holder line might be changed.");
            }
        }
        return list;
    }

    private static final String Q1 = "年第1季";
    private static final String Q2 = "年中期";
    private static final String Q2s = "年第2季";
    private static final String Q3 = "年前3季";
    private static final String Q4 = "年年度";
    private static final String[] REPORT_QS = new String[] { Q1, Q2, Q2s, Q3, Q4 };
    private static final int[] REPORT_QS_NUM = new int[] { 1, 2, 2, 3, 4 };

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
            throw new RuntimeException("[Error] Report Date parse failed. " + r);
        }
        return new int[] { year, quarter };
    }

    private int parseInt(String s) {
        s = s.replace(",", "");
        if (s.indexOf(".") > -1) {
            s = s.substring(0, s.indexOf("."));
        }
        return Integer.parseInt(s);
    }
}
