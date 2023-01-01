package com.gmail.edpsun.hystock.inbound.parser;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.inbound.collect.DataRetriever;
import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component("tdxParser")
public class TdxParser implements Parser, ContextAware {
    private static final String Q1 = "-03-31";
    private static final String Q2 = "-06-30";
    private static final String Q3 = "-09-30";
    private static final String Q4 = "-12-31";
    private static final String[] REPORT_QS = new String[]{Q1, Q2, Q3, Q4};
    private static final int[] REPORT_QS_NUM = new int[]{1, 2, 3, 4};

    private static final String URL = "file:%s/%s/%s-Q%s";
    private static final Pattern LINE_PATTERN = Pattern.compile("│(\\d\\d\\d\\d)-(03-31|06-30|09-30|12-31)│.*");
    private InboundContext ctx;

    // example - /opt/data/stockdata/data/st_info_extractor/stock_data_f10
    private final String stockDataFolder;

    public TdxParser() {
        this(System.getenv("TDX_DATA_FOLDER"));
    }

    public TdxParser(String stockDataFolder) {
        this.stockDataFolder = stockDataFolder;
    }

    @Override
    public String getEncoding() {
        return DataRetriever.GBK;
    }

    @Override
    public String getTargetURL(String id) {
        if (stockDataFolder == null || !new File(stockDataFolder).exists()) {
            throw new RuntimeException(String.format("Error - data folder [%s] does not exist", stockDataFolder));
        }
        return String.format(URL, stockDataFolder, id, ctx.getQuarter().getYear(), ctx.getQuarter().getQuarter());
    }

    @Override
    public Stock parse(String id, String name, String content) {
        final Stock stock = new Stock();
        stock.setId(id);
        stock.setName(name);


        final List<String> stockInfoLines = extractLines(content);

        if (stockInfoLines.size() > 0) {
            final List<HolderStat> hsList = stockInfoLines.stream().map((line) -> {
                return parseLine(line, id);
            }).collect(Collectors.toList());

            stock.setHolderStats(hsList);
        }

        return stock;
    }

    @Override
    public void setContext(InboundContext ctx) {
        this.ctx = ctx;
    }

    private HolderStat parseLine(final String line, final String id) {
        final HolderStat hs = new HolderStat();

        // │2012-12-31│   16275│    4525│   38.51│   5108.24│     11.86│  非常集中│ 12.61│
        String[] columns = line.split("│");
        final String reportDate = columns[1].trim();
        final String holderNum = columns[2].trim();
        final String avgStockNumberPerHolder = columns[5].trim();
        // avg amount change rate
        final String delta = columns[6].trim();

        final int[] yAndQ = parseReportDate(reportDate);
        if (yAndQ.length == 0) {
            throw new RuntimeException("Cannot parse report date: " + reportDate);
        }

        hs.setYear(yAndQ[0]);
        hs.setQuarter(yAndQ[1]);

        hs.setHolderNum(parseLong(holderNum));
        hs.setAverageHolding(parseLong(avgStockNumberPerHolder));

        final long totalShare = (long) (parseLong(holderNum) * parseLong(avgStockNumberPerHolder)/10000);
        final long circulatingShare = totalShare; // no such field in GF TDX

        hs.setTotalShare(totalShare);
        hs.setCirculatingShare(circulatingShare);

        final float fdelta = NumberUtils.createFloat(delta);
        hs.setDelta("" + fdelta);
        hs.setStockId(id);
        hs.setId(id + ":" + hs.getYear() + ":" + hs.getQuarter());
        return hs;
    }

    private List<String> extractLines(final String content) {
        final List<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(content)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (LINE_PATTERN.matcher(line).matches() && !line.contains("---")) {
                    lines.add(line);
                }
            }
        }

        return lines;
    }

    private int[] parseReportDate(final String r) {
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
