package com.gmail.edpsun.hystock.inbound.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;

@Component("tecentParser")
public class TecentParser implements Parser {
    public static Logger LOGGER = Logger.getLogger(TecentParser.class);
    String titleLine = "shtml\" target=\"_top\">(.*) (.*)</a>";
    Pattern titlePattern = Pattern.compile(titleLine);

    String holderStatLine = "<tdclass=\"nobor_l\">(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td>";
    Pattern holderStatPattern = Pattern.compile(holderStatLine);

    public String getTargetURL(String id) {
        return String.format("http://stock.finance.qq.com/corp1/stk_holder_count.php?zqdm=%s", id);
    }

    public Stock parse(String content) {
        if (content == null) {
            throw new RuntimeException("Content is null from Tecent.");
        }

        if (content.indexOf(">- 股东户数<") == -1) {
            throw new RuntimeException("Parser cannot work.");
        }

        String payload = content.replace("\r", "");
        String[] lines = payload.split("\n");
        Stock stock = null;
        StringBuilder sb = new StringBuilder();
        int dataline = 0;
        List<HolderStat> hsList = new ArrayList<HolderStat>();
        for (String l : lines) {
            if (l.indexOf(">- 股东户数<") > -1) {
                stock = parseStockMetaInfo(l);
                continue;
            }

            if (l.indexOf("class=\"nobor_l\"") > -1) {
                dataline = 5;
            }
            if (dataline > 0) {
                sb.append(l);
                dataline--;
            }
            if (dataline == 0 && sb.length() > 0) {
                HolderStat h = parseHolderStat(sb.toString().replace(" ", ""), stock.getId());
                if (h != null) {
                    hsList.add(h);
                    if (hsList.size() >= 2) {
                        calculateDelta(hsList.get(hsList.size() - 2), hsList.get(hsList.size() - 1));
                    }
                }
                sb.setLength(0);
            }
        }

        for (HolderStat h0 : hsList) {
            LOGGER.debug(h0.getYear() + "-" + h0.getQuarter() + " => " + h0.getDelta());
        }
        stock.setHolderStats(hsList);
        return stock;
    }

    private void calculateDelta(HolderStat h1, HolderStat h2) {
        float fdelta = (h1.getAverageHolding() - h2.getAverageHolding() + 0.0f) / h2.getAverageHolding();
        float f2 = (float) (Math.round(fdelta * 10000)) / 100;
        h1.setDelta("" + f2);
        h2.setDelta("0.0");
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

    private HolderStat parseHolderStat(String quarterInfo, String id) {
        if (quarterInfo.indexOf("nobor_l") == -1 || quarterInfo.indexOf("style") > -1) {
            return null;
        }

        HolderStat hs = new HolderStat();
        Matcher m = holderStatPattern.matcher(quarterInfo);
        if (m.find()) {
            String reportDate = m.group(1);
            String holderNum = m.group(2);
            String avg = m.group(3);
            String totalShare = m.group(4);
            String circulating = m.group(5);

            StringBuilder sb = new StringBuilder();
            sb.append(holderNum).append("|");
            sb.append(avg).append("|");
            sb.append(totalShare).append("|");
            sb.append(circulating).append("|");
            if (sb.indexOf("--|") > -1) {
                return null;
            }

            int[] yAndQ = null;
            try {
                yAndQ = parseReportDate(reportDate);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                return null;
            }
            hs.setYear(yAndQ[0]);
            hs.setQuarter(yAndQ[1]);

            hs.setHolderNum(parseInt(holderNum));
            hs.setAverageHolding(parseInt(avg));
            hs.setTotalShare(parseInt(totalShare));
            hs.setCirculatingShare(parseInt(circulating));

            hs.setStockId(id);
            hs.setId(id + ":" + hs.getYear() + ":" + hs.getQuarter());
        } else {
            throw new RuntimeException("[Error] Can NOT parse stock holder info. Stock holder line might be changed.");
        }

        return hs;
    }

    private static final String Q1 = "-03-31";
    private static final String Q2 = "-06-30";
    private static final String Q3 = "-09-30";
    private static final String Q4 = "-12-31";
    private static final String[] REPORT_QS = new String[] { Q1, Q2, Q3, Q4 };
    private static final int[] REPORT_QS_NUM = new int[] { 1, 2, 3, 4 };

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
