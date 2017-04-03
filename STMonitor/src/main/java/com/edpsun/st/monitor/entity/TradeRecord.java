package com.edpsun.st.monitor.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class TradeRecord {
    @NonNull
    private String id;

    @NonNull
    private LocalDate date;

    @NonNull
    private Double price;

    @NonNull
    private Double priceChange;

    @NonNull
    private Double priceChangeRate;

    @NonNull
    private Double volume;

    @NonNull
    private Double turnoverRate;

    @NonNull
    private Double open;

    @NonNull
    private Double high;

    @NonNull
    private Double low;
}
