package com.edpsun.st.monitor.data.sohu;

import com.edpsun.st.monitor.configuration.ObjectMapperProvider;
import com.edpsun.st.monitor.entity.TradeRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ObjectMapperProvider.class})
public class SohuDataParserTest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Autowired
    private ObjectMapperProvider objectMapperProvider;

    private SohuDataParser underTest;

    @Before
    public void before() throws Exception {
        underTest = new SohuDataParser(objectMapperProvider.objectMapper());
    }

    @Test
    public void successfullyParse() {
        final List<TradeRecord> actualData = underTest.parse(testData());
        Assert.assertEquals("First trade record is not as expected.", firstTradeRecord(), actualData.get(0));

        final LocalDate dateOfSecondTradeRecord = LocalDate.parse("2017-03-30", FORMATTER);
        Assert.assertEquals("Second trade record date is not as expected.", dateOfSecondTradeRecord, actualData.get(1).getDate());
        Assert.assertEquals("Second trade record turnover rate is not as expected.", Double.valueOf(2.21d), actualData.get(1).getTurnoverRate());
    }

    private TradeRecord firstTradeRecord() {
        return TradeRecord.builder().id("002214").date(LocalDate.parse("2017-03-31", FORMATTER)).
                price(12.66).
                priceChange(0.31).
                priceChangeRate(2.51).
                volume(152348d).
                turnoverRate(4.18).
                open(12.31).
                high(12.75).
                low(12.25).build();
    }

    private String testData() {
        return "[\n" +
                "  {\n" +
                "    \"code\": \"cn_002214\",\n" +
                "    \"hq\": [\n" +
                "      [\n" +
                "        \"2017-03-31\",\n" +
                "        \"12.31\",\n" +
                "        \"12.66\",\n" +
                "        \"0.31\",\n" +
                "        \"2.51%\",\n" +
                "        \"12.25\",\n" +
                "        \"12.75\",\n" +
                "        \"152348\",\n" +
                "        \"19179.16\",\n" +
                "        \"4.18%\"\n" +
                "      ],\n" +
                "      [\n" +
                "        \"2017-03-30\",\n" +
                "        \"12.29\",\n" +
                "        \"12.35\",\n" +
                "        \"0.08\",\n" +
                "        \"0.65%\",\n" +
                "        \"11.98\",\n" +
                "        \"12.45\",\n" +
                "        \"80487\",\n" +
                "        \"9820.33\",\n" +
                "        \"2.21%\"\n" +
                "      ]\n" +
                "    ],\n" +
                "    \"stat\": [\n" +
                "      \"累计:\",\n" +
                "      \"2017-03-30至2017-03-31\",\n" +
                "      \"0.39\",\n" +
                "      \"3.18%\",\n" +
                "      11.98,\n" +
                "      12.75,\n" +
                "      232835,\n" +
                "      28999.49,\n" +
                "      \"6.39%\"\n" +
                "    ],\n" +
                "    \"status\": 0\n" +
                "  }\n" +
                "]";
    }
}
