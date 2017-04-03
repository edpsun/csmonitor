package com.edpsun.st.monitor.data.sohu;

import com.edpsun.st.monitor.data.DataParser;
import com.edpsun.st.monitor.entity.TradeRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SohuDataParser implements DataParser<String, TradeRecord> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @NonNull
    private ObjectMapper objectMapper;

    @Override
    public List<TradeRecord> parse(@NonNull final String source) {
        final JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(source);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final String code = getCode(jsonNode);

        final JsonNode hqData = jsonNode.findValue("hq");
        final List<TradeRecord> list = parse(code, hqData);

        return list;
    }

    private List<TradeRecord> parse(final String code, final JsonNode hqData) {
        final List<TradeRecord> list = new ArrayList<>();
        for (final JsonNode oneDayData : hqData) {
            list.add(parseTradeRecord(code, oneDayData));
        }
        return list;
    }

    private TradeRecord parseTradeRecord(final String code, final JsonNode oneDayData) {
        final LocalDate date = LocalDate.parse(oneDayData.get(0).asText(), FORMATTER);

        final Double price = oneDayData.get(2).asDouble();
        final Double priceChange = oneDayData.get(3).asDouble();
        final Double priceChangeRate = Double.valueOf(oneDayData.get(4).asText().replace("%", ""));
        final Double volume = oneDayData.get(7).asDouble();
        final Double turnoverRate = Double.valueOf(oneDayData.get(9).asText().replace("%", ""));
        final Double open = oneDayData.get(1).asDouble();
        final Double high = oneDayData.get(6).asDouble();
        final Double low = oneDayData.get(5).asDouble();
        final TradeRecord tr = TradeRecord.builder()
                .id(code)
                .date(date)
                .price(price)
                .priceChange(priceChange)
                .priceChangeRate(priceChangeRate)
                .volume(volume)
                .turnoverRate(turnoverRate)
                .open(open)
                .high(high)
                .low(low)
                .build();

        return tr;
    }

    private String getCode(final JsonNode jsonNode) {
        return jsonNode.findValue("code").asText().replace("cn_", "");
    }
}
